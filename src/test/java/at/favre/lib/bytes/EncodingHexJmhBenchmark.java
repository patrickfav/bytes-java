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
 * Benchmark                                        (byteLength)   Mode  Cnt         Score         Error  Units
 * EncodingHexJmhBenchmark.encodeBigInteger                    4  thrpt    4   5675746,504 ±  842567,828  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                    8  thrpt    4   1696726,355 ±  212646,110  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                   16  thrpt    4    880997,077 ±  116768,783  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                  128  thrpt    4     81326,528 ±   13169,476  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                  512  thrpt    4     15520,869 ±    3587,318  ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger              1000000  thrpt    4         2,470 ±       0,110  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                      4  thrpt    4  15544365,475 ± 1333444,750  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                      8  thrpt    4  14342273,380 ± 1997502,302  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                     16  thrpt    4  12410491,100 ± 1671309,859  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                    128  thrpt    4   3896074,682 ±  453096,190  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                    512  thrpt    4    909938,189 ±  137965,178  ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                1000000  thrpt    4       465,305 ±     182,300  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode             4  thrpt    4  15917799,229 ± 1133947,331  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode             8  thrpt    4  14490924,588 ±  819188,772  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode            16  thrpt    4  12635881,815 ± 1545063,635  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode           128  thrpt    4   3807810,524 ±  499109,818  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode           512  thrpt    4    917914,259 ±  122729,496  ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode       1000000  thrpt    4       471,778 ±     126,385  ops/s
 */

/**
 * Benchmark                                         (byteLength)   Mode  Cnt         Score   Error  Units
 * EncodingHexJmhBenchmark.encodeBigInteger                     4  thrpt    2   9571230,509          ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                     8  thrpt    2   3724335,328          ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                    16  thrpt    2   1454898,799          ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                   128  thrpt    2    135397,758          ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger                   512  thrpt    2     26011,356          ops/s
 * EncodingHexJmhBenchmark.encodeBigInteger               1000000  thrpt    2         4,163          ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                       4  thrpt    2  24530310,446          ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                       8  thrpt    2  23430124,448          ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                      16  thrpt    2  20531301,587          ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                     128  thrpt    2   6733027,370          ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                     512  thrpt    2   1606857,133          ops/s
 * EncodingHexJmhBenchmark.encodeBytesLib                 1000000  thrpt    2       772,010          ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                    4  thrpt    2  18639952,166          ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                    8  thrpt    2  15485869,934          ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                   16  thrpt    2  11458232,999          ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                  128  thrpt    2   2042399,306          ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib                  512  thrpt    2    280376,308          ops/s
 * EncodingHexJmhBenchmark.encodeOldBytesLib              1000000  thrpt    2       122,003          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1             4  thrpt    2  24755066,357          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1             8  thrpt    2  23455073,140          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1            16  thrpt    2  20548280,011          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1           128  thrpt    2   6675118,357          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1           512  thrpt    2   1618356,891          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode1       1000000  thrpt    2       829,757          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2             4  thrpt    2  25323515,857          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2             8  thrpt    2  24027424,805          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2            16  thrpt    2  21262668,356          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2           128  thrpt    2   7492036,913          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2           512  thrpt    2   1789353,825          ops/s
 * EncodingHexJmhBenchmark.encodeStackOverflowCode2       1000000  thrpt    2       935,383          ops/s
 */
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 2, time = 5)
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
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random();

        option1 = new StackOverflowAnswer1Encoder();
        option2 = new BinaryToTextEncoding.Hex(true);
        option3 = new BigIntegerHexEncoder();
        option4 = new OldBytesImplementation();
        option5 = new StackOverflowAnswer2Encoder();

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

    //    @Benchmark
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

    @Benchmark
    public String encodeStackOverflowCode2() {
        return encodeDecode(option5);
    }

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
}
