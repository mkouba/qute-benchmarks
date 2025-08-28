package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods17 {

   static int times17(Integer val) {
      return val * 17;
   }

   @TemplateExtension(namespace = "ext17")
   static int add17(int val) {
      return val + 17;
   }
}

