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

package at.favre.lib.primitives.bytes;

import org.junit.Before;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public abstract class ABytesTest {
    byte[] example2_bytes;
    String example2_hex;

    byte[] example3_bytes;
    String example3_hex;

    @Before
    public void setUp() throws Exception {
        example2_bytes = new byte[]{0x4A, (byte) 0x94, (byte) 0xFD, (byte) 0xFF, 0x1E, (byte) 0xAF, (byte) 0xED};
        example2_hex = "4a94fdff1eafed";
        example3_bytes = new byte[]{0x1E, (byte) 0x01, (byte) 0xFD, (byte) 0xAA, 0x12, (byte) 0xAF, (byte) 0x78};
        example3_hex = "1e01fdaa12af78";
    }

    Bytes wrapAndTest(byte[] bytes) {
        Bytes b = Bytes.wrap(bytes);
        assertSame(bytes, b.array());
        return b;
    }

    static void assertArrayNotEquals(byte[] unexpected, byte[] actual) {
        assertFalse(Arrays.equals(unexpected, actual));
    }
}
