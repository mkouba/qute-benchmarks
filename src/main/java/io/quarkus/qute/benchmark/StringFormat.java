package io.quarkus.qute.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.quarkus.qute.Qute;

@Warmup(batchSize = 1000)
@Measurement(batchSize = 1000)
@State(Scope.Benchmark)
public class StringFormat extends BenchmarkBase {

    @Setup
    public void setup() throws Exception {
        Qute.setEngine(null);
        Qute.enableCache();
    }

    @Benchmark
    public String render() {
        String result = String.format("Hello %s!", "Pete");
        if (!result.equals("Hello Pete!")) {
            throw new AssertionError("Incorrect result: " + result);
        }
        return result;
    }

}
