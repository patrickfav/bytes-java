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

import org.junit.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MutableBytesABytesTest extends ABytesTest {
    @Test
    public void overwriteWithEmptyArray() throws Exception {
        MutableBytes b = wrapAndTest(example2_bytes).mutable();
        assertSame(b, b.overwrite(new byte[example2_bytes.length]));
        assertArrayEquals(new byte[example2_bytes.length], b.array());
    }

    @Test
    public void overwriteOtherArray() throws Exception {
        MutableBytes b = wrapAndTest(example2_bytes).mutable();
        assertSame(b, b.overwrite(Arrays.copyOf(example3_bytes, example3_bytes.length)));
        assertArrayEquals(example3_bytes, b.array());
    }

    @Test
    public void overwritePartialArray() throws Exception {
        MutableBytes b = wrapAndTest(Arrays.copyOf(example2_bytes, example2_bytes.length)).mutable();
        assertSame(b, b.overwrite(new byte[]{(byte) 0xAA}, 0));
        assertArrayEquals(Bytes.from((byte) 0xAA).append(Bytes.wrap(example2_bytes).copy(1, example2_bytes.length - 1)).array(), b.array());
    }

    @Test
    public void fill() throws Exception {
        MutableBytes b = wrapAndTest(example2_bytes).mutable();
        assertSame(b, b.fill((byte) 0));
        assertArrayEquals(new byte[example2_bytes.length], b.array());
    }

    @Test
    public void wipe() throws Exception {
        MutableBytes b = wrapAndTest(example2_bytes).mutable();
        assertSame(b, b.wipe());
        assertArrayEquals(new byte[example2_bytes.length], b.array());
    }

    @Test
    public void secureWipe() throws Exception {
        MutableBytes b = wrapAndTest(example2_bytes).mutable();
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe());
        assertEquals(example2_bytes.length, b.length());
        assertArrayNotEquals(new byte[example2_bytes.length], b.array());
        assertNotEquals(hashcode, b.hashCode());
    }

    @Test
    public void secureWipeWithSecureRandom() throws Exception {
        MutableBytes b = wrapAndTest(example2_bytes).mutable();
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe(new SecureRandom()));
        assertEquals(example2_bytes.length, b.length());
        assertArrayNotEquals(new byte[example2_bytes.length], b.array());
        assertNotEquals(hashcode, b.hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void secureWipeShouldThrowException() throws Exception {
        Bytes.wrap(new byte[0]).mutable().secureWipe(null);
    }

}