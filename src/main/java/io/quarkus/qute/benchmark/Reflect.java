package io.quarkus.qute.benchmark;

import java.util.Map;

import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(batchSize = 100)
@Measurement(batchSize = 100)
public class Reflect extends SimpleBenchmarkBase {

    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = Map.of("foo", Loop15.generateItem(42), "bar", Loop15.generateItem(24));
    }

    protected String getTemplateName() {
        return "reflect.html";
    }

    @Override
    protected Object getTestData() {
        return testData;
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("Reflection")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

}
