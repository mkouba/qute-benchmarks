package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods18 {

   static int times18(Integer val) {
      return val * 18;
   }

   @TemplateExtension(namespace = "ext18")
   static int add18(int val) {
      return val + 18;
   }
}

