package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class ExtensionMethods4 {

   static int times4(Integer val) {
      return val * 4;
   }

   @TemplateExtension(namespace = "ext4")
   static int add4(int val) {
      return val + 4;
   }
}

