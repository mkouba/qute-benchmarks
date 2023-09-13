package io.quarkus.qute.benchmark;

import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(batchSize = 100)
@Measurement(batchSize = 100)
public class IfComplexCondition extends SimpleBenchmarkBase {
    
    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<>();
        testData.put("item", Loop.generateItem(6));
    }

    protected String getTemplateName() {
        return "if-complex-condition.html";
    }

    @Override
    protected Object getTestData() {
        return testData;
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("Dear sir")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

}
