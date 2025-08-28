package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods12 {

   static int times12(Integer val) {
      return val * 12;
   }

   @TemplateExtension(namespace = "ext12")
   static int add12(int val) {
      return val + 12;
   }
}

