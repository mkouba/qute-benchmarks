package io.quarkus.qute.benchmark;

import java.util.concurrent.TimeUnit;

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

import io.quarkus.qute.Engine;

@Fork(3)
@Warmup(iterations = 5, time = 1, batchSize = 1000)
@Measurement(iterations = 5, time = 2, batchSize = 1000)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class HelloParser {

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
