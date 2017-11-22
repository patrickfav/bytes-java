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

import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BinaryToTextEncodingTest {
    @Test
    public void encodeHex() throws Exception {
        assertEquals("010203", new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
        assertEquals("030201", new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeHexShouldFail() throws Exception {
        new BinaryToTextEncoding.Hex(false).decode("AAI=");
    }

    @Test
    public void encodeBaseRadix() throws Exception {
        assertEquals("100211", new BinaryToTextEncoding.BaseRadix(16).encode(new byte[]{16, 2, 17}, ByteOrder.BIG_ENDIAN));
        assertEquals("110210", new BinaryToTextEncoding.BaseRadix(16).encode(new byte[]{16, 2, 17}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.BaseRadix(2).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.BaseRadix(2).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidRadix116() throws Exception {
        new BinaryToTextEncoding.BaseRadix(16).decode("AAI=");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidRadix36() throws Exception {
        new BinaryToTextEncoding.BaseRadix(36).decode("AAI=");
    }

    @Test
    public void encodeBase64() throws Exception {
        assertEquals("EAIR", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{16, 2, 17}, ByteOrder.BIG_ENDIAN));
        assertEquals("EQIQ", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{17, 2, 16}, ByteOrder.BIG_ENDIAN));
        assertEquals("EQIQ", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{16, 2, 17}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidBase64() throws Exception {
        new BinaryToTextEncoding.Base64Encoding().decode("(&´´");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeHalfInvalidBase64() throws Exception {
        new BinaryToTextEncoding.Base64Encoding().decode("EAI`");
    }
}