package io.quarkus.qute.benchmark;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import io.quarkus.qute.EngineBuilder;
import io.quarkus.qute.EvalContext;
import io.quarkus.qute.NamespaceResolver;
import io.quarkus.qute.benchmark.data.Item;

@Warmup(batchSize = 100)
@Measurement(batchSize = 100)
public class NameResolver extends BenchmarkBase {

    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<String, Object>();
        testData.put("name", "Foo");
    }

    @Override
    protected void customizeEngine(EngineBuilder builder) {
        Item item = Loop.generateItem(6);
        builder.addNamespaceResolver(new NamespaceResolver() {

            @Override
            public CompletionStage<Object> resolve(EvalContext context) {
                return CompletableFuture.completedFuture(item);
            }

            @Override
            public String getNamespace() {
                return "inject";
            }

        });
    }

    protected String getTemplateName() {
        return "name-resolver.html";
    }

    @Override
    protected Object getTestData() {
        return testData;
    }

    @Override
    protected void assertResult(String result) {
        if (!result.contains("the price is 6000!")) {
            throw new AssertionError("Incorrect result: " + result);
        }
    }

}
