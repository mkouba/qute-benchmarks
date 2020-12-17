package io.quarkus.qute.benchmark;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openjdk.jmh.annotations.Setup;

import io.quarkus.qute.benchmark.data.Item;

public class Loop extends BenchmarkBase {

    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<String, Object>();
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 15; i++) {
            items.add(generateItem(i));
        }
        testData.put("items", items);
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

    protected Item generateItem(int idx) {
        Item item = new Item();
        item.setId(Long.valueOf(idx * 10));
        item.setName("" + idx);
        item.setPrice(new BigDecimal(idx * 1000));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        item.setCreated(cal.getTime());
        item.setDescription(String.format("Item %s with price %s (created at %s)", item.getId(), item.getPrice(),
                item.getCreated().getTime()));
        return item;
    }

}
