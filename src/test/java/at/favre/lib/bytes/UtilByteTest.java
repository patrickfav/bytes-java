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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

public class UtilByteTest extends AUtilTest {

    @Test
    public void testIndexOf() {
        assertEquals(-1, indexOf(EMPTY, (byte) 1));
        assertEquals(-1, indexOf(ARRAY1, (byte) 2));
        assertEquals(-1, indexOf(ARRAY234, (byte) 1));
        assertEquals(0, indexOf(
                new byte[]{(byte) -1}, (byte) -1));
        assertEquals(0, indexOf(ARRAY234, (byte) 2));
        assertEquals(1, indexOf(ARRAY234, (byte) 3));
        assertEquals(2, indexOf(ARRAY234, (byte) 4));
        assertEquals(1, indexOf(
                new byte[]{(byte) 2, (byte) 3, (byte) 2, (byte) 3},
                (byte) 3));
    }

    private int indexOf(byte[] empty, byte b) {
        return Util.Byte.indexOf(empty, new byte[]{b}, 0, empty.length);
    }

    @Test
    public void testIndexOf_arrayTarget() {
        assertEquals(-1, Util.Byte.indexOf(EMPTY, EMPTY, 0, EMPTY.length));
        assertEquals(-1, Util.Byte.indexOf(ARRAY234, EMPTY, 0, ARRAY234.length));
        assertEquals(-1, Util.Byte.indexOf(EMPTY, ARRAY234, 0, EMPTY.length));
        assertEquals(-1, Util.Byte.indexOf(ARRAY234, ARRAY1, 0, ARRAY234.length));
        assertEquals(-1, Util.Byte.indexOf(ARRAY1, ARRAY234, 0, ARRAY1.length));
        assertEquals(0, Util.Byte.indexOf(ARRAY1, ARRAY1, 0, ARRAY1.length));
        assertEquals(0, Util.Byte.indexOf(ARRAY234, ARRAY234, 0, ARRAY234.length));
        assertEquals(0, Util.Byte.indexOf(
                ARRAY234, new byte[]{(byte) 2, (byte) 3}, 0, 2));
        assertEquals(1, Util.Byte.indexOf(
                ARRAY234, new byte[]{(byte) 3, (byte) 4}, 0, 2));
        assertEquals(1, Util.Byte.indexOf(ARRAY234, new byte[]{(byte) 3}, 0, ARRAY234.length));
        assertEquals(2, Util.Byte.indexOf(ARRAY234, new byte[]{(byte) 4}, 0, ARRAY234.length));
        assertEquals(1, Util.Byte.indexOf(new byte[]{(byte) 2, (byte) 3, (byte) 3, (byte) 3, (byte) 3}, new byte[]{(byte) 3}, 0, 5));
        assertEquals(2, Util.Byte.indexOf(
                new byte[]{(byte) 2, (byte) 3, (byte) 2,
                        (byte) 3, (byte) 4, (byte) 2, (byte) 3},
                new byte[]{(byte) 2, (byte) 3, (byte) 4}
                , 0, 7));
        assertEquals(1, Util.Byte.indexOf(
                new byte[]{(byte) 2, (byte) 2, (byte) 3,
                        (byte) 4, (byte) 2, (byte) 3, (byte) 4},
                new byte[]{(byte) 2, (byte) 3, (byte) 4}
                , 0, 7));
        assertEquals(-1, Util.Byte.indexOf(
                new byte[]{(byte) 4, (byte) 3, (byte) 2},
                new byte[]{(byte) 2, (byte) 3, (byte) 4}
                , 0, 2));
    }

    @Test
    public void testLastIndexOf() {
        assertEquals(-1, lastIndexOf(EMPTY, (byte) 1));
        assertEquals(-1, lastIndexOf(ARRAY1, (byte) 2));
        assertEquals(-1, lastIndexOf(ARRAY234, (byte) 1));
        assertEquals(0, lastIndexOf(
                new byte[]{(byte) -1}, (byte) -1));
        assertEquals(0, lastIndexOf(ARRAY234, (byte) 2));
        assertEquals(1, lastIndexOf(ARRAY234, (byte) 3));
        assertEquals(2, lastIndexOf(ARRAY234, (byte) 4));
        assertEquals(3, lastIndexOf(
                new byte[]{(byte) 2, (byte) 3, (byte) 2, (byte) 3},
                (byte) 3));
    }

    private int lastIndexOf(byte[] empty, byte b) {
        return Util.Byte.lastIndexOf(empty, b, 0, empty.length);
    }

