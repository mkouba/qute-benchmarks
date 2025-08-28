package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods22 {

   static int times22(Integer val) {
      return val * 22;
   }

   @TemplateExtension(namespace = "ext22")
   static int add22(int val) {
      return val + 22;
   }
}

