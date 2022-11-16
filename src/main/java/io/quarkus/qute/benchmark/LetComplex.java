package io.quarkus.qute.benchmark;

import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(batchSize = 100)
@Measurement(batchSize = 100)
public class LetComplex extends BenchmarkBase {
    
    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<>();
        testData.put("item", Loop.generateItem(6));
        testData.put("baz", true);
    }

    protected String getTemplateName() {
        return "let-complex.html";
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("1::true::6")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

    @Override
    protected Object getTestData() {
        return testData;
    }

}
