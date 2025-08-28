package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods21 {

   static int times21(Integer val) {
      return val * 21;
   }

   @TemplateExtension(namespace = "ext21")
   static int add21(int val) {
      return val + 21;
   }
}

