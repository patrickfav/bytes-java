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
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 5)
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
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random();

        option1 = new StackOverflowAnswer1Encoder();
        option2 = new BinaryToTextEncoding.Hex(true);
        option3 = new BigIntegerHexEncoder();
        option4 = new OldBytesImplementation();

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

    @Benchmark
    public String encodeStackOverflowCode() {
        return encodeDecode(option1);
    }

    @Benchmark
    public String encodeBytesLib() {
        return encodeDecode(option2);
    }

    @Benchmark
    public String encodeBigInteger() {
        return encodeDecode(option3);
    }

    @Benchmark
    public String encodeOldBytesLib() {
        return encodeDecode(option4);
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
