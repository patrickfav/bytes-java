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

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
# JMH version: 1.21
# VM version: JDK 1.8.0_172, Java HotSpot(TM) 64-Bit Server VM, 25.172-b11
# i7 7700K / 24G

Benchmark                               (byteLength)   Mode  Cnt         Score        Error  Units
Benchmark                                (byteLength)   Mode  Cnt         Score         Error  Units
EncodingJmhBenchmark.encodeBase64Apache             1  thrpt    4   1260664,187 ±  134162,595  ops/s
EncodingJmhBenchmark.encodeBase64Apache            16  thrpt    4   1018969,264 ±    4008,839  ops/s
EncodingJmhBenchmark.encodeBase64Apache           128  thrpt    4    470368,001 ±    6377,776  ops/s
EncodingJmhBenchmark.encodeBase64Apache           512  thrpt    4    170623,614 ±    5243,433  ops/s
EncodingJmhBenchmark.encodeBase64Apache       1000000  thrpt    4       102,602 ±       2,441  ops/s
EncodingJmhBenchmark.encodeBase64Guava              1  thrpt    4  10961113,761 ± 2448198,032  ops/s
EncodingJmhBenchmark.encodeBase64Guava             16  thrpt    4   6223639,376 ±  189028,495  ops/s
EncodingJmhBenchmark.encodeBase64Guava            128  thrpt    4   1390184,429 ±   35982,746  ops/s
EncodingJmhBenchmark.encodeBase64Guava            512  thrpt    4    343957,426 ±   10939,429  ops/s
EncodingJmhBenchmark.encodeBase64Guava        1000000  thrpt    4       229,641 ±       5,811  ops/s
EncodingJmhBenchmark.encodeBase64Okio               1  thrpt    4  12567622,970 ±  460318,474  ops/s
EncodingJmhBenchmark.encodeBase64Okio              16  thrpt    4   6552615,163 ± 1382416,859  ops/s
EncodingJmhBenchmark.encodeBase64Okio             128  thrpt    4   1640526,665 ±  375607,815  ops/s
EncodingJmhBenchmark.encodeBase64Okio             512  thrpt    4    235282,079 ±    6631,560  ops/s
EncodingJmhBenchmark.encodeBase64Okio         1000000  thrpt    4       103,728 ±       0,820  ops/s
 */

@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class EncodingBase64JmhBenchmark {

    @Param({"1", "16", "128", "512", "1000000"})
    private int byteLength;
    private Map<Integer, Bytes[]> rndMap;

    private BinaryToTextEncoding.EncoderDecoder base64Okio;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random();
        base64Okio = new BinaryToTextEncoding.Base64Encoding();

        rndMap = new HashMap<>();
        int[] lengths = new int[]{1, 16, 128, 512, 1000000};
        for (int length : lengths) {
            int count = 10;
            rndMap.put(length, new Bytes[count]);
            for (int i = 0; i < count; i++) {
                rndMap.get(length)[i] = Bytes.random(length);
            }
        }
    }

    @Benchmark
    public byte[] encodeBase64Okio() {
        return encodeDecode(base64Okio);
    }

    private byte[] encodeDecode(BinaryToTextEncoding.EncoderDecoder encoder) {
        Bytes[] bytes = rndMap.get(byteLength);
        int rndNum = random.nextInt(bytes.length);

        String encoded = encoder.encode(bytes[rndNum].array(), ByteOrder.BIG_ENDIAN);
        return encoder.decode(encoded);
    }
}
