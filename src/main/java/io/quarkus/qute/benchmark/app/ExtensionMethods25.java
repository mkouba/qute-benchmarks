package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods25 {

   static int times25(Integer val) {
      return val * 25;
   }

   @TemplateExtension(namespace = "ext25")
   static int add25(int val) {
      return val + 25;
   }
}

