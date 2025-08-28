package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods20 {

   static int times20(Integer val) {
      return val * 20;
   }

   @TemplateExtension(namespace = "ext20")
   static int add20(int val) {
      return val + 20;
   }
}

