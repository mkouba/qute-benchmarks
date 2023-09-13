package io.quarkus.qute.benchmark;

import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(batchSize = 100)
@Measurement(batchSize = 100)
public class LetSimple extends SimpleBenchmarkBase {

    protected String getTemplateName() {
        return "let-simple.html";
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("<strong>true</strong>")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

    @Override
    protected Object getTestData() {
        return "foo";
    }

}
