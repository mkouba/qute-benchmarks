package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods16 {

   static int times16(Integer val) {
      return val * 16;
   }

   @TemplateExtension(namespace = "ext16")
   static int add16(int val) {
      return val + 16;
   }
}

