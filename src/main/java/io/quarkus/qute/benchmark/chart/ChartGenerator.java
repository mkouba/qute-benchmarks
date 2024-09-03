package io.quarkus.qute.benchmark.chart;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.BitmapEncoder.BitmapFormat;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;

import io.quarkus.qute.Qute;

public class ChartGenerator {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            return;
        }

        List<File> files = new ArrayList<File>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            File file = new File(arg);
            if (!file.canRead()) {
                throw new IllegalArgumentException("Unable to read the data file: " + file);
            }
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                Collections.addAll(files, file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile() && !pathname.isHidden() && pathname.getName().endsWith(".json");
                    }
                }));
            }
        }

        if (files.isEmpty()) {
            return;
        }
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                // e.g. results-1.8.1.Final.json and results-1.10.3.Final.json
                String f1Name = f1.getName().substring(8);
                String f2Name = f2.getName().substring(8);
                if (f1Name.startsWith("999-SNAPSHOT")) {
                    return 1;
                }
                if (f2Name.startsWith("999-SNAPSHOT")) {
                    return -1;
                }
                String[] f1Parts = f1Name.split("\\.");
                String[] f2Parts = f2Name.split("\\.");

                int result = 0;
                for (int i = 0; i < 3; i++) {
                    int f1Val = Integer.parseInt(f1Parts[i]);
                    int f2Val = Integer.parseInt(f2Parts[i]);
                    result = Integer.compare(f1Val, f2Val);
                    if (result != 0) {
                        return result;
                    }
                }
                return result;
            }
        });

        Chart chart = new ChartBuilder().chartType(ChartType.Bar).width(1920).height(1080).title("Qute Microbenchmarks")
                .xAxisTitle("Benchmarks")
                .yAxisTitle("Ops/s").build();
        chart.getStyleManager().setXAxisTicksVisible(true);

        Set<String> allBenchmarks = new LinkedHashSet<String>();
        Map<String, Map<String, JsonObject>> seriesMap = new LinkedHashMap<String, Map<String, JsonObject>>();

        for (File file : files) {
            Map<String, JsonObject> benchmarkMap = new HashMap<String, JsonObject>();
            JsonArray series = readJsonElementFromFile(file).getAsJsonArray();
            for (JsonElement jsonElement : series) {
                JsonObject benchmark = jsonElement.getAsJsonObject();
                String benchmarkName = benchmark.get("benchmark").getAsString();
                benchmarkName = benchmarkName.substring(0, benchmarkName.lastIndexOf('.'));
                benchmarkName = benchmarkName.substring(benchmarkName.lastIndexOf('.') + 1, benchmarkName.length());
                allBenchmarks.add(benchmarkName);
                benchmarkMap.put(benchmarkName, benchmark);
            }
            seriesMap.put(file.getName().replace("results-", "").replace(".json", ""), benchmarkMap);
        }

        List<String> sortedBenchmarks = new ArrayList<String>(allBenchmarks);
        Collections.sort(sortedBenchmarks);

        for (Entry<String, Map<String, JsonObject>> series : seriesMap.entrySet()) {
            List<String> benchmarks = new ArrayList<String>();
            List<BigDecimal> scores = new ArrayList<BigDecimal>();
            List<BigDecimal> errors = new ArrayList<BigDecimal>();

            for (String benchmarkName : sortedBenchmarks) {
                benchmarks.add(benchmarkName);
                JsonObject benchmark = series.getValue().get(benchmarkName);
                if (benchmark != null) {
                    scores.add(benchmark.get("primaryMetric").getAsJsonObject().get("score").getAsBigDecimal());
                    errors.add(benchmark.get("primaryMetric").getAsJsonObject().get("scoreError").getAsBigDecimal());
                } else {
                    scores.add(BigDecimal.ZERO);
                    errors.add(BigDecimal.ZERO);
                }
            }
            chart.addSeries(series.getKey(), benchmarks, scores, errors);
        }

        // Print the summary table
        // 1st line
        System.out.println(Qute.fmt("{title}{#each versions}|{it}{/each}", Map.of("title", padRight("RESULTS SUMMARY"),
                "versions", seriesMap.keySet().stream().map(ChartGenerator::padRight).toList())));
        // 2nd line
        System.out
                .println(Qute.fmt("{sep}{#for i in versions.size}|{columns}{/for}",
                        Map.of("sep", "=".repeat(DEFAULT_PAD), "versions",
                                seriesMap.keySet().stream().map(ChartGenerator::padRight).toList(), "columns",
                                "Score     |Error |Diff   ")));
        // 3rd line - separator
        System.out.println(Qute.fmt("{sep}{#for i in versions}|{sep}{/for}", Map.of("sep", "-".repeat(DEFAULT_PAD), "versions",
                seriesMap.size())));
        // Data lines
        for (String benchmark : sortedBenchmarks) {
            BigDecimal lastScore = null;
            String[] scores = new String[seriesMap.size()];
            int idx = 0;
            for (Entry<String, Map<String, JsonObject>> series : seriesMap.entrySet()) {
                BigDecimal score = series.getValue().get(benchmark).get("primaryMetric").getAsJsonObject().get("score")
                        .getAsBigDecimal().setScale(0, RoundingMode.HALF_UP);
                BigDecimal scoreError = series.getValue().get(benchmark).get("primaryMetric").getAsJsonObject()
                        .get("scoreError").getAsBigDecimal().setScale(0, RoundingMode.HALF_UP);
                String diffStr = "";
                if (lastScore != null) {
                    BigDecimal diff = score.divide(lastScore, 2, RoundingMode.HALF_UP)
                            .subtract(BigDecimal.ONE)
                            .multiply(new BigDecimal(100))
                            .setScale(0, RoundingMode.HALF_UP);
                    diffStr = (diff.signum() == 1 ? "+" + diff.toString() : diff.toString()) + "%";
                }
                lastScore = score;
                scores[idx++] = padLeft(score.toString(), 9) + "|" + padLeft(scoreError.toString(), 7) + "|"
                        + padLeft(diffStr, 7);
            }
            System.out.println(Qute.fmt("{benchmark}{#each scores}|{it}{/each}",
                    Map.of("benchmark", padRight(benchmark), "scores", scores)));
        }

        // Save as png
        BitmapEncoder.saveBitmap(chart, "./qute-microbenchmarks", BitmapFormat.PNG);
    }

    static JsonElement readJsonElementFromFile(File inputFile) throws IOException {
        try (Reader reader = Files.newBufferedReader(inputFile.toPath(), Charset.forName("UTF-8"))) {
            return JsonParser.parseReader(reader);
        }
    }

    static int DEFAULT_PAD = 25;

    static String padRight(String val, int pad) {
        return String.format("%1$-" + pad + "s", val);
    }

    static String padLeft(String val, int pad) {
        return String.format("%1$" + pad + "s", val);
    }

    static String padRight(String val) {
        return padRight(val, DEFAULT_PAD);
    }

    static String padLeft(String val) {
        return padLeft(val, DEFAULT_PAD);
    }

}
