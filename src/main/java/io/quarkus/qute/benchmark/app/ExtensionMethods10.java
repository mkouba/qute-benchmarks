package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods10 {

   static int times10(Integer val) {
      return val * 10;
   }

   @TemplateExtension(namespace = "ext10")
   static int add10(int val) {
      return val + 10;
   }
}

