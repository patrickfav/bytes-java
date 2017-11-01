/*
 * Copyright 2017 Patrick Favre-Bulle
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

import org.junit.Ignore;
import org.junit.Test;

public class BytesBenchmark {

    @Test
    @Ignore
    public void immutableVsMutable() throws Exception {
        int length = 16 * 1024;
        Bytes randomXorOp = Bytes.randomNonSecure(length);
        Bytes immutable = Bytes.allocate(length);
        Bytes mutable = Bytes.allocate(length).mutable();

        for (int i = 0; i < 10; i++) {
            immutable = immutable.xor(randomXorOp);
            mutable = mutable.xor(randomXorOp);
        }

        for (int i = 0; i < 5; i++) {
            Thread.sleep(32);
            long durationImmutable = runBenchmark(randomXorOp, immutable);
            Thread.sleep(120);
            long durationMutable = runBenchmark(randomXorOp, mutable);
            System.out.println("\nRun " + i);
            System.out.println("Immutable: \t" + durationImmutable + " ns");
            System.out.println("Mutable: \t" + durationMutable + " ns");
        }
    }

    private long runBenchmark(Bytes randomXorOp, Bytes bytes) {
        long startImmutable = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            bytes = bytes.xor(randomXorOp);
        }
        return System.nanoTime() - startImmutable;
    }
}
