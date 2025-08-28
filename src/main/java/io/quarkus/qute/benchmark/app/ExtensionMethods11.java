package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods11 {

   static int times11(Integer val) {
      return val * 11;
   }

   @TemplateExtension(namespace = "ext11")
   static int add11(int val) {
      return val + 11;
   }
}

