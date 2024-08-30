package io.quarkus.qute.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.quarkus.gizmo.ClassOutput;
import io.quarkus.qute.Engine;
import io.quarkus.qute.EngineBuilder;
import io.quarkus.qute.ReflectionValueResolver;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateLocator;
import io.quarkus.qute.ValueResolver;
import io.quarkus.qute.Variant;
import io.quarkus.qute.benchmark.data.Item;
import io.quarkus.qute.benchmark.data.JavaBean;
import io.quarkus.qute.generator.ValueResolverGenerator;

@Fork(3)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public abstract class SimpleBenchmarkBase {

    static final Class<?>[] DATA_CLASSES = { Item.class, JavaBean.class, JavaBean.State.class };

    protected Template template;
    protected Engine engine;

    @Benchmark
    public String render() {
        String result = template.render(getTestData());
        assertResult(result);
        return result;
    }

    @Setup
    public void setup() throws Exception {

        Set<String> types = generateClasses();

        // Build engine
        EngineBuilder builder = Engine.builder();

        builder.addDefaults().addValueResolver(new ReflectionValueResolver());

        for (String resolverClass : types) {
            builder.addValueResolver(createResolver(resolverClass.replace("/", ".")));
        }

        builder.addLocator(new TemplateLocator() {

            @Override
            public Optional<TemplateLocation> locate(String id) {
                InputStream in = SimpleBenchmarkBase.class.getResourceAsStream("/templates/" + id);
                if (in == null) {
                    return Optional.empty();
                }
                return Optional.of(new TemplateLocation() {

                    @Override
                    public Reader read() {
                        return new InputStreamReader(in);
                    }

                    @Override
                    public Optional<Variant> getVariant() {
                        return Optional.empty();
                    }
                });
            }
        });

        customizeEngine(builder);
        engine = builder.build();

        template = engine.getTemplate(getTemplateName());
    }

    protected abstract Object getTestData();

    protected abstract String getTemplateName();

    protected void assertResult(String result) {
        // Noop
    }

    protected void customizeEngine(EngineBuilder builder) {
        // Noop
    }

    ValueResolver createResolver(String resolverClassName)
            throws IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        try {
            ClassLoader cl = Thread.currentThread()
                    .getContextClassLoader();
            if (cl == null) {
                cl = SimpleBenchmarkBase.class.getClassLoader();
            }
            Class<?> resolverClazz = cl.loadClass(resolverClassName);
            if (ValueResolver.class.isAssignableFrom(resolverClazz)) {
                return (ValueResolver) resolverClazz.getDeclaredConstructor().newInstance();
            }
            throw new IllegalStateException("Not a value resolver: " + resolverClassName);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to create resolver: " + resolverClassName, e);
        }
    }

    static Index index(Class<?>... classes) throws IOException {
        Indexer indexer = new Indexer();
        for (Class<?> clazz : classes) {
            try (InputStream stream = SimpleBenchmarkBase.class.getClassLoader()
                    .getResourceAsStream(clazz.getName().replace('.', '/') + ".class")) {
                indexer.index(stream);
            }
        }
        return indexer.complete();
    }

    static byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    Set<String> generateClasses() throws IOException {
        IndexView index = index(DATA_CLASSES);
        ClassOutput classOutput = new ClassOutput() {

            @Override
            public void write(String name, byte[] data) {
                try {
                    File dir = new File("target/classes/", name.substring(0, name.lastIndexOf("/")));
                    dir.mkdirs();
                    File output = new File("target/classes/", name + ".class");
                    Files.write(output.toPath(), data);
                } catch (IOException e) {
                    throw new IllegalStateException("Cannot dump the class: " + name, e);
                }

            }
        };

        ValueResolverGenerator.Builder builder = ValueResolverGenerator.builder().setIndex(index).setClassOutput(classOutput);

        Class<?> builderClass = builder.getClass();

        // This method is used in 1.9+
        Method addClass = null;
        try {
            addClass = builderClass.getDeclaredMethod("addClass", ClassInfo.class);
        } catch (NoSuchMethodException ignored) {
        }

        if (addClass != null) {
            // builder.addClass(...)
            for (int i = 0; i < DATA_CLASSES.length; i++) {
                ClassInfo clazz = index.getClassByName(DotName.createSimple(DATA_CLASSES[i].getName()));
                try {
                    addClass.invoke(builder, clazz);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }

        }

        ValueResolverGenerator generator = builder.build();

        if (addClass != null) {
            try {
                generator.getClass().getDeclaredMethod("generate").invoke(generator);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                    | SecurityException e) {
                throw new IllegalStateException(e);
            }
        } else {
            // generator.generate(clazz)
            try {
                Method generate = generator.getClass().getDeclaredMethod("generate", ClassInfo.class);

                for (int i = 0; i < DATA_CLASSES.length; i++) {
                    ClassInfo clazz = index.getClassByName(DotName.createSimple(DATA_CLASSES[i].getName()));
                    generate.invoke(generator, clazz);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        return generator.getGeneratedTypes();
    }

}
