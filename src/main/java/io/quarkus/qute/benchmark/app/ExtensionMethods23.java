package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods23 {

   static int times23(Integer val) {
      return val * 23;
   }

   @TemplateExtension(namespace = "ext23")
   static int add23(int val) {
      return val + 23;
   }
}

