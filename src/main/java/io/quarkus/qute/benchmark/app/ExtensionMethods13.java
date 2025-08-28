package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods13 {

   static int times13(Integer val) {
      return val * 13;
   }

   @TemplateExtension(namespace = "ext13")
   static int add13(int val) {
      return val + 13;
   }
}

