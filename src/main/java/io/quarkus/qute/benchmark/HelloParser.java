package io.quarkus.qute.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;

import io.quarkus.qute.Engine;

public class HelloParser extends BenchmarkBase {

    private Engine engine;

    @Setup
    public void setup() throws Exception {
        engine = Engine.builder().addDefaults().build();
    }

    @Benchmark
    public String parse() {
        String result = engine.parse("Hello {name}!").data("name", "Foo").render();
        if (!result.equals("Hello Foo!")) {
            throw new AssertionError("Incorrect result: " + result);
        }
        return result;
    }

}
