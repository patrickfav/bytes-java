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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.Assert.*;

public class BytesMiscTest extends ABytesTest {

    @Test
    public void testToString() {
        testToString(Bytes.wrap(new byte[0]));
        testToString(Bytes.wrap(example_bytes_one));
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
    public void testHashcode() {
        assertEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.from(example_bytes_seven).hashCode());
        assertEquals(Bytes.wrap(example2_bytes_seven).hashCode(), Bytes.from(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example_bytes_eight).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(0, Bytes.wrap(example2_bytes_seven).hashCode());
    }

    @Test
    public void testEquals() {
        assertTrue(Bytes.wrap(new byte[0]).equals(Bytes.wrap(new byte[0])));
        assertTrue(Bytes.wrap(new byte[16]).equals(Bytes.wrap(new byte[16])));
        assertTrue(Bytes.wrap(example_bytes_seven).equals(Bytes.from(example_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_seven).byteOrder(ByteOrder.BIG_ENDIAN).equals(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.wrap(example2_bytes_seven).equals(Bytes.from(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_seven).equals(Bytes.wrap(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_eight).equals(Bytes.wrap(example2_bytes_seven)));
    }

    @Test
    public void testEqualsWithArray() {
        assertTrue(Bytes.allocate(4).equals(new byte[4]));
        assertFalse(Bytes.allocate(4).equals(new byte[3]));
        assertFalse(Bytes.random(16).equals(new byte[16]));
    }

    @Test
    public void testEqualsWithConstantTime() {
        assertTrue(Bytes.allocate(4).equalsConstantTime(new byte[4]));
        assertFalse(Bytes.allocate(4).equalsConstantTime(new byte[3]));
        assertFalse(Bytes.random(16).equalsConstantTime(new byte[16]));
    }

    @Test
    public void testEqualsWithObjectArray() {
        assertFalse(Bytes.allocate(4).equals(new Byte[4]));
        assertTrue(Bytes.allocate(4).equals(new Byte[]{0, 0, 0, 0}));
        assertFalse(Bytes.allocate(4).equals(new Byte[3]));
        assertFalse(Bytes.random(16).equals(new Byte[16]));
    }

    @Test
    public void testEqualsWithByteBuffer() {
        assertTrue(Bytes.allocate(4).equals(ByteBuffer.wrap(new byte[4])));
        assertFalse(Bytes.allocate(4).equals(ByteBuffer.wrap(new byte[3])));
        assertFalse(Bytes.random(16).equals(ByteBuffer.wrap(new byte[16])));
        assertTrue(Bytes.allocate(16).byteOrder(ByteOrder.LITTLE_ENDIAN).equals(ByteBuffer.wrap(new byte[16]).order(ByteOrder.LITTLE_ENDIAN)));
        assertFalse(Bytes.wrap(new byte[]{3, 2}).byteOrder(ByteOrder.BIG_ENDIAN).equals(ByteBuffer.wrap(new byte[]{3, 2}).order(ByteOrder.LITTLE_ENDIAN)));
    }

    @Test
    public void testEqualsContent() {
        assertTrue(Bytes.wrap(new byte[0]).byteOrder(ByteOrder.BIG_ENDIAN).equalsContent(Bytes.wrap(new byte[0]).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.BIG_ENDIAN).equalsContent(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.from(example_bytes_seven).mutable().equalsContent(Bytes.from(example_bytes_seven).byteOrder(ByteOrder.LITTLE_ENDIAN)));
        assertTrue(Bytes.from(example_bytes_seven).mutable().equalsContent(Bytes.from(example_bytes_seven)));
        assertTrue(Bytes.from(example_bytes_seven).readOnly().equalsContent(Bytes.from(example_bytes_seven)));
    }

    @Test
    public void testCompareTo() {
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
    public void testLength() {
        assertEquals(0, Bytes.from(new byte[0]).length());

        for (int i = 0; i < 128; i++) {
            assertEquals(i, Bytes.from(new byte[i]).length());
            assertEquals(i * 8, Bytes.from(new byte[i]).lengthBit());
            assertEquals(i, Bytes.allocate(i).length());
        }
    }

    @Test
    public void testIsEmpty() {
        assertTrue(Bytes.from(new byte[0]).isEmpty());
        assertTrue(Bytes.allocate(0).isEmpty());
        assertFalse(Bytes.from(new byte[1]).isEmpty());
        assertFalse(Bytes.allocate(1).isEmpty());
        assertFalse(Bytes.from(example_bytes_seven).isEmpty());
    }

    @Test
    public void containsTest() {
        assertEquals(false, Bytes.allocate(0).contains((byte) 0xFD));
        assertEquals(true, Bytes.allocate(128).contains((byte) 0x00));
        assertEquals(true, Bytes.from(example_bytes_seven).contains((byte) 0xFD));
        assertEquals(true, Bytes.from(example_bytes_seven).contains((byte) 0xAF));
        assertEquals(false, Bytes.from(example_bytes_seven).contains((byte) 0x00));
    }

    @Test
    public void indexOfByte() {
        assertEquals(-1, Bytes.allocate(0).indexOf((byte) 0xFD));
        assertEquals(0, Bytes.allocate(128).indexOf((byte) 0x00));
        assertEquals(2, Bytes.from(example_bytes_seven).indexOf((byte) 0xFD));
        assertEquals(5, Bytes.from(example_bytes_seven).indexOf((byte) 0xAF));
        assertEquals(-1, Bytes.from(example_bytes_seven).indexOf((byte) 0x00));
    }

    @Test
    public void indexOfByteFromIndex() {
        assertEquals(-1, Bytes.allocate(0).indexOf((byte) 0xFD, 0));
        assertEquals(-1, Bytes.allocate(0).indexOf((byte) 0xFD, 100));
        assertEquals(5, Bytes.allocate(128).indexOf((byte) 0x00, 5));
        assertEquals(2, Bytes.from(example_bytes_sixteen).indexOf((byte) 0xFD, 0));
        assertEquals(10, Bytes.from(example_bytes_sixteen).indexOf((byte) 0xFD, 5));
    }


    @Test
    public void indexOfArray() {
        assertEquals(-1, Bytes.allocate(0).indexOf(new byte[]{(byte) 0xFD}));
        assertEquals(0, Bytes.allocate(1).indexOf(new byte[0]));
        assertEquals(2, Bytes.from(example_bytes_seven).indexOf(new byte[]{(byte) 0xFD, (byte) 0xFF}));
        assertEquals(-1, Bytes.from(example_bytes_seven).indexOf(new byte[]{(byte) 0xFD, (byte) 0x00}));
    }

    @Test
    public void lastIndexOf() {
        assertEquals(-1, Bytes.allocate(0).lastIndexOf((byte) 0xFD));
        assertEquals(127, Bytes.allocate(128).lastIndexOf((byte) 0x00));
        assertEquals(2, Bytes.from(example_bytes_seven).lastIndexOf((byte) 0xFD));
        assertEquals(5, Bytes.from(example_bytes_seven).lastIndexOf((byte) 0xAF));
        assertEquals(-1, Bytes.from(example_bytes_seven).lastIndexOf((byte) 0x00));
    }

    @Test
    public void bitAt() {

        for (int i = 0; i < 8; i++) {
            assertFalse(Bytes.allocate(1).bitAt(i));
        }

        for (int i = 0; i < 8; i++) {
            assertTrue(Bytes.from((byte) 0xFF).bitAt(i));
        }

        assertFalse(Bytes.from((byte) 8).bitAt(0));
        assertFalse(Bytes.from((byte) 8).bitAt(1));
        assertFalse(Bytes.from((byte) 8).bitAt(2));
        assertTrue(Bytes.from((byte) 8).bitAt(3));
        assertFalse(Bytes.from((byte) 8).bitAt(4));
        assertFalse(Bytes.from((byte) 0b11010000).bitAt(0));
        assertFalse(Bytes.from((byte) 0b10010000).bitAt(0));
        assertTrue(Bytes.from((byte) 0b10010001).bitAt(0));
        assertFalse(Bytes.from((byte) 0b0010_1000).bitAt(4));
        assertFalse(Bytes.parseBinary("101111110101100100110010011111001011101110110011011000010000000").bitAt(54));

        try {
            Bytes.allocate(1).bitAt(8);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Bytes.allocate(16).bitAt(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void byteAt() {
        assertEquals(0, Bytes.allocate(1).byteAt(0));
        assertEquals(0, Bytes.allocate(128).byteAt(127));
        assertEquals(-1, Bytes.from((byte) 0b1111_1111).byteAt(0));

        for (int i = 0; i < example_bytes_twentyfour.length; i++) {
            assertEquals(example_bytes_twentyfour[i], Bytes.wrap(example_bytes_twentyfour).byteAt(i));
        }

        try {
            Bytes.allocate(1).byteAt(1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Bytes.allocate(16).byteAt(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void unsignedByteAt() {
        assertEquals(0, Bytes.allocate(1).unsignedByteAt(0));
        assertEquals(0, Bytes.allocate(128).unsignedByteAt(127));
        assertEquals(255, Bytes.from((byte) 0b1111_1111).unsignedByteAt(0));

        try {
            Bytes.allocate(1).unsignedByteAt(1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Bytes.allocate(16).unsignedByteAt(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void charAt() {
        assertEquals(0, Bytes.allocate(2).charAt(0));
        assertEquals(0, Bytes.allocate(128).charAt(0));
        assertEquals(8, Bytes.wrap(new byte[]{0, 0b00001000}).charAt(0));
        assertEquals(2048, Bytes.wrap(new byte[]{0b00001000, 0}).charAt(0));
        assertEquals(32768, Bytes.wrap(new byte[]{(byte) 0b10000000, 0}).charAt(0));
        assertEquals(Character.MAX_VALUE, Bytes.wrap(new byte[]{(byte) 0b11111111, (byte) 0b11111111}).charAt(0));

        try {
            Bytes.allocate(1).charAt(0);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Bytes.allocate(16).charAt(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void shortAt() {
        assertEquals(0, Bytes.allocate(2).shortAt(0));
        assertEquals(0, Bytes.allocate(128).shortAt(0));
        assertEquals(8, Bytes.wrap(new byte[]{0, 0b00001000}).shortAt(0));
        assertEquals(2048, Bytes.wrap(new byte[]{0b00001000, 0}).shortAt(0));
        assertEquals(Short.MAX_VALUE, Bytes.wrap(new byte[]{(byte) 0b01111111, (byte) 0b11111111}).shortAt(0));
        assertEquals(Short.MIN_VALUE, Bytes.wrap(new byte[]{(byte) 0b10000000, 0}).shortAt(0));

        try {
            Bytes.allocate(1).shortAt(0);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Bytes.allocate(16).shortAt(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void intAt() {
        assertEquals(0, Bytes.allocate(4).intAt(0));
        assertEquals(0, Bytes.allocate(128).intAt(0));
        assertEquals(8, Bytes.wrap(new byte[]{0, 0, 0, 0b00001000}).intAt(0));
        assertEquals(2048, Bytes.wrap(new byte[]{0, 0, 0b00001000, 0}).intAt(0));
        assertEquals(32768, Bytes.wrap(new byte[]{0, 0, (byte) 0b10000000, 0}).intAt(0));
        assertEquals(Integer.MIN_VALUE, Bytes.wrap(new byte[]{(byte) 0b10000000, 0, 0, 0}).intAt(0));
        assertEquals(Integer.MAX_VALUE, Bytes.wrap(new byte[]{(byte) 0b01111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111}).intAt(0));

        try {
            Bytes.allocate(3).intAt(0);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Bytes.allocate(16).intAt(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void longAt() {
        assertEquals(0, Bytes.allocate(8).longAt(0));
        assertEquals(0, Bytes.allocate(128).longAt(0));
        assertEquals(8, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 0b00001000}).longAt(0));
        assertEquals(2048, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0b00001000, 0}).longAt(0));
        assertEquals(32768, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, (byte) 0b10000000, 0}).longAt(0));
        assertEquals(2147483648L, Bytes.wrap(new byte[]{0, 0, 0, 0, (byte) 0b10000000, 0, 0, 0}).longAt(0));
        assertEquals(Integer.MAX_VALUE, Bytes.wrap(new byte[]{0, 0, 0, 0, (byte) 0b01111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111}).longAt(0));

        try {
            Bytes.allocate(7).longAt(0);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Bytes.allocate(16).longAt(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void primitiveAtLittleEndian() {
        assertEquals(576460752303423488L, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 0b00001000}).byteOrder(ByteOrder.LITTLE_ENDIAN).longAt(0)); //2^59
        assertEquals(134217728, Bytes.wrap(new byte[]{0, 0, 0, 0b00001000}).byteOrder(ByteOrder.LITTLE_ENDIAN).intAt(0));
        assertEquals(2048, Bytes.wrap(new byte[]{0, 0b00001000}).byteOrder(ByteOrder.LITTLE_ENDIAN).shortAt(0)); //2^11
        assertEquals(2048, Bytes.wrap(new byte[]{0, 0b00001000}).byteOrder(ByteOrder.LITTLE_ENDIAN).charAt(0)); //2^11
    }

    @Test
    public void bitAtAgainstRefImpl() {
        for (int i = 0; i < 1000; i++) {
            Bytes rnd = Bytes.random(4 + new Random().nextInt(8));
            int index = new Random().nextInt(rnd.lengthBit() - 1);
            assertEquals(new BigInteger(rnd.array()).testBit(index), rnd.bitAt(index));
        }
    }

    @Test
    public void count() {
        assertEquals(0, Bytes.allocate(0).count((byte) 0));
        assertEquals(1, Bytes.allocate(1).count((byte) 0));
        assertEquals(128, Bytes.allocate(128).count((byte) 0));
        assertEquals(3, Bytes.from(example_bytes_twentyfour).count((byte) 0xAA));
        assertEquals(1, Bytes.from(example_bytes_seven).count((byte) 0xAF));
    }

    @Test
    public void countByteArray() {
        assertEquals(0, Bytes.allocate(0).count(new byte[0]));
        assertEquals(0, Bytes.allocate(1).count(new byte[0]));
        assertEquals(0, Bytes.allocate(128).count(new byte[0]));
        assertEquals(128, Bytes.allocate(128).count(new byte[]{0}));
        assertEquals(3, Bytes.from(example_bytes_twentyfour).count(new byte[]{(byte) 0xFD}));
        assertEquals(3, Bytes.from(example_bytes_twentyfour).count(new byte[]{(byte) 0xD1}));
        assertEquals(0, Bytes.from(example_bytes_twentyfour).count(new byte[]{(byte) 0x22}));
        assertEquals(1, Bytes.from(example_bytes_eight).count(new byte[]{(byte) 0xAF}));
        assertEquals(0, Bytes.from(example_bytes_eight).count(new byte[]{(byte) 0xAF, 0x00}));
        assertEquals(0, Bytes.from(example_bytes_eight).count(new byte[]{(byte) 0xED}));
        assertEquals(0, Bytes.from(example_bytes_eight).count(new byte[]{(byte) 0x22}));
        assertEquals(2, Bytes.from(new byte[]{0, 1, 2, 3, 0, 1, 0}).count(new byte[]{0, 1}));
        assertEquals(1, Bytes.from(new byte[]{0, 1, 2, 3, 0, 1, 0}).count(new byte[]{0, 1, 2}));
        assertEquals(1, Bytes.from(new byte[]{0, 1, 2, 3, 0, 1, 0}).count(new byte[]{0, 1, 2, 3}));
        assertEquals(0, Bytes.from(new byte[]{0, 1, 2, 3, 0, 1, 0}).count(new byte[]{0, 1, 2, 0}));
    }

    @Test(expected = NullPointerException.class)
    public void countByteArrayShouldCheckArgument() {
        Bytes.allocate(1).count(null);
    }

    @Test
    public void entropy() {
        assertEquals(0, Bytes.allocate(0).entropy(), 0.1d);
        assertEquals(0, Bytes.allocate(1).entropy(), 0.1d);
        assertEquals(0, Bytes.allocate(256).entropy(), 0.1d);
        assertEquals(0, Bytes.from(new byte[]{1}).entropy(), 0.1d);
        assertTrue(Bytes.from(example_bytes_twentyfour).entropy() > 3.5);
        assertTrue(Bytes.from(example_bytes_seven).entropy() > 2.5);
        assertTrue(Bytes.from(example_bytes_two).entropy() > 0.5);
    }

    @Test
    public void readOnlyShouldKeepProperty() {
        ReadOnlyBytes b = Bytes.from(example_bytes_seven).readOnly();
        assertSame(b, b.readOnly());
        assertTrue(b.isReadOnly());
        assertTrue(b.copy().isReadOnly());
        assertTrue(b.duplicate().isReadOnly());
        assertTrue(b.reverse().isReadOnly());
        assertTrue(b.resize(7).isReadOnly());
        assertTrue(b.resize(6).isReadOnly());
        assertTrue(b.not().isReadOnly());
        assertTrue(b.leftShift(1).isReadOnly());
        assertTrue(b.rightShift(1).isReadOnly());
        assertTrue(b.and(Bytes.random(b.length())).isReadOnly());
        assertTrue(b.or(Bytes.random(b.length())).isReadOnly());
        assertTrue(b.xor(Bytes.random(b.length())).isReadOnly());
        assertTrue(b.append(3).isReadOnly());
        assertTrue(b.hashSha256().isReadOnly());
    }

    @Test
    public void readOnly() {
        assertFalse(Bytes.from(example_bytes_twentyfour).isReadOnly());
        assertTrue(Bytes.from(example_bytes_twentyfour).readOnly().isReadOnly());
        assertTrue(Bytes.from(example_bytes_twentyfour).readOnly().copy().isReadOnly());

        assertArrayEquals(example_bytes_twentyfour, Bytes.from(example_bytes_twentyfour).readOnly().internalArray());
        try {
            Bytes.from(example_bytes_twentyfour).readOnly().array();
            fail();
        } catch (ReadOnlyBufferException e) {
        }

        Bytes b = Bytes.from(example_bytes_twentyfour).readOnly();
        assertSame(b, b.readOnly());
    }

    @Test
    public void iteratorTest() {
        Bytes b = Bytes.wrap(example_bytes_seven);

        int counter = 0;
        for (Byte aByte : b) {
            assertEquals((Byte) example_bytes_seven[counter], aByte);
            counter++;
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iteratorTestRemoveNotPossible() {
        Bytes.wrap(example_bytes_seven).iterator().remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorNoElement() {
        Bytes.allocate(0).iterator().next();
    }
}
