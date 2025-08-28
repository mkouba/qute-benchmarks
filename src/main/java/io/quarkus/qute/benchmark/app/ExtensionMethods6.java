package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods6 {

   static int times6(Integer val) {
      return val * 6;
   }

   @TemplateExtension(namespace = "ext6")
   static int add6(int val) {
      return val + 6;
   }
}

