package io.quarkus.qute;

import java.util.BitSet;
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
public class JsonEscaping {

   private static final int SEED = 42;
   // how many 0-255 chars should be generated vs non-latin ones
   @Param({ "100" })
   private int latinCharsProbability;
   // these are the chars 0-31 excluding \, \r, \b, \n, \t, \f, /, " - which need "big" replacements i.e. 6 chars
   @Param({ "0", "10" })
   private int ctrlProbabibility;
   // this includes any other special char, including the big ones, if any
   @Param({ "0", "10" })
   private int replacementProbability;
   @Param({ "8", "32" })
   private int size;
   @Param({ "100", "10000" })
   private int samples;
   private String[] toEscape;
   private JsonEscaper escaper;
   private int nextInput;

   @Setup
   public void prepareDataSet() {
      // save useless trials to be performed
      if (latinCharsProbability == 0 && (ctrlProbabibility != 0 || replacementProbability != 0)) {
         System.exit(0);
      }
      if (replacementProbability == 0 && ctrlProbabibility != 0) {
         System.exit(0);
      }
      final var specialChars = specialChars();
      final var specialCtrlChars = specialCtrlChars(specialChars);
      final var nonSpecialChars = nonSpecialChars(specialChars, specialCtrlChars);
      Random random = new Random(SEED);
      toEscape = new String[samples];
      var builder = new StringBuilder(size);
      for (int i = 0; i < samples; i++) {
         builder.setLength(0);
         toEscape[i] = generateString(random, specialChars, specialCtrlChars, nonSpecialChars, builder);
      }
      this.escaper = new JsonEscaper();
      nextInput = 0;
   }

   private String nextInput() {
      // increment index and sanitize it
      String[] inputs = toEscape;
      int index  = nextInput;
      index++;
      if (index >= inputs.length) {
         index = 0;
      }
      nextInput = index;
      return inputs[index];
   }


   private String generateString(Random rnd, byte[] specialChars, byte[] specialCtrlChars, int[] nonSpecialChars, StringBuilder builder) {
      // generate the string based on the probabilities configured
      for (int i = 0; i < size; i++) {
         int pickLatinChar = rnd.nextInt(100);
         if (pickLatinChar < latinCharsProbability) {
            // let's check first if we should pick any special char
            int pickSpecialChar = rnd.nextInt(100);
            if (pickSpecialChar < replacementProbability) {
               // it is a ctrl char or any other special char
               int pickCtrlChar = rnd.nextInt(100);
               if (pickCtrlChar < ctrlProbabibility) {
                  // pick a special ctrl char: 6 bytes
                  byte ch = specialCtrlChars[rnd.nextInt(0, specialCtrlChars.length)];
                  builder.append((char) ch);
               } else {
                  // pick any other special char: 2 bytes
                  byte ch = specialChars[rnd.nextInt(0, specialChars.length)];
                  builder.append((char) ch);
               }
            } else {
               // pick random non-special char
               char ch = (char) nonSpecialChars[rnd.nextInt(0, nonSpecialChars.length)];
               builder.append(ch);
            }
         } else {
            // generate a non-latin char
            builder.append((char) rnd.nextInt(256, Character.MAX_VALUE));
         }
      }
      return builder.toString();
   }

   private static byte[] specialChars() {
      byte[] specialChars = new byte[8];
      specialChars[0] = 0x5C; // \
      specialChars[1] = 0x0D; // \r
      specialChars[2] = 0x08; // \b
      specialChars[3] = 0x0A; // \n
      specialChars[4] = 0x09; // \t
      specialChars[5] = 0x0C; // \f
      specialChars[6] = 0x2F; // /
      specialChars[7] = 0x22; // "
      return specialChars;
   }

   private static byte[] specialCtrlChars(byte[] specialChars) {
      BitSet specialCtrlChars = new BitSet(32);
      for (byte specialChar : specialChars) {
         if (specialChar < 32) {
            specialCtrlChars.set(specialChar);
         }
      }
      // negate these to know the remaining ones
      specialCtrlChars.flip(0, 32);
      // create a byte[] out of this
      byte[] ctrlChars = new byte[specialCtrlChars.cardinality()];
      for (int i = specialCtrlChars.nextSetBit(0), j = 0; i >= 0; i = specialCtrlChars.nextSetBit(i + 1), j++) {
         ctrlChars[j] = (byte) i;
      }
      return ctrlChars;
   }

   private static int[] nonSpecialChars(byte[] specialChars, byte[] specialCtrlChars) {
      BitSet nonSpecialChars = new BitSet(256);
      for (byte specialChar : specialChars) {
         nonSpecialChars.set(specialChar);
      }
      for (byte specialCtrlChar : specialCtrlChars) {
         nonSpecialChars.set(specialCtrlChar);
      }
      // negate these to know the remaining ones
      nonSpecialChars.flip(0, 256);
      // create a int[] out of this
      int[] chars = new int[nonSpecialChars.cardinality()];
      for (int i = nonSpecialChars.nextSetBit(0), j = 0; i >= 0; i = nonSpecialChars.nextSetBit(i + 1), j++) {
         chars[j] = i;
      }
      return chars;
   }

   @Benchmark
   public void escape() {
      // we don't need to BlackHole::consume since the method is not going to be inlined!
      escape(nextInput());
   }

   @CompilerControl(CompilerControl.Mode.DONT_INLINE)
   public String escape(String value) {
      return escaper.escape(value);
   }

}
