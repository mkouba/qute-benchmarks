package io.quarkus.qute.benchmark;

import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.annotations.Setup;

public class Reflect extends SimpleBenchmarkBase {

    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<String, Object>();
        testData.put("items", Loop15.generateItems(20));
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
