package io.quarkus.qute.benchmark;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.openjdk.jmh.annotations.Setup;

import io.quarkus.qute.benchmark.data.JavaBean;

/**
 * Test a generated value resolver generated for a pojo with 15 properties.
 */
public class JavaBeanValueResolver extends BenchmarkBase {

    private final AtomicLong idGenerator = new AtomicLong();

    @Setup
    public void setup() throws Exception {
        super.setup();
    }

    protected String getTemplateName() {
        return "javabean.html";
    }

    @Override
    protected Object getTestData() {
        return Map.of("bean", new JavaBean(idGenerator.getAndIncrement()));
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("Qute Benchmark - JavaBean Resolver")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

}
