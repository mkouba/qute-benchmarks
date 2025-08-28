package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods5 {

   static int times5(Integer val) {
      return val * 5;
   }

   @TemplateExtension(namespace = "ext5")
   static int add5(int val) {
      return val + 5;
   }
}

