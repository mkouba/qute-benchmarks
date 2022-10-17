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

import io.quarkus.qute.Qute;

@Fork(3)
@Warmup(iterations = 3, time = 1, batchSize = 1000)
@Measurement(iterations = 3, time = 2, batchSize = 1000)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class QuteFmtCache {

    @Setup
    public void setup() throws Exception {
        Qute.setEngine(null);
        Qute.enableCache();
    }

    @Benchmark
    public String render() {
        String result = Qute.fmt("Hello {}!", "Pete");
        if (!result.equals("Hello Pete!")) {
            throw new AssertionError("Incorrect result: " + result);
        }
        return result;
    }

}
