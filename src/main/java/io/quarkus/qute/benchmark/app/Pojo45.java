package io.quarkus.qute.benchmark.app;

import io.quarkus.qute.TemplateData;

@TemplateData
public class Pojo45 {

   public boolean isActive;

   private String name;

   public String getName() {
      return name;
   }

   public int nameLength() {
      return name.length();
   }

   public int getScore(int times) {
      return 45 * times;
   }

   public void setName(String name) {
      this.name = name;
   }

   int ignored() {
      return 1;
   }

}

