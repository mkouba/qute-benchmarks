package io.quarkus.qute.benchmark;

import java.util.Collections;

import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(batchSize = 100)
@Measurement(batchSize = 100)
public class IncludeSimple extends BenchmarkBase {

    @Setup
    public void setup() throws Exception {
        super.setup();
        engine.getTemplate("base.html");
    }
    
    protected String getTemplateName() {
        return "include-simple.html";
    }

    @Override
    protected Object getTestData() {
        return Collections.emptyMap();
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("Qute Benchmark - Simple Include")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

}
