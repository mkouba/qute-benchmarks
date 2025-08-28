package io.quarkus.qute.benchmark;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type.Kind;
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
import io.quarkus.qute.TemplateExtension;
import io.quarkus.qute.generator.ExtensionMethodGenerator;
import io.quarkus.qute.generator.ValueResolverGenerator;
import io.quarkus.qute.generator.ValueResolverGenerator.Builder;

@Fork(3)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ClassGeneration {
    
    private static DotName TEMPLATE_EXTENSION = DotName.createSimple(TemplateExtension.class);

    private Index index;
    private List<ClassInfo> dataClasses;
    private List<MethodInfo> extensionMethods;
    private Class<?> classOutputClazz;
    private Object classOutput;
    private Method valueResolverSetClassOutput;

    @Setup
    public void setup() throws Exception {
        Indexer indexer = new Indexer();

        try (Stream<Path> appClasses = Files.walk(Path.of("target/classes/io/quarkus/qute/benchmark/app"))) {
            List<Path> classFiles = appClasses
                    .filter(it -> it.toString().endsWith(".class"))
                    .toList();
            for (Path classFile : classFiles) {
                if (!classFile.getFileName().endsWith("_ValueResolver")) {
                    try (InputStream in = Files.newInputStream(classFile)) {
                        indexer.index(in);
                    }
                }
            }
        }

        List<DotName> extensionClassNames = new ArrayList<>();

        indexer.indexClass(String.class);
        indexer.indexClass(LocalDateTime.class);
        indexer.indexClass(HashMap.class);

        index = indexer.complete();

        dataClasses = new ArrayList<>();
        for (ClassInfo clazz : index.getKnownClasses()) {
            if (clazz.isSynthetic()) {
                continue;
            }
            String clazzName = clazz.name().toString();
            if (clazzName.startsWith("io.quarkus.qute.benchmark.app")) {
                if (clazzName.contains("Pojo") || clazzName.startsWith("java.")) {
                    dataClasses.add(clazz);
                } else if (clazzName.contains("ExtensionMethods")) {
                    extensionClassNames.add(clazz.name());
                }
            }
        }

        extensionMethods = new ArrayList<>();
        for (DotName extensionClassName : extensionClassNames) {
            for (MethodInfo method : index.getClassByName(extensionClassName).methods()) {
                if (!method.isConstructor()
                        && !method.isStaticInitializer()
                        && !method.isSynthetic()
                        && Modifier.isStatic(method.flags())
                        && !Modifier.isPrivate(method.flags())
                        && method.returnType().kind() != Kind.VOID) {
                    AnnotationInstance extensionAnnotation = method.annotation(TEMPLATE_EXTENSION);
                    AnnotationValue namespaceValue = extensionAnnotation != null ? extensionAnnotation.value("namespace")
                            : null;
                    if (namespaceValue == null) {
                        // We cannot benchmark namespace ext methods due to API changes
                        extensionMethods.add(method);
                    }
                }
            }
        }

        Class<Builder> builderClass = ValueResolverGenerator.Builder.class;
        valueResolverSetClassOutput = Arrays.stream(builderClass.getDeclaredMethods())
                .filter(m -> m.getName().equals("setClassOutput")).findFirst().get();
        classOutputClazz = valueResolverSetClassOutput.getParameterTypes()[0];
        classOutput = classOutputClazz.equals(ClassOutput.class)
                ? SimpleBenchmarkBase.gizmo1ClassOutput()
                : SimpleBenchmarkBase.gizmo2ClassOutput();
    }

    @Benchmark
    public Set<String> generate() throws Exception {
        return generateClasses();
    }

    Set<String> generateClasses() throws Exception {
        Set<String> ret = new HashSet<>();
        ret.addAll(generateDataClassesValueResolvers(index, dataClasses));
        ret.addAll(generateExtensionMethodValueResolvers(index, extensionMethods));
        return ret;
    }

    Set<String> generateExtensionMethodValueResolvers(IndexView index, List<MethodInfo> extensionMethods) throws Exception {
        Constructor<ExtensionMethodGenerator> constructor = ExtensionMethodGenerator.class.getConstructor(IndexView.class,
                classOutputClazz);
        ExtensionMethodGenerator extensionMethodGenerator = constructor.newInstance(index, classOutput);
        for (MethodInfo extensionMethod : extensionMethods) {
            // Workaround a NPE with matchNames
            List<String> matchNames = List.of();
            AnnotationInstance extensionAnnotation = extensionMethod.annotation(DotName.createSimple(TemplateExtension.class));
            if (extensionAnnotation != null) {
                AnnotationValue matchNamesValue = extensionAnnotation.value("matchNames");
                if (matchNamesValue != null) {
                    matchNames = new ArrayList<>();
                    for (String name : matchNamesValue.asStringArray()) {
                        matchNames.add(name);
                    }
                }
            }
            extensionMethodGenerator.generate(extensionMethod, null, matchNames, null, null);
        }
        return extensionMethodGenerator.getGeneratedTypes();
    }

    Set<String> generateDataClassesValueResolvers(IndexView index, List<ClassInfo> dataClasses)
            throws Exception {

        ValueResolverGenerator.Builder builder = ValueResolverGenerator.builder().setIndex(index);
        valueResolverSetClassOutput.invoke(builder, classOutput);

        // This method is used in 1.9+
        Method addClass = null;
        try {
            addClass = ValueResolverGenerator.Builder.class.getDeclaredMethod("addClass", ClassInfo.class);
        } catch (NoSuchMethodException ignored) {
        }

        if (addClass != null) {
            // builder.addClass(...)
            for (ClassInfo dataClass : dataClasses) {
                try {
                    addClass.invoke(builder, dataClass);
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

                for (ClassInfo dataClass : dataClasses) {
                    generate.invoke(generator, dataClass);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        return generator.getGeneratedTypes();
    }
}
