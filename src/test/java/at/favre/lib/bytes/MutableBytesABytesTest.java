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

import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MutableBytesABytesTest extends ABytesTest {
    @Test
    public void overwriteWithEmptyArray() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(new byte[example_bytes_seven.length]));
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void overwriteOtherArray() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(Arrays.copyOf(example2_bytes_seven, example2_bytes_seven.length)));
        assertArrayEquals(example2_bytes_seven, b.array());
    }

    @Test
    public void overwritePartialArray() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(new byte[]{(byte) 0xAA}, 0));
        assertArrayEquals(Bytes.from((byte) 0xAA).append(Bytes.wrap(example_bytes_seven).copy(1, example_bytes_seven.length - 1)).array(), b.array());
    }

    @Test
    public void overwritePartialArray2() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(new byte[]{(byte) 0xAA}, 1));
        assertArrayEquals(
                Bytes.from(example_bytes_seven)
                        .copy(0, 1)
                        .append((byte) 0xAA)
                        .append(Bytes.wrap(example_bytes_seven).copy(2, example_bytes_seven.length - 2)).array(), b.array());
    }

    @Test
    public void fill() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.fill((byte) 0));
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void wipe() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.wipe());
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void secureWipe() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe());
        assertEquals(example_bytes_seven.length, b.length());
        assertArrayNotEquals(new byte[example_bytes_seven.length], b.array());
        assertNotEquals(hashcode, b.hashCode());
    }

    @Test
    public void secureWipeWithSecureRandom() throws Exception {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe(new SecureRandom()));
        assertEquals(example_bytes_seven.length, b.length());
        assertArrayNotEquals(new byte[example_bytes_seven.length], b.array());
        assertNotEquals(hashcode, b.hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void secureWipeShouldThrowException() throws Exception {
        Bytes.wrap(new byte[0]).mutable().secureWipe(null);
    }

}