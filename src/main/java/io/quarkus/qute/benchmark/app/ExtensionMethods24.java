package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods24 {

   static int times24(Integer val) {
      return val * 24;
   }

   @TemplateExtension(namespace = "ext24")
   static int add24(int val) {
      return val + 24;
   }
}

