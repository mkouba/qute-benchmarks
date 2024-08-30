package io.quarkus.qute.benchmark;

import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(batchSize = 100)
@Measurement(batchSize = 100)
public class IfSimple extends SimpleBenchmarkBase {
    
    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<>();
        testData.put("item", Loop15.generateItem(6));
    }

    protected String getTemplateName() {
        return "if-simple.html";
    }

    @Override
    protected Object getTestData() {
        return testData;
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("The price is too high.")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

}
