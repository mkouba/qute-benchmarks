package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods9 {

   static int times9(Integer val) {
      return val * 9;
   }

   @TemplateExtension(namespace = "ext9")
   static int add9(int val) {
      return val + 9;
   }
}

