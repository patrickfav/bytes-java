/*
 * Copyright 2018 Patrick Favre-Bulle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package at.favre.lib.bytes;

import org.openjdk.jmh.annotations.*;

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Simple benchmark checking the performance of hex encoding
 * <p>
 * # Run complete. Total time: 00:22:00
 * <p>
 * REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
 * why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
 * experiments, perform baseline and negative tests that provide experimental control, make sure
 * the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
 * Do not assume the numbers tell you what you want them to tell.
 * <p>
 * Benchmark                                         (byteLength)   Mode  Cnt         Score          Error  Units
 * EncodingHexJmhBenchmark.encodeBigInteger                     4  thrpt    3   9427359,108 ±   194693,624  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                     8  thrpt    3   2638943,318 ±    15520,030  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                    16  thrpt    3   2088514,337 ±    50090,292  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                   128  thrpt    3    133665,420 ±     5750,982  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                   512  thrpt    3     25807,764 ±      407,273  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger               1000000  thrpt    3         4,178 ±        0,021  ops/s
 * EncodingHexJmhBenchmark.encodeBouncyCastleHex                4  thrpt    3  14260598,238 ±  4113483,586  ops/s
 * EncodingHexJmhBenchmark.encodeBouncyCastleHex                8  thrpt    3  10910660,517 ±    51077,989  ops/s
 * EncodingHexJmhBenchmark.encodeBouncyCastleHex               16  thrpt    3   7501666,934 ±   938571,073  ops/s
 * EncodingHexJmhBenchmark.encodeBouncyCastleHex              128  thrpt    3   1077236,523 ±    78734,473  ops/s
 * EncodingHexJmhBenchmark.encodeBouncyCastleHex              512  thrpt    3    266238,359 ±     2544,657  ops/s
 * EncodingHexJmhBenchmark.encodeBouncyCastleHex          1000000  thrpt    3       152,235 ±        8,156  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                       4  thrpt    3  24944896,332 ±  2367456,841  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                       8  thrpt    3  23222999,376 ±   396218,748  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                      16  thrpt    3  20423170,033 ±   194747,311  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                     128  thrpt    3   6685522,295 ±   920540,781  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                     512  thrpt    3   1612325,148 ±    18002,919  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                 1000000  thrpt    3       825,527 ±      124,972  ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                    4  thrpt    3  17973467,504 ±   429817,214  ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                    8  thrpt    3  14836039,023 ± 16437596,584  ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                   16  thrpt    3  11727630,076 ±  1849815,074  ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                  128  thrpt    3   2656731,985 ±    53887,109  ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                  512  thrpt    3    288949,898 ±     7650,867  ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib              1000000  thrpt    3       127,737 ±        3,982  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1             4  thrpt    3  24557504,065 ±   797466,455  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1             8  thrpt    3  23515327,490 ±   979922,009  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1            16  thrpt    3  20377272,084 ±   480955,369  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1           128  thrpt    3   6767893,154 ±   511154,141  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1           512  thrpt    3   1573543,465 ±  1126382,628  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1       1000000  thrpt    3       833,885 ±       54,099  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2             4  thrpt    3  25220997,342 ±   852230,094  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2             8  thrpt    3  24152600,074 ±   480651,708  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2            16  thrpt    3  21494551,701 ±  1722250,353  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2           128  thrpt    3   7554272,476 ±  1329386,082  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2           512  thrpt    3   1781989,859 ±   125696,811  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2       1000000  thrpt    3       935,685 ±      103,571  ops/s
 * EncodingHexJmhBenchmark.encodeGuavaBase16                    4  thrpt    3  17606767,347 ± 3615884,433  ops/s
 * EncodingHexJmhBenchmark.encodeGuavaBase16                    8  thrpt    3  14077449,253 ± 3570354,199  ops/s
 * EncodingHexJmhBenchmark.encodeGuavaBase16                   16  thrpt    3  10177925,910 ± 5584265,227  ops/s
 * EncodingHexJmhBenchmark.encodeGuavaBase16                  128  thrpt    3   2094658,415 ± 2321008,783  ops/s
 * EncodingHexJmhBenchmark.encodeGuavaBase16                  512  thrpt    3    514528,610 ±   20419,732  ops/s
 * EncodingHexJmhBenchmark.encodeGuavaBase16              1000000  thrpt    3       257,440 ±      23,100  ops/s
 * EncodingHexJmhBenchmark.encodeSpringSecurity                 4  thrpt    3  25269684,873 ± 1820484,918  ops/s
 * EncodingHexJmhBenchmark.encodeSpringSecurity                 8  thrpt    3  22631565,512 ±  291386,502  ops/s
 * EncodingHexJmhBenchmark.encodeSpringSecurity                16  thrpt    3  18704986,566 ±  604936,041  ops/s
 * EncodingHexJmhBenchmark.encodeSpringSecurity               128  thrpt    3   4904805,240 ±   18454,315  ops/s
 * EncodingHexJmhBenchmark.encodeSpringSecurity               512  thrpt    3   1165535,921 ±   13487,051  ops/s
 * EncodingHexJmhBenchmark.encodeSpringSecurity           1000000  thrpt    3       601,152 ±     248,099  ops/s
 */

