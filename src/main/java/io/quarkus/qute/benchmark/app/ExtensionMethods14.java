package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods14 {

   static int times14(Integer val) {
      return val * 14;
   }

   @TemplateExtension(namespace = "ext14")
   static int add14(int val) {
      return val + 14;
   }
}

