package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods1 {

   static int times1(Integer val) {
      return val * 1;
   }

   @TemplateExtension(namespace = "ext1")
   static int add1(int val) {
      return val + 1;
   }
}

