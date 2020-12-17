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
