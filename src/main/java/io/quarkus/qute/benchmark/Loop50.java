package io.quarkus.qute.benchmark;

import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.annotations.Setup;

public class Loop50 extends BenchmarkBase {

    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<String, Object>();
        testData.put("items", Loop.generateItems(50));
        testData.put("name", "Foo");
    }

    protected String getTemplateName() {
        return "loop.html";
    }

    @Override
    protected Object getTestData() {
        return testData;
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("Dear Foo")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

}
