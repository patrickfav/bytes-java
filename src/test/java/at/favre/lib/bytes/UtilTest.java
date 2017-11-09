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

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class UtilTest {
    private static final byte[] EMPTY = {};
    private static final byte[] ARRAY1 = {(byte) 1};
    private static final byte[] ARRAY234 = {(byte) 2, (byte) 3, (byte) 4};

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
        return Util.indexOf(empty, b, 0, empty.length);
    }

    @Test
    public void testIndexOf_arrayTarget() {
        assertEquals(0, Util.indexOf(EMPTY, EMPTY));
        assertEquals(0, Util.indexOf(ARRAY234, EMPTY));
        assertEquals(-1, Util.indexOf(EMPTY, ARRAY234));
        assertEquals(-1, Util.indexOf(ARRAY234, ARRAY1));
        assertEquals(-1, Util.indexOf(ARRAY1, ARRAY234));
        assertEquals(0, Util.indexOf(ARRAY1, ARRAY1));
        assertEquals(0, Util.indexOf(ARRAY234, ARRAY234));
        assertEquals(0, Util.indexOf(
                ARRAY234, new byte[]{(byte) 2, (byte) 3}));
        assertEquals(1, Util.indexOf(
                ARRAY234, new byte[]{(byte) 3, (byte) 4}));
        assertEquals(1, Util.indexOf(ARRAY234, new byte[]{(byte) 3}));
        assertEquals(2, Util.indexOf(ARRAY234, new byte[]{(byte) 4}));
        assertEquals(1, Util.indexOf(new byte[]{(byte) 2, (byte) 3,
                        (byte) 3, (byte) 3, (byte) 3},
                new byte[]{(byte) 3}
        ));
        assertEquals(2, Util.indexOf(
                new byte[]{(byte) 2, (byte) 3, (byte) 2,
                        (byte) 3, (byte) 4, (byte) 2, (byte) 3},
                new byte[]{(byte) 2, (byte) 3, (byte) 4}
        ));
        assertEquals(1, Util.indexOf(
                new byte[]{(byte) 2, (byte) 2, (byte) 3,
                        (byte) 4, (byte) 2, (byte) 3, (byte) 4},
                new byte[]{(byte) 2, (byte) 3, (byte) 4}
        ));
        assertEquals(-1, Util.indexOf(
                new byte[]{(byte) 4, (byte) 3, (byte) 2},
                new byte[]{(byte) 2, (byte) 3, (byte) 4}
        ));
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
        return Util.lastIndexOf(empty, b, 0, empty.length);
    }

    @Test
    public void testConcat() {
        assertTrue(Arrays.equals(EMPTY, Util.concat()));
        assertTrue(Arrays.equals(EMPTY, Util.concat(EMPTY)));
        assertTrue(Arrays.equals(EMPTY, Util.concat(EMPTY, EMPTY, EMPTY)));
        assertTrue(Arrays.equals(ARRAY1, Util.concat(ARRAY1)));
        assertNotSame(ARRAY1, Util.concat(ARRAY1));
        assertTrue(Arrays.equals(ARRAY1, Util.concat(EMPTY, ARRAY1, EMPTY)));
        assertTrue(Arrays.equals(
                new byte[]{(byte) 1, (byte) 1, (byte) 1},
                Util.concat(ARRAY1, ARRAY1, ARRAY1)));
        assertTrue(Arrays.equals(
                new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4},
                Util.concat(ARRAY1, ARRAY234)));
    }

    @Test
    public void testToArray() {
        // need explicit type parameter to avoid javac warning!?
        List<Byte> none = Arrays.asList();
        assertTrue(Arrays.equals(EMPTY, Util.toArray(none)));

        List<Byte> one = Arrays.asList((byte) 1);
        assertTrue(Arrays.equals(ARRAY1, Util.toArray(one)));

        byte[] array = {(byte) 0, (byte) 1, (byte) 0x55};

        List<Byte> three = Arrays.asList((byte) 0, (byte) 1, (byte) 0x55);
        assertTrue(Arrays.equals(array, Util.toArray(three)));

        assertTrue(Arrays.equals(array, Util.toArray(Util.toList(array))));
    }

    @Test
    public void testToArray_withNull() {
        List<Byte> list = Arrays.asList((byte) 0, (byte) 1, null);
        try {
            Util.toArray(list);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testToArray_withConversion() {
        byte[] array = {(byte) 0, (byte) 1, (byte) 2};

        List<Byte> bytes = Arrays.asList((byte) 0, (byte) 1, (byte) 2);
        assertTrue(Arrays.equals(array, Util.toArray(bytes)));
    }

    @Test
    public void testAsList_toArray_roundTrip() {
        byte[] array = {(byte) 0, (byte) 1, (byte) 2};
        List<Byte> list = Util.toList(array);
        byte[] newArray = Util.toArray(list);

        // Make sure it returned a copy
        list.set(0, (byte) 4);
        assertTrue(Arrays.equals(
                new byte[]{(byte) 0, (byte) 1, (byte) 2}, newArray));
        newArray[1] = (byte) 5;
        assertEquals((byte) 1, (byte) list.get(1));
    }

    @Test
    // This test stems from a real bug found by andrewk
    public void testAsList_subList_toArray_roundTrip() {
        byte[] array = {(byte) 0, (byte) 1, (byte) 2, (byte) 3};
        List<Byte> list = Util.toList(array);
        assertTrue(Arrays.equals(new byte[]{(byte) 1, (byte) 2},
                Util.toArray(list.subList(1, 3))));
        assertTrue(Arrays.equals(new byte[]{},
                Util.toArray(list.subList(2, 2))));
    }

    @Test(expected = IllegalStateException.class)
    public void readFromStream() throws Exception {
        Util.readFromStream(null);
    }

    @Test
    public void concatVararg() throws Exception {
        assertArrayEquals(new byte[]{1}, Util.concatVararg((byte) 1, null));
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
        Util.reverse(input, 0, input.length);
        assertTrue(Arrays.equals(expectedOutput, input));
    }

    private static void testReverse(byte[] input, int fromIndex, int toIndex, byte[] expectedOutput) {
        input = Arrays.copyOf(input, input.length);
        Util.reverse(input, fromIndex, toIndex);
        assertTrue(Arrays.equals(expectedOutput, input));
    }
}