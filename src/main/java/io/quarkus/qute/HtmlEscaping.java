package io.quarkus.qute;

import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
public class HtmlEscaping {

   private static final int SEED = 42;
   @Param({ "100" })
   private int latinCharsProbability;
   @Param({ "0", "10" })
   private int replacementProbability;
   @Param({ "32" })
   private int size;
   @Param({ "100", "10000" })
   private int samples;
   @Param({ "true", "false" })
   private boolean branchfull;

   private String[] toEscape;
   private Template escaper;
   private int nextInput;

   @Setup
   public void prepareDataSet() {
      final var specialChars = specialChars();
      final var nonSpecialChars = nonSpecialChars(specialChars);
      Random random = new Random(SEED);
      toEscape = new String[samples];
      var builder = new StringBuilder(size);
      for (int i = 0; i < samples; i++) {
         builder.setLength(0);
         toEscape[i] = generateString(random, specialChars, nonSpecialChars, builder);
      }
      this.escaper = Engine.builder()
            .addDefaults()
            .addResultMapper(instantiateHtmlEscaper(branchfull))
            .build()
            .parse("{this}", Variant.forContentType(Variant.TEXT_HTML));
      nextInput = 0;
   }

   private String nextInput() {
      String[] inputs = toEscape;
      int index = nextInput;
      index++;
      if (index >= inputs.length) {
         index = 0;
      }
      nextInput = index;
      return inputs[index];
   }

   private String generateString(Random rnd, byte[] specialChars, int[] nonSpecialChars, StringBuilder builder) {
      for (int i = 0; i < size; i++) {
         int pickLatinChar = rnd.nextInt(100);
         if (pickLatinChar < latinCharsProbability) {
            int pickSpecialChar = rnd.nextInt(100);
            if (pickSpecialChar < replacementProbability) {
               byte ch = specialChars[rnd.nextInt(specialChars.length)];
               builder.append((char) ch);
            } else {
               char ch = (char) nonSpecialChars[rnd.nextInt(nonSpecialChars.length)];
               builder.append(ch);
            }
         } else {
            builder.append((char) rnd.nextInt(256, Character.MAX_VALUE));
         }
      }
      return builder.toString();
   }

   private static byte[] specialChars() {
      return new byte[]{ '"', '\'', '&', '<', '>' };
   }

   private static int[] nonSpecialChars(byte[] specialChars) {
      BitSet nonSpecialChars = new BitSet(256);
      for (byte specialChar : specialChars) {
         nonSpecialChars.set(specialChar);
      }
      nonSpecialChars.flip(0, 256);
      int[] chars = new int[nonSpecialChars.cardinality()];
      for (int i = nonSpecialChars.nextSetBit(0), j = 0; i >= 0; i = nonSpecialChars.nextSetBit(i + 1), j++) {
         chars[j] = i;
      }
      return chars;
   }

   @Benchmark
   public void escape() {
      escape(nextInput());
   }

   @CompilerControl(CompilerControl.Mode.DONT_INLINE)
   public String escape(String value) {
      return escaper.render(value);
   }

   private ResultMapper instantiateHtmlEscaper(boolean branchfull) {
      if (branchfull) {
         return new CharReplacementResultMapper() {

            @Override
            protected String replacementFor(char c) {
               switch (c) {
                  case '"':
                     return "&quot;";
                  case '\'':
                     return "&#39;";
                  case '&':
                     return "&amp;";
                  case '<':
                     return "&lt;";
                  case '>':
                     return "&gt;";
                  default:
                     return null;
               }
            }
         };
      } else {
         return new HtmlEscaper(List.of(Variant.TEXT_HTML));
      }
   }
}
