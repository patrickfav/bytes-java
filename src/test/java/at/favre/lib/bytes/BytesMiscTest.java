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

import static org.junit.Assert.*;

public class BytesMiscTest extends ABytesTest {

    @Test
    public void testToString() throws Exception {
        testToString(Bytes.wrap(new byte[0]));
        testToString(Bytes.wrap(new byte[2]));
        testToString(Bytes.wrap(example_bytes_seven));
        testToString(Bytes.wrap(example2_bytes_seven));
        testToString(Bytes.wrap(example_bytes_eight));
        testToString(Bytes.wrap(example_bytes_sixteen));
    }

    private void testToString(Bytes bytes) {
        assertNotNull(bytes.toString());
        System.out.println(bytes.toString());
    }

    @Test
    public void testHashcode() throws Exception {
        assertEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.from(example_bytes_seven).hashCode());
        assertEquals(Bytes.wrap(example2_bytes_seven).hashCode(), Bytes.from(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example_bytes_eight).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(0, Bytes.wrap(example2_bytes_seven).hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(Bytes.wrap(new byte[0]).equals(Bytes.wrap(new byte[0])));
        assertTrue(Bytes.wrap(new byte[16]).equals(Bytes.wrap(new byte[16])));
        assertTrue(Bytes.wrap(example_bytes_seven).equals(Bytes.from(example_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_seven).byteOrder(ByteOrder.BIG_ENDIAN).equals(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.wrap(example2_bytes_seven).equals(Bytes.from(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_seven).equals(Bytes.wrap(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_eight).equals(Bytes.wrap(example2_bytes_seven)));
    }

    @Test
    public void testEqualsContent() throws Exception {
        assertTrue(Bytes.wrap(new byte[0]).byteOrder(ByteOrder.BIG_ENDIAN).equalsContent(Bytes.wrap(new byte[0]).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.BIG_ENDIAN).equalsContent(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.from(example_bytes_seven).mutable().equalsContent(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.from(example_bytes_seven).mutable().equalsContent(Bytes.from(example_bytes_seven)));
        assertTrue(Bytes.from(example_bytes_seven).readOnly().equalsContent(Bytes.from(example_bytes_seven)));
        assertTrue(Bytes.from(example_bytes_seven).readOnly().equalsContent(example_bytes_seven));
    }

    @Test
    public void testCompareTo() throws Exception {
        byte[] b1 = new byte[]{0x01};
        byte[] b2 = new byte[]{0x01, 0x02};

        assertTrue(-1 >= Bytes.from(b1).compareTo(Bytes.from(b2)));
        assertTrue(1 <= Bytes.from(b2).compareTo(Bytes.from(b1)));
        assertTrue(0 == Bytes.from(b1).compareTo(Bytes.from(b1)));

        byte[] bOne = new byte[]{0x01};
        byte[] bTwo = new byte[]{0x02};

        assertTrue(-1 >= Bytes.from(bOne).compareTo(Bytes.from(bTwo)));
        assertTrue(1 <= Bytes.from(bTwo).compareTo(Bytes.from(bOne)));
        assertTrue(0 == Bytes.from(bOne).compareTo(Bytes.from(bOne)));
    }

    @Test
    public void testLength() throws Exception {
        assertEquals(0, Bytes.from(new byte[0]).length());

        for (int i = 0; i < 128; i++) {
            assertEquals(i, Bytes.from(new byte[i]).length());
            assertEquals(i * 8, Bytes.from(new byte[i]).lengthBit());
            assertEquals(i, Bytes.allocate(i).length());
        }
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(Bytes.from(new byte[0]).isEmpty());
        assertTrue(Bytes.allocate(0).isEmpty());
        assertFalse(Bytes.from(new byte[1]).isEmpty());
        assertFalse(Bytes.allocate(1).isEmpty());
        assertFalse(Bytes.from(example_bytes_seven).isEmpty());
    }

    @Test
    public void indexOfByte() throws Exception {
        assertEquals(-1, Bytes.allocate(0).indexOf((byte) 0xFD));
        assertEquals(0, Bytes.allocate(128).indexOf((byte) 0x00));
        assertEquals(2, Bytes.from(example_bytes_seven).indexOf((byte) 0xFD));
        assertEquals(5, Bytes.from(example_bytes_seven).indexOf((byte) 0xAF));
        assertEquals(-1, Bytes.from(example_bytes_seven).indexOf((byte) 0x00));
    }

    @Test
    public void indexOfArray() throws Exception {
        assertEquals(-1, Bytes.allocate(0).indexOf(new byte[]{(byte) 0xFD}));
        assertEquals(0, Bytes.allocate(1).indexOf(new byte[0]));
        assertEquals(2, Bytes.from(example_bytes_seven).indexOf(new byte[]{(byte) 0xFD, (byte) 0xFF}));
        assertEquals(-1, Bytes.from(example_bytes_seven).indexOf(new byte[]{(byte) 0xFD, (byte) 0x00}));
    }

    @Test
    public void lastIndexOf() throws Exception {
        assertEquals(-1, Bytes.allocate(0).lastIndexOf((byte) 0xFD));
        assertEquals(127, Bytes.allocate(128).lastIndexOf((byte) 0x00));
        assertEquals(2, Bytes.from(example_bytes_seven).lastIndexOf((byte) 0xFD));
        assertEquals(5, Bytes.from(example_bytes_seven).lastIndexOf((byte) 0xAF));
        assertEquals(-1, Bytes.from(example_bytes_seven).lastIndexOf((byte) 0x00));
    }

    @Test
    public void byteAt() throws Exception {
        assertEquals(0, Bytes.allocate(1).byteAt(0));
        assertEquals(0, Bytes.allocate(128).byteAt(127));

        for (int i = 0; i < example_bytes_twentyfour.length; i++) {
            assertEquals(example_bytes_twentyfour[i], Bytes.wrap(example_bytes_twentyfour).byteAt(i));
        }

        try {
            assertEquals(0, Bytes.allocate(1).byteAt(1));
            fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void count() throws Exception {
        assertEquals(0, Bytes.allocate(0).count((byte) 0));
        assertEquals(1, Bytes.allocate(1).count((byte) 0));
        assertEquals(128, Bytes.allocate(128).count((byte) 0));
        assertEquals(3, Bytes.from(example_bytes_twentyfour).count((byte) 0xAA));
        assertEquals(1, Bytes.from(example_bytes_seven).count((byte) 0xAF));
    }

    @Test
    public void entropy() throws Exception {
        assertEquals(0, Bytes.allocate(0).entropy(), 0.1d);
        assertEquals(0, Bytes.allocate(1).entropy(), 0.1d);
        assertEquals(0, Bytes.allocate(256).entropy(), 0.1d);
        assertEquals(0, Bytes.from(new byte[]{1}).entropy(), 0.1d);
        assertTrue(Bytes.from(example_bytes_twentyfour).entropy() > 3.5);
        assertTrue(Bytes.from(example_bytes_seven).entropy() > 2.5);
        assertTrue(Bytes.from(example_bytes_two).entropy() > 0.5);
    }

    @Test
    public void readOnly() throws Exception {
        assertFalse(Bytes.from(example_bytes_twentyfour).isReadOnly());
        assertTrue(Bytes.from(example_bytes_twentyfour).readOnly().isReadOnly());
        assertTrue(Bytes.from(example_bytes_twentyfour).readOnly().copy().isReadOnly());
    }
}