@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 3, time = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class EncodingHexJmhBenchmark {

    @Param({"4", "8", "16", "128", "512", "1000000"})
    private int byteLength;
    private Map<Integer, Bytes[]> rndMap;

    private BinaryToTextEncoding.EncoderDecoder option1;
    private BinaryToTextEncoding.EncoderDecoder option2;
    private BinaryToTextEncoding.EncoderDecoder option3;
    private BinaryToTextEncoding.EncoderDecoder option4;
    private BinaryToTextEncoding.EncoderDecoder option5;
    private BinaryToTextEncoding.EncoderDecoder option6;
    private BinaryToTextEncoding.EncoderDecoder option7;
    private BinaryToTextEncoding.EncoderDecoder option8;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random();

        option1 = new StackOverflowAnswer1Encoder();
        option2 = new BinaryToTextEncoding.Hex(true);
        option3 = new BigIntegerHexEncoder();
        option4 = new OldBytesImplementation();
        option5 = new StackOverflowAnswer2Encoder();
//        option6 = new BCUtilEncoder();
//        option7 = new GuavaEncoder();
//        option8 = new SpringSecurityEncoder();

        rndMap = new HashMap<>();
        int[] lengths = new int[]{4, 8, 16, 128, 512, 1000000};
        for (int length : lengths) {
            int count = 10;
            rndMap.put(length, new Bytes[count]);
            for (int i = 0; i < count; i++) {
                rndMap.get(length)[i] = Bytes.random(length);
            }
        }
    }

    //    @Benchmark
    public String encodeStackOverflowCode1() {
        return encodeDecode(option1);
    }

    @Benchmark
    public String encodeBytesLib() {
        return encodeDecode(option2);
    }

    //    @Benchmark
    public String encodeBigInteger() {
        return encodeDecode(option3);
    }

    //    @Benchmark
    public String encodeOldBytesLib() {
        return encodeDecode(option4);
    }

    //    @Benchmark
    public String encodeStackOverflowCode2() {
        return encodeDecode(option5);
    }

