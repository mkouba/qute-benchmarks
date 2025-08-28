package io.quarkus.qute.benchmark.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.quarkus.qute.Qute;

public class Generator {

    static final String POJO_TEMPLATE = """
            package io.quarkus.qute.benchmark.app;
            
            import io.quarkus.qute.TemplateData;

            @TemplateData
            public class Pojo{idx} {

               public boolean isActive;

               private String name;

               public String getName() {
                  return name;
               }

               public int nameLength() {
                  return name.length();
               }

               public int getScore(int times) {
                  return {idx} * times;
               }

               public void setName(String name) {
                  this.name = name;
               }

               int ignored() {
                  return 1;
               }

            }

            """;

    static final String EXT_METHOD_TEMPLATE = """
            package io.quarkus.qute.benchmark.app;

            import io.quarkus.qute.TemplateExtension;

            @TemplateExtension
            public class ExtensionMethods{idx} {

               static int times{idx}(Integer val) {
                  return val * {idx};
               }
               
               @TemplateExtension(namespace = "ext{idx}")
               static int add{idx}(int val) {
                  return val + {idx};
               }
            }

            """;

    public static void main(String[] args) throws IOException {
        int pojoCount = 100;
        int extCount = 25;

        File outputDir = new File("src/main/java/io/quarkus/qute/benchmark/app");
        if (outputDir.exists()) {
            Files.walk(outputDir.toPath()).map(Path::toFile).forEach(File::delete);
        }
        outputDir.mkdirs();

        for (int i = 1; i <= pojoCount; i++) {
            Path path = new File(outputDir, "Pojo" + i + ".java").toPath();
            Files.writeString(path, Qute.fmt(POJO_TEMPLATE, Map.of("idx", i)));
        }
        for (int i = 1; i <= extCount; i++) {
            Path path = new File(outputDir, "ExtensionMethods" + i + ".java").toPath();
            Files.writeString(path, Qute.fmt(EXT_METHOD_TEMPLATE, Map.of("idx", i)));
        }
    }

}