    @Test
    public void testConcat() {
        assertArrayEquals(EMPTY, Util.Byte.concat());
        assertArrayEquals(EMPTY, Util.Byte.concat(EMPTY));
        assertArrayEquals(EMPTY, Util.Byte.concat(EMPTY, EMPTY, EMPTY));
        assertArrayEquals(ARRAY1, Util.Byte.concat(ARRAY1));
        assertNotSame(ARRAY1, Util.Byte.concat(ARRAY1));
        assertArrayEquals(ARRAY1, Util.Byte.concat(EMPTY, ARRAY1, EMPTY));
        assertArrayEquals(
                new byte[]{(byte) 1, (byte) 1, (byte) 1},
                Util.Byte.concat(ARRAY1, ARRAY1, ARRAY1));
        assertArrayEquals(
                new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4},
                Util.Byte.concat(ARRAY1, ARRAY234));
    }

    @Test(expected = IllegalStateException.class)
    public void readFromStream() {
        Util.File.readFromStream(null, -1);
    }

    @Test
    public void concatVararg() {
        assertArrayEquals(new byte[]{1}, Util.Byte.concatVararg((byte) 1, null));
    }

    @Test
    public void testReverse() {
        testReverse(new byte[]{}, new byte[]{});
        testReverse(new byte[]{1}, new byte[]{1});
        testReverse(new byte[]{1, 2}, new byte[]{2, 1});
        testReverse(new byte[]{3, 1, 1}, new byte[]{1, 1, 3});
        testReverse(new byte[]{-1, 1, -2, 2}, new byte[]{2, -2, 1, -1});
    }

    @Test
    public void testReverseIndexed() {
        testReverse(new byte[]{}, 0, 0, new byte[]{});
        testReverse(new byte[]{1}, 0, 1, new byte[]{1});
        testReverse(new byte[]{1, 2}, 0, 2, new byte[]{2, 1});
        testReverse(new byte[]{3, 1, 1}, 0, 2, new byte[]{1, 3, 1});
        testReverse(new byte[]{3, 1, 1}, 0, 1, new byte[]{3, 1, 1});
        testReverse(new byte[]{-1, 1, -2, 2}, 1, 3, new byte[]{-1, -2, 1, 2});
    }

    private static void testReverse(byte[] input, byte[] expectedOutput) {
        input = Arrays.copyOf(input, input.length);
        Util.Byte.reverse(input, 0, input.length);
        assertArrayEquals(expectedOutput, input);
    }

    private static void testReverse(byte[] input, int fromIndex, int toIndex, byte[] expectedOutput) {
        input = Arrays.copyOf(input, input.length);
        Util.Byte.reverse(input, fromIndex, toIndex);
        assertArrayEquals(expectedOutput, input);
    }

    @Test
    public void testLeftShift() {
        byte[] test = new byte[]{0, 0, 1, 0};
        assertArrayEquals(new byte[]{0, 1, 0, 0}, Util.Byte.shiftLeft(new byte[]{0, 0, -128, 0}, 1));
        assertArrayEquals(new byte[]{0, 1, 0, 0}, Util.Byte.shiftLeft(new byte[]{0, 0, 64, 0}, 2));
        assertArrayEquals(new byte[]{1, 1, 1, 0}, Util.Byte.shiftLeft(new byte[]{-128, -128, -128, -128}, 1));
        assertArrayEquals(new byte[]{0, 0, 2, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 1));
        assertArrayEquals(new byte[]{0, 0, 4, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 2));
        assertArrayEquals(new byte[]{0, 0, 8, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 3));
        assertArrayEquals(new byte[]{0, 1, 0, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 8));
        assertArrayEquals(new byte[]{0, 2, 0, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 9));
        assertArrayEquals(new byte[]{1, 0, 0, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 16));
        assertArrayEquals(new byte[]{2, 0, 0, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 17));
        assertArrayEquals(new byte[]{-128, 0, 0, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 23));
        assertArrayEquals(new byte[]{0, 0, 0, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 24));
        assertArrayEquals(new byte[]{0, 0, 0, 0}, Util.Byte.shiftLeft(Bytes.of(test).array(), 24));

        assertSame(test, Util.Byte.shiftLeft(test, 1));
    }

    @Test
    @Ignore
    public void testLeftShiftAgainstRefImpl() {
        for (int i = 0; i < 1000; i++) {
            int shift = 1;
            Bytes rnd = Bytes.random(4 + new Random().nextInt(14));

            byte[] expected = Bytes.of(new BigInteger(rnd.array()).shiftLeft(shift).toByteArray()).resize(rnd.length(), BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_MAX_LENGTH).array();
            byte[] actual = Bytes.of(Util.Byte.shiftLeft(rnd.copy().array(), shift)).resize(rnd.length(), BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_MAX_LENGTH).array();

            System.out.println("Original  \t" + rnd.encodeBinary() + " << " + shift);
            System.out.println("Expected \t" + Bytes.wrap(expected).encodeBinary());
            System.out.println("Actual   \t" + Bytes.wrap(actual).encodeBinary() + "\n\n");

            assertArrayEquals(expected, actual);
            assertEquals(Bytes.wrap(expected).encodeHex(), Bytes.wrap(actual).encodeHex());
        }
    }

    @Test
    public void testRightShift() {
        byte[] test = new byte[]{0, 0, 16, 0};
        assertEquals(0b01111110, 0b11111101 >>> 1);
        assertArrayEquals(new byte[]{0b00101101, (byte) 0b01111110}, Util.Byte.shiftRight(new byte[]{0b01011010, (byte) 0b11111101}, 1));
        assertArrayEquals(new byte[]{0, -128, -128, -128}, Util.Byte.shiftRight(new byte[]{1, 1, 1, 1}, 1));
        assertArrayEquals(new byte[]{0, -128, 66, 0}, Util.Byte.shiftRight(new byte[]{2, 1, 8, 2}, 2));
        assertArrayEquals(new byte[]{0, -128, 66, 0}, new BigInteger(new byte[]{2, 1, 8, 2}).shiftRight(2).toByteArray());
        assertArrayEquals(new byte[]{0, 0, 0, -128}, Util.Byte.shiftRight(Bytes.of(test).array(), 5));
        assertArrayEquals(new byte[]{0, 0, 0, -128}, Util.Byte.shiftRight(new byte[]{0, 0, 1, 0}, 1));
        assertArrayEquals(new byte[]{0, 0, 8, 0}, Util.Byte.shiftRight(Bytes.of(test).array(), 1));
        assertArrayEquals(new byte[]{0, 0, 4, 0}, Util.Byte.shiftRight(Bytes.of(test).array(), 2));
        assertArrayEquals(new byte[]{0, 0, 2, 0}, Util.Byte.shiftRight(Bytes.of(test).array(), 3));
        assertArrayEquals(new byte[]{0, 0, 1, 0}, Util.Byte.shiftRight(Bytes.of(test).array(), 4));

        assertSame(test, Util.Byte.shiftRight(test, 1));
    }

    @Test
    public void testRightShiftAgainstRefImpl() {
        for (int i = 0; i < 1000; i++) {
            int shift = new Random().nextInt(64);
            Bytes rnd = Bytes.random(4 + new Random().nextInt(12));
            if (!rnd.bitAt(rnd.lengthBit() - 1)) { //only unsigned
                byte[] expected = Bytes.of(new BigInteger(rnd.array()).shiftRight(shift).toByteArray()).resize(rnd.length(), BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_MAX_LENGTH).array();
                byte[] actual = Bytes.of(Util.Byte.shiftRight(rnd.copy().array(), shift)).resize(rnd.length(), BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_MAX_LENGTH).array();

//                System.out.println("Original  \t" + rnd.encodeBinary() + " >> " + shift);
//                System.out.println("Expected \t" + Bytes.wrap(expected).encodeBinary());
//                System.out.println("Actual   \t" + Bytes.wrap(actual).encodeBinary() + "\n\n");

                assertArrayEquals(expected, actual);
                assertEquals(Bytes.wrap(expected).encodeHex(), Bytes.wrap(actual).encodeHex());
            }
        }
    }

    @Test
    public void entropy() {
        assertEquals(0, Util.Byte.entropy(new byte[0]), 0.1d);
        assertEquals(0, Util.Byte.entropy(new byte[1]), 0.1d);
        assertEquals(0, Util.Byte.entropy(new byte[256]), 0.1d);
        assertEquals(0, Util.Byte.entropy(new byte[]{1}), 0.1d);
        assertTrue(Util.Byte.entropy(new byte[]{(byte) 0x8E, (byte) 0xD1, (byte) 0xFD, (byte) 0xAA, 0x12, (byte) 0xAF, (byte) 0x78, 0x09, 0x1E, (byte) 0xD1, (byte) 0xFD, (byte) 0xAA, 0x12, (byte) 0xAF, (byte) 0x00, 0x0A, (byte) 0xEE, (byte) 0xD1, (byte) 0xFD, (byte) 0xAA, 0x12, (byte) 0xAF, (byte) 0x78, 0x11}) > 3.5);
        assertTrue(Util.Byte.entropy(new byte[]{0x4A, (byte) 0x94, (byte) 0xFD, (byte) 0xFF, 0x1E, (byte) 0xAF, (byte) 0xED}) > 2.5);
        assertTrue(Util.Byte.entropy(new byte[]{0x1A, 0x6F}) > 0.5);
    }
}