//    @Benchmark
//    public String encodeBouncyCastleHex() {
//        return encodeDecode(option6);
//    }
//
//    @Benchmark
//    public String encodeGuavaBase16() {
//        return encodeDecode(option7);
//    }
//
//    @Benchmark
//    public String encodeSpringSecurity() {
//        return encodeDecode(option8);
//    }

    private String encodeDecode(BinaryToTextEncoding.EncoderDecoder encoder) {
        Bytes[] bytes = rndMap.get(byteLength);
        int rndNum = random.nextInt(bytes.length);
        return encoder.encode(bytes[rndNum].array(), ByteOrder.BIG_ENDIAN);
    }

    /**
     * See: https://stackoverflow.com/a/9855338/774398
     */
    static final class StackOverflowAnswer1Encoder implements BinaryToTextEncoding.EncoderDecoder {
        private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

        @Override
        public String encode(byte[] bytes, ByteOrder byteOrder) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        }

        @Override
        public byte[] decode(CharSequence encoded) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * With full lookup table of all 256 values
     * See: https://stackoverflow.com/a/21429909/774398
     */
    static final class StackOverflowAnswer2Encoder implements BinaryToTextEncoding.EncoderDecoder {

        private static final char[] BYTE2HEX = (
                "000102030405060708090A0B0C0D0E0F" +
                        "101112131415161718191A1B1C1D1E1F" +
                        "202122232425262728292A2B2C2D2E2F" +
                        "303132333435363738393A3B3C3D3E3F" +
                        "404142434445464748494A4B4C4D4E4F" +
                        "505152535455565758595A5B5C5D5E5F" +
                        "606162636465666768696A6B6C6D6E6F" +
                        "707172737475767778797A7B7C7D7E7F" +
                        "808182838485868788898A8B8C8D8E8F" +
                        "909192939495969798999A9B9C9D9E9F" +
                        "A0A1A2A3A4A5A6A7A8A9AAABACADAEAF" +
                        "B0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF" +
                        "C0C1C2C3C4C5C6C7C8C9CACBCCCDCECF" +
                        "D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF" +
                        "E0E1E2E3E4E5E6E7E8E9EAEBECEDEEEF" +
                        "F0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF").toCharArray();

        @Override
        public String encode(byte[] bytes, ByteOrder byteOrder) {
            final int len = bytes.length;
            final char[] chars = new char[len << 1];
            int hexIndex;
            int idx = 0;
            int ofs = 0;
            while (ofs < len) {
                hexIndex = (bytes[ofs++] & 0xFF) << 1;
                chars[idx++] = BYTE2HEX[hexIndex++];
                chars[idx++] = BYTE2HEX[hexIndex];
            }
            return new String(chars);
        }

        @Override
        public byte[] decode(CharSequence encoded) {
            throw new UnsupportedOperationException();
        }
    }

    static final class BigIntegerHexEncoder implements BinaryToTextEncoding.EncoderDecoder {
        @Override
        public String encode(byte[] bytes, ByteOrder byteOrder) {
            return new BigInteger(1, bytes).toString(16);
        }

        @Override
        public byte[] decode(CharSequence encoded) {
            throw new UnsupportedOperationException();
        }
    }

    static final class OldBytesImplementation implements BinaryToTextEncoding.EncoderDecoder {

        @Override
        public String encode(byte[] byteArray, ByteOrder byteOrder) {
            StringBuilder sb = new StringBuilder(byteArray.length * 2);

            int index;
            char first4Bit;
            char last4Bit;
            for (int i = 0; i < byteArray.length; i++) {
                index = (byteOrder == ByteOrder.BIG_ENDIAN) ? i : byteArray.length - i - 1;
                first4Bit = Character.forDigit((byteArray[index] >> 4) & 0xF, 16);
                last4Bit = Character.forDigit((byteArray[index] & 0xF), 16);
                sb.append(first4Bit).append(last4Bit);
            }
            return sb.toString();
        }

        @Override
        public byte[] decode(CharSequence encoded) {
            throw new UnsupportedOperationException();
        }
    }

    /*
     * Requires dependencies:
     *
     *
     * <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.60</version>
            <scope>test</scope>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.google.guava/guava -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>28.1-jre</version>
            <scope>test</scope>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.security/spring-security-core -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-core</artifactId>
            <version>5.2.0.RELEASE</version>
            <scope>test</scope>
        </dependency>
     */

//    static final class BCUtilEncoder implements BinaryToTextEncoding.EncoderDecoder {
//        @Override
//        public String encode(byte[] array, ByteOrder byteOrder) {
//            return org.bouncycastle.util.encoders.Hex.toHexString(array);
//        }
//
//        @Override
//        public byte[] decode(CharSequence encoded) {
//            return org.bouncycastle.util.encoders.Hex.decode(encoded.toString());
//        }
//    }
//
//    static final class GuavaEncoder implements BinaryToTextEncoding.EncoderDecoder {
//        @Override
//        public String encode(byte[] array, ByteOrder byteOrder) {
//            return com.google.common.io.BaseEncoding.base16().lowerCase().encode(array);
//        }
//
//        @Override
//        public byte[] decode(CharSequence encoded) {
//            return com.google.common.io.BaseEncoding.base16().lowerCase().decode(encoded);
//        }
//    }
//
//    static final class SpringSecurityEncoder implements BinaryToTextEncoding.EncoderDecoder {
//        @Override
//        public String encode(byte[] array, ByteOrder byteOrder) {
//            return new String(org.springframework.security.crypto.codec.Hex.encode(array));
//        }
//
//        @Override
//        public byte[] decode(CharSequence encoded) {
//            return org.springframework.security.crypto.codec.Hex.decode(encoded);
//        }
//    }
}
