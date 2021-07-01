package io.quarkus.qute.benchmark;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.openjdk.jmh.annotations.Setup;

import io.quarkus.qute.benchmark.data.Item;

public class Loop extends BenchmarkBase {

    private Map<String, Object> testData;

    @Setup
    public void setup() throws Exception {
        super.setup();
        testData = new HashMap<String, Object>();
        testData.put("items", generateItems(15));
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

    static List<Item> generateItems(int size) {
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < size; i++) {
            items.add(generateItem(i));
        }
        return items;
    }

    static Item generateItem(int idx) {
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
        cal.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/Prague")));
        item.setCreated(cal.getTime());
        item.setDescription(String.format("Item %s with price %s (created at %s)", item.getId(), item.getPrice(),
                item.getCreated().getTime()));
        return item;
    }

}
