package io.quarkus.qute.benchmark;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

public class ExpectedOutputTest {

    @BeforeClass
    public static void beforeClass() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void testHello() throws Exception {
        Hello benchmark = new Hello();
        benchmark.setup();
        assertOutput("/expected_hello.html", benchmark.render());
    }

    @Test
    public void testLoop() throws Exception {
        Loop benchmark = new Loop();
        benchmark.setup();
        assertOutput("/expected_loop.html", benchmark.render());
    }

    @Test
    public void testIfComplexCondition() throws Exception {
        IfComplexCondition benchmark = new IfComplexCondition();
        benchmark.setup();
        assertOutput("/expected_if-complex-condition.html", benchmark.render());
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
