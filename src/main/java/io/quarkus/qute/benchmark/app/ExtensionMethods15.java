package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods15 {

   static int times15(Integer val) {
      return val * 15;
   }

   @TemplateExtension(namespace = "ext15")
   static int add15(int val) {
      return val + 15;
   }
}

