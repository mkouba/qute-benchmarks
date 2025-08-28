package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods8 {

   static int times8(Integer val) {
      return val * 8;
   }

   @TemplateExtension(namespace = "ext8")
   static int add8(int val) {
      return val + 8;
   }
}

