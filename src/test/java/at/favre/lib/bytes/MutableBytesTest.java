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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MutableBytesTest extends ABytesTest {
    @Test
    public void overwriteWithEmptyArray() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(new byte[example_bytes_seven.length]));
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void overwriteOtherArray() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(Arrays.copyOf(example2_bytes_seven, example2_bytes_seven.length)));
        assertArrayEquals(example2_bytes_seven, b.array());
    }

    @Test
    public void overwritePartialArray() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(new byte[]{(byte) 0xAA}, 0));
        assertArrayEquals(Bytes.from((byte) 0xAA).append(Bytes.wrap(example_bytes_seven).copy(1, example_bytes_seven.length - 1)).array(), b.array());
    }

    @Test
    public void overwritePartialArray2() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.overwrite(new byte[]{(byte) 0xAA}, 1));
        assertArrayEquals(
                Bytes.from(example_bytes_seven)
                        .copy(0, 1)
                        .append((byte) 0xAA)
                        .append(Bytes.wrap(example_bytes_seven).copy(2, example_bytes_seven.length - 2)).array(), b.array());
    }

    @Test
    public void fill() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.fill((byte) 0));
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void testConvertImmutable() {
        Bytes b = Bytes.from(example_bytes_seven);
        MutableBytes m = b.copy().mutable();
        assertNotEquals(b, m);
        assertTrue(b.equalsContent(m));
        assertEquals(b.byteOrder(), m.byteOrder());

        Bytes m2b = m.immutable();
        assertNotEquals(m2b, m);
        assertEquals(m2b, b);
        assertNotSame(m2b, b);
        assertTrue(m2b.equalsContent(m));
        assertEquals(m2b.byteOrder(), m.byteOrder());

        assertEquals(m.length(), m2b.length());
        assertEquals(m.length(), b.length());

        assertNotEquals(example_bytes_seven[0], 0);
        assertEquals(example_bytes_seven[0], b.byteAt(0));
        assertEquals(example_bytes_seven[0], m.byteAt(0));
        assertEquals(example_bytes_seven[0], m2b.byteAt(0));

        m.fill((byte) 0);

        assertEquals(example_bytes_seven[0], b.byteAt(0));
        assertEquals(0, m.byteAt(0));
        assertEquals(0, m2b.byteAt(0));
    }

    @Test
    public void setByteAtTest() {
        MutableBytes b = fromAndTest(example_bytes_sixteen).mutable();

        for (int i = 0; i < b.length(); i++) {
            byte old = b.byteAt(i);
            b.setByteAt(i, (byte) 0);
            if (old != 0) {
                assertNotEquals(old, b.byteAt(i));
            }
        }
    }

    @Test
    public void wipe() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.wipe());
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void secureWipe() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe());
        assertEquals(example_bytes_seven.length, b.length());
        assertArrayNotEquals(new byte[example_bytes_seven.length], b.array());
        assertNotEquals(hashcode, b.hashCode());
    }

    @Test
    public void secureWipeWithSecureRandom() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe(new SecureRandom()));
        assertEquals(example_bytes_seven.length, b.length());
        assertArrayNotEquals(new byte[example_bytes_seven.length], b.array());
        assertNotEquals(hashcode, b.hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void secureWipeShouldThrowException() {
        Bytes.wrap(new byte[0]).mutable().secureWipe(null);
    }

    @Test
    public void testIfGetSameInstance() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertSame(b, b.mutable());
    }

    @Test
    public void testTransformerShouldBeMutable() {
        MutableBytes b = fromAndTest(example_bytes_seven).mutable();
        assertTrue(b.isMutable());
        assertTrue(b.copy().isMutable());
        assertTrue(b.duplicate().isMutable());
        assertTrue(b.reverse().isMutable());
        assertTrue(b.resize(7).isMutable());
        assertTrue(b.resize(6).isMutable());
        assertTrue(b.not().isMutable());
        assertTrue(b.leftShift(1).isMutable());
        assertTrue(b.rightShift(1).isMutable());
        assertTrue(b.and(Bytes.random(b.length())).isMutable());
        assertTrue(b.or(Bytes.random(b.length())).isMutable());
        assertTrue(b.xor(Bytes.random(b.length())).isMutable());
        assertTrue(b.append(3).isMutable());
        assertTrue(b.hashSha256().isMutable());
    }

    @Test
    public void testAutoCloseable() {
        MutableBytes leak;

        try (MutableBytes b = Bytes.wrap(new byte[16]).mutable()) {
            assertArrayEquals(new byte[16], b.array());
            SecretKey s = new SecretKeySpec(b.array(), "AES");
            leak = b;
        }

        assertArrayNotEquals(new byte[16], leak.array());

    }
}
