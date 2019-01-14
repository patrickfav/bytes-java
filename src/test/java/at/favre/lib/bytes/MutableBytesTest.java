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
        Bytes b = fromAndTest(example_bytes_seven);
        assertSame(b, b.overwrite(new byte[example_bytes_seven.length]));
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void overwriteOtherArray() {
        Bytes b = fromAndTest(example_bytes_seven);
        assertSame(b, b.overwrite(Arrays.copyOf(example2_bytes_seven, example2_bytes_seven.length)));
        assertArrayEquals(example2_bytes_seven, b.array());
    }

    @Test
    public void overwritePartialArray() {
        Bytes b = fromAndTest(example_bytes_seven);
        assertSame(b, b.overwrite(new byte[]{(byte) 0xAA}, 0));
        assertArrayEquals(Bytes.of((byte) 0xAA).append(Bytes.wrap(example_bytes_seven).copy(1, example_bytes_seven.length - 1)).array(), b.array());
    }

    @Test
    public void overwritePartialArray2() {
        Bytes b = fromAndTest(example_bytes_seven);
        assertSame(b, b.overwrite(new byte[]{(byte) 0xAA}, 1));
        assertArrayEquals(
                Bytes.of(example_bytes_seven)
                        .copy(0, 1)
                        .append((byte) 0xAA)
                        .append(Bytes.wrap(example_bytes_seven).copy(2, example_bytes_seven.length - 2)).array(), b.array());
    }

    @Test
    public void fill() {
        Bytes b = fromAndTest(example_bytes_seven);
        assertSame(b, b.fill((byte) 0));
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void testCheckReferencesAndCopy() {
        Bytes b = Bytes.of(example_bytes_seven);
        Bytes m = b.copy();
        assertEquals(b, m);
        assertTrue(b.equalsContent(m));
        assertEquals(b.byteOrder(), m.byteOrder());

        Bytes dup = m.duplicate();
        assertEquals(dup, m);
        assertEquals(dup, b);
        assertNotSame(dup, b);
        assertTrue(dup.equalsContent(m));
        assertEquals(dup.byteOrder(), m.byteOrder());

        assertEquals(m.length(), dup.length());
        assertEquals(m.length(), b.length());

        assertNotEquals(example_bytes_seven[0], 0);
        assertEquals(example_bytes_seven[0], b.byteAt(0));
        assertEquals(example_bytes_seven[0], m.byteAt(0));
        assertEquals(example_bytes_seven[0], dup.byteAt(0));

        m.fill((byte) 0);

        assertEquals(example_bytes_seven[0], b.byteAt(0));
        assertEquals(0, m.byteAt(0));
        assertEquals(0, dup.byteAt(0));
    }

    @Test
    public void setByteAtTest() {
        Bytes b = fromAndTest(example_bytes_sixteen);

        for (int i = 0; i < b.length(); i++) {
            byte old = b.byteAt(i);
            Bytes bcopy = b.setByteAt(i, (byte) 0);
            assertSame(b, bcopy);
            if (old != 0) {
                assertNotEquals(old, b.byteAt(i));
            }
        }
    }

    @Test
    public void wipe() {
        Bytes b = fromAndTest(example_bytes_seven);
        assertSame(b, b.wipe());
        assertArrayEquals(new byte[example_bytes_seven.length], b.array());
    }

    @Test
    public void secureWipe() {
        Bytes b = fromAndTest(example_bytes_seven);
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe());
        assertEquals(example_bytes_seven.length, b.length());
        assertArrayNotEquals(new byte[example_bytes_seven.length], b.array());
        assertEquals(hashcode, b.hashCode());
    }

    @Test
    public void secureWipeWithSecureRandom() {
        Bytes b = fromAndTest(example_bytes_seven);
        int hashcode = b.hashCode();
        assertSame(b, b.secureWipe(new SecureRandom()));
        assertEquals(example_bytes_seven.length, b.length());
        assertArrayNotEquals(new byte[example_bytes_seven.length], b.array());
        assertEquals(hashcode, b.hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void secureWipeShouldThrowException() {
        Bytes.wrap(new byte[0]).secureWipe(null);
    }

    @Test
    public void testIfGetSameInstance() {
        Bytes b = fromAndTest(example_bytes_seven);
        assertSame(b, b);
    }

    @Test
    public void testTransformerShouldBeMutable() {
        Bytes b = fromAndTest(example_bytes_seven);
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
        Bytes leak;

        try (Bytes b = Bytes.wrap(new byte[16])) {
            assertArrayEquals(new byte[16], b.array());
            SecretKey s = new SecretKeySpec(b.array(), "AES");
            leak = b;
        }

        assertArrayNotEquals(new byte[16], leak.array());
    }


    @Test
    public void testCheckReferenceOf() {
        byte[] ref = new byte[]{1, 2, 3, 4};

        Bytes refFrom = Bytes.of(ref);
        byte[] refFromInternalArr = refFrom.array();
        assertNotSame(ref, refFrom.array());
        assertArrayEquals(ref, refFrom.array());
        assertSame(refFromInternalArr, refFrom.array());
        assertSame(refFrom.array(), refFrom.array());

        refFrom.xor(new byte[]{0, 0, 0, 0});
        assertSame(refFromInternalArr, refFrom.array());
        assertNotEquals(ref, refFrom.array());
    }

    @Test
    public void testCheckReferenceWrap() {
        byte[] ref = new byte[]{1, 2, 3, 4};

        Bytes refWrap = Bytes.wrap(ref);
        byte[] refFromInternalArr = refWrap.array();
        assertSame(ref, refWrap.array());
        assertArrayEquals(ref, refWrap.array());
        assertSame(refFromInternalArr, refWrap.array());
        assertSame(refWrap.array(), refWrap.array());

        refWrap.xor(new byte[]{0, 0, 0, 0});
        assertSame(refFromInternalArr, refWrap.array());
        assertEquals(ref, refWrap.array());
        assertNotEquals(new byte[]{1, 2, 3, 4}, refWrap.array());
    }
}
