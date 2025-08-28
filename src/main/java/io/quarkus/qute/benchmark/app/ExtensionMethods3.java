package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods3 {

   static int times3(Integer val) {
      return val * 3;
   }

   @TemplateExtension(namespace = "ext3")
   static int add3(int val) {
      return val + 3;
   }
}

