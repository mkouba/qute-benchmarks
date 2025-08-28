package io.quarkus.qute.benchmark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import io.quarkus.qute.ValueResolver;

public class ExpectedOutputTest {

    @BeforeClass
    public static void beforeClass() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void testHelloSimple() throws Exception {
        HelloSimple benchmark = new HelloSimple();
        benchmark.setup();
        assertOutput("/expected_hello.html", benchmark.render());
    }

    @Test
    public void testLoop() throws Exception {
        Loop15 benchmark = new Loop15();
        benchmark.setup();
        assertOutput("/expected_loop.html", benchmark.render());
    }

    @Test
    public void testIfComplex() throws Exception {
        IfComplex benchmark = new IfComplex();
        benchmark.setup();
        assertOutput("/expected_if-complex.html", benchmark.render());
    }

    @Test
    public void testIfSimple() throws Exception {
        IfSimple benchmark = new IfSimple();
        benchmark.setup();
        assertOutput("/expected_if-simple.html", benchmark.render());
    }

    @Test
    public void testWhen() throws Exception {
        try {
            // "when" was added in 1.11
            getClass().getClassLoader().loadClass("io.quarkus.qute.WhenSectionHelper");
            When benchmark = new When();
            benchmark.setup();
            assertOutput("/expected_when.html", benchmark.render());
        } catch (ClassNotFoundException ignored) {
        }
    }

    @Test
    public void testNamedResolver() throws Exception {
        NameResolver benchmark = new NameResolver();
        benchmark.setup();
        assertOutput("/expected_name-resolver.html", benchmark.render());
    }

    @Test
    public void testIncludeSimple() throws Exception {
        IncludeSimple benchmark = new IncludeSimple();
        benchmark.setup();
        assertOutput("/expected_include-simple.html", benchmark.render());
    }

    @Test
    public void testLetSimple() throws Exception {
        LetSimple benchmark = new LetSimple();
        benchmark.setup();
        assertOutput("/expected_let-simple.html", benchmark.render());
    }

    @Test
    public void testLetComplex() throws Exception {
        LetComplex benchmark = new LetComplex();
        benchmark.setup();
        assertOutput("/expected_let-complex.html", benchmark.render());
    }

    @Test
    public void testPojoResolver() throws Exception {
        JavaBeanValueResolver benchmark = new JavaBeanValueResolver();
        benchmark.setup();
        assertOutput("/expected_javabean.html", benchmark.render());
    }

    @Test
    public void testReflect() throws Exception {
        Reflect benchmark = new Reflect();
        benchmark.setup();
        assertOutput("/expected_reflect.html", benchmark.render());
    }

    @Test
    public void testClassGeneration() throws Exception {
        ClassGeneration benchmark = new ClassGeneration();
        benchmark.setup();
        Set<String> generatedClasses = benchmark.generate();
        for (String generatedClass : generatedClasses) {
            Class<?> clazz = ExpectedOutputTest.class.getClassLoader().loadClass(generatedClass);
            assertTrue(ValueResolver.class.isAssignableFrom(clazz));
        }
        System.out.println("Classes generated: %s".formatted(generatedClasses.size()));
    }

    private void assertOutput(String expectedOutputFile, String actual) throws IOException {
        assertEquals(readExpectedOutputResource(expectedOutputFile), actual.replaceAll("\\s", ""));
    }

    private String readExpectedOutputResource(String expectedOutputFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(ExpectedOutputTest.class.getResourceAsStream(expectedOutputFile)))) {
            for (;;) {
                String line = in.readLine();
                if (line == null)
                    break;
                builder.append(line);
            }
        }
        // Remove all whitespaces
        return builder.toString().replaceAll("\\s", "");
    }

}
