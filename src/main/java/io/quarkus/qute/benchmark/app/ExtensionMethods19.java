package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods19 {

   static int times19(Integer val) {
      return val * 19;
   }

   @TemplateExtension(namespace = "ext19")
   static int add19(int val) {
      return val + 19;
   }
}

