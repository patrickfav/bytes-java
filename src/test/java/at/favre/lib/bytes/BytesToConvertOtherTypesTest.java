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
import java.util.List;

import static org.junit.Assert.*;

public class BytesToConvertOtherTypesTest extends ABytesTest {

    @Test
    public void toObjectArray() {
        checkArray(example_bytes_empty);
        checkArray(example_bytes_one);
        checkArray(example_bytes_two);
        checkArray(example_bytes_seven);
        checkArray(example_bytes_eight);
        checkArray(example_bytes_sixteen);
    }

    private void checkArray(byte[] array) {
        Byte[] byteArray = Bytes.of(array).toBoxedArray();
        assertEquals(array.length, byteArray.length);
        for (int i = 0; i < array.length; i++) {
            assertEquals(byteArray[i], Byte.valueOf(array[i]));
        }
        assertArrayEquals(byteArray, Bytes.of(array).toBoxedArray());
    }

    @Test
    public void toList() {
        checkList(example_bytes_empty);
        checkList(example_bytes_one);
        checkList(example_bytes_two);
        checkList(example_bytes_seven);
        checkList(example_bytes_eight);
        checkList(example_bytes_sixteen);
    }

    private void checkList(byte[] array) {
        List<Byte> byteList = Bytes.of(array).toList();
        assertEquals(array.length, byteList.size());
        for (int i = 0; i < array.length; i++) {
            assertEquals(byteList.get(i), Byte.valueOf(array[i]));
        }
    }

    @Test
    public void toBitSet() {
        assertArrayEquals(example_bytes_empty, Bytes.of(example_bytes_empty).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_one, Bytes.of(example_bytes_one).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_two, Bytes.of(example_bytes_two).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_seven, Bytes.of(example_bytes_seven).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_eight, Bytes.of(example_bytes_eight).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_sixteen, Bytes.of(example_bytes_sixteen).toBitSet().toByteArray());
    }

    @Test
    public void toByte() {
        assertEquals(example_bytes_one[0], Bytes.of(example_bytes_one).toByte());
        assertEquals((byte) 0, Bytes.of(new byte[1]).toByte());
        assertEquals(-1, Bytes.of((byte) 0b1111_1111).toByte());

        try {
            Bytes.of(example_bytes_two).toByte();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void toUnsignedByte() {
        assertEquals(example_bytes_one[0], Bytes.of(example_bytes_one).toUnsignedByte());
        assertEquals((byte) 0, Bytes.of(new byte[1]).toUnsignedByte());
        assertEquals(255, Bytes.of((byte) 0b1111_1111).toUnsignedByte());

        try {
            Bytes.of(example_bytes_two).toUnsignedByte();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void toChar() {
        assertEquals(6767, Bytes.of(example_bytes_two).toChar());
        assertEquals(Bytes.of(example_bytes_two).toShort(), Bytes.of(example_bytes_two).toChar());
        assertEquals((char) 0, Bytes.of(new byte[2]).toChar());

        try {
            Bytes.of(new byte[3]).toChar();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Bytes.of(new byte[1]).toChar();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void toShort() {
        assertEquals(6767, Bytes.of(example_bytes_two).toShort());
        assertEquals(Bytes.of(example_bytes_one).toByte(), Bytes.of((byte) 0, example_bytes_one).toShort());
        assertEquals((short) 0, Bytes.of(new byte[2]).toShort());

        try {
            Bytes.of(new byte[3]).toShort();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Bytes.of(new byte[1]).toShort();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void toInt() {
        assertEquals(591065872, Bytes.of(example_bytes_four).toInt());
        assertEquals(Bytes.of(new byte[]{0, 0, 0, 0}, example_bytes_four).toLong(), Bytes.of(example_bytes_four).toInt());

        System.out.println(Bytes.of(new byte[]{0, 0, 0x01, 0x02}).resize(4).encodeHex());
        System.out.println(Bytes.of(new byte[]{0x01, 0x02, 0x03, 0x04}).resize(4).encodeHex());

        assertEquals(6767, Bytes.of(new byte[]{(byte) 0, (byte) 0}, example_bytes_two).toInt());
        assertEquals(0, Bytes.of(new byte[4]).toInt());

        try {
            Bytes.of(new byte[5]).toInt();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Bytes.of(new byte[3]).toInt();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void toLong() {
        assertEquals(-1237929515650418679L, Bytes.of(example_bytes_eight).toLong());

        assertEquals(example_bytes_one[0], Bytes.of(new byte[]{0, 0, 0, 0, 0, 0, 0}, example_bytes_one).toLong());
        assertEquals(6767, Bytes.of(new byte[]{0, 0, 0, 0, 0, 0}, example_bytes_two).toLong());
        assertEquals(0, Bytes.of(new byte[8]).toLong());

        try {
            Bytes.of(new byte[9]).toLong();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Bytes.of(new byte[7]).toLong();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void toFloat() {
        assertEquals(1.0134550690550691E-17, Bytes.of(example_bytes_four).toFloat(), 0.001);

        assertEquals(5.1E-322, Bytes.of(new byte[]{0, 0, 0}, example_bytes_one).toFloat(), 0.001);
        assertEquals(3.3433E-320, Bytes.of(new byte[]{0, 0}, example_bytes_two).toFloat(), 0.001);
        assertEquals(0, Bytes.of(new byte[4]).toFloat(), 0);

        try {
            Bytes.of(new byte[5]).toFloat();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Bytes.of(new byte[3]).toFloat();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void toDouble() {
        assertEquals(-6.659307728279082E225, Bytes.of(example_bytes_eight).toDouble(), 0.001);

        assertEquals(5.1E-322, Bytes.of(new byte[]{0, 0, 0, 0, 0, 0, 0}, example_bytes_one).toDouble(), 0.001);
        assertEquals(3.3433E-320, Bytes.of(new byte[]{0, 0, 0, 0, 0, 0}, example_bytes_two).toDouble(), 0.001);
        assertEquals(0, Bytes.of(new byte[8]).toDouble(), 0);

        try {
            Bytes.of(new byte[9]).toDouble();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Bytes.of(new byte[7]).toDouble();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testToUUIDToLong() {
        Bytes.random(17).toUUID();
    }

    @Test(expected = IllegalStateException.class)
    public void testToUUIDToShort() {
        Bytes.random(15).toUUID();
    }

    @Test(expected = IllegalStateException.class)
    public void testToUUIDEmpty() {
        Bytes.allocate(0).toUUID();
    }

    @Test
    public void testToIntArray() {
        assertArrayEquals(new int[]{1}, Bytes.wrap(new byte[]{0, 0, 0, 1}).toIntArray());
        assertArrayEquals(new int[]{257}, Bytes.wrap(new byte[]{0, 0, 1, 1}).toIntArray());
        assertArrayEquals(new int[]{65_793}, Bytes.wrap(new byte[]{0, 1, 1, 1}).toIntArray());
        assertArrayEquals(new int[]{16_843_009}, Bytes.wrap(new byte[]{1, 1, 1, 1}).toIntArray());
        assertArrayEquals(new int[]{571_211_845}, Bytes.wrap(new byte[]{34, 12, 0, 69}).toIntArray());
        assertArrayEquals(new int[]{1_290_429_439}, Bytes.wrap(new byte[]{76, (byte) 234, 99, (byte) 255}).toIntArray());

        assertArrayEquals(new int[]{1, 1}, Bytes.wrap(new byte[]{0, 0, 0, 1, 0, 0, 0, 1}).toIntArray());
        assertArrayEquals(new int[]{257, 1}, Bytes.wrap(new byte[]{0, 0, 1, 1, 0, 0, 0, 1}).toIntArray());
        assertArrayEquals(new int[]{257, 65_793, 1}, Bytes.wrap(new byte[]{0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1}).toIntArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToIntArrayNotMod4Was5Byte() {
        Bytes.wrap(new byte[]{1, 0, 0, 0, 1}).toIntArray();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToIntArrayNotMod4Only3Byte() {
        Bytes.wrap(new byte[]{0, 0, 1}).toIntArray();
    }

    @Test
    public void testToIntEmptyArray() {
        assertArrayEquals(new int[0], Bytes.empty().toIntArray());
    }

    @Test
    public void testToIntArrayLittleEndian() {
        assertArrayEquals(new int[]{1}, Bytes.wrap(new byte[]{1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toIntArray());
        assertArrayEquals(new int[]{257}, Bytes.wrap(new byte[]{1, 1, 0, 0}, ByteOrder.LITTLE_ENDIAN).toIntArray());
        assertArrayEquals(new int[]{1_290_429_439}, Bytes.wrap(new byte[]{(byte) 255, 99, (byte) 234, 76}, ByteOrder.LITTLE_ENDIAN).toIntArray());

        assertArrayEquals(new int[]{1, 1}, Bytes.wrap(new byte[]{1, 0, 0, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toIntArray());
        assertArrayEquals(new int[]{257, 1}, Bytes.wrap(new byte[]{1, 1, 0, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toIntArray());
        assertArrayEquals(new int[]{257, 65_793, 1}, Bytes.wrap(new byte[]{1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toIntArray());
    }

    @Test
    public void testToLongArray() {
        assertArrayEquals(new long[]{1}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 1}).toLongArray());
        assertArrayEquals(new long[]{257}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 1, 1}).toLongArray());
        assertArrayEquals(new long[]{3329575617569751109L}, Bytes.wrap(new byte[]{46, 53, 7, 98, 34, 12, 0, 69}).toLongArray());
        assertArrayEquals(new long[]{-7124130559744646145L}, Bytes.wrap(new byte[]{(byte) 157, 34, 1, 0, 76, (byte) 234, 99, (byte) 255}).toLongArray());

        assertArrayEquals(new long[]{1, 1}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1}).toLongArray());
        assertArrayEquals(new long[]{257, 1}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1}).toLongArray());
        assertArrayEquals(new long[]{1099511628033L, 281474976776449L, 1}, Bytes.wrap(new byte[]{
                0, 0, 1, 0, 0, 0, 1, 1,
                0, 1, 0, 0, 0, 1, 1, 1,
                0, 0, 0, 0, 0, 0, 0, 1}).toLongArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToLongArrayNotMod4Was9Byte() {
        Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 1}).toLongArray();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToLongArrayNotMod4Only7Byte() {
        Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 1}).toLongArray();
    }

    @Test
    public void testToLongEmptyArray() {
        assertArrayEquals(new long[0], Bytes.empty().toLongArray());
    }

    @Test
    public void testToLongArrayLittleEndian() {
        assertArrayEquals(new long[]{1}, Bytes.wrap(new byte[]{1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toLongArray());
        assertArrayEquals(new long[]{257}, Bytes.wrap(new byte[]{1, 1, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toLongArray());
        assertArrayEquals(new long[]{3329575617569751109L}, Bytes.wrap(new byte[]{69, 0, 12, 34, 98, 7, 53, 46}, ByteOrder.LITTLE_ENDIAN).toLongArray());
        assertArrayEquals(new long[]{-7124130559744646145L}, Bytes.wrap(new byte[]{(byte) 255, 99, (byte) 234, 76, 0, 1, 34, (byte) 157}, ByteOrder.LITTLE_ENDIAN).toLongArray());

        assertArrayEquals(new long[]{1, 1}, Bytes.wrap(new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toLongArray());
        assertArrayEquals(new long[]{257, 1}, Bytes.wrap(new byte[]{1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toLongArray());
        assertArrayEquals(new long[]{1099511628033L, 281474976776449L, 1}, Bytes.wrap(new byte[]{
                1, 1, 0, 0, 0, 1, 0, 0,
                1, 1, 1, 0, 0, 0, 1, 0,
                1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toLongArray());
    }

    @Test
    public void testToFloatArray() {
        assertArrayEquals(new float[]{1.4E-45f}, Bytes.wrap(new byte[]{0, 0, 0, 1}).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f}, Bytes.wrap(new byte[]{0, 0, 1, 1}).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{9.2196E-41f}, Bytes.wrap(new byte[]{0, 1, 1, 1}).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{2.3694278E-38f}, Bytes.wrap(new byte[]{1, 1, 1, 1}).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{1.897368E-18f}, Bytes.wrap(new byte[]{34, 12, 0, 69}).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{1.22888184E8f}, Bytes.wrap(new byte[]{76, (byte) 234, 99, (byte) 255}).toFloatArray(), 0.01f);

        assertArrayEquals(new float[]{1.4E-45f, 1.4E-45f}, Bytes.wrap(new byte[]{0, 0, 0, 1, 0, 0, 0, 1}).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f, 1.4E-45f}, Bytes.wrap(new byte[]{0, 0, 1, 1, 0, 0, 0, 1}).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f, 9.2196E-41f, 1.4E-45f}, Bytes.wrap(new byte[]{0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1}).toFloatArray(), 0.01f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToFloatArrayNotMod4Was5Byte() {
        Bytes.wrap(new byte[]{1, 0, 0, 0, 1}).toFloatArray();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToFloatArrayNotMod4Only3Byte() {
        Bytes.wrap(new byte[]{0, 0, 1}).toFloatArray();
    }

    @Test
    public void testToFloatEmptyArray() {
        assertArrayEquals(new float[0], Bytes.empty().toFloatArray(), 0.01f);
    }

    @Test
    public void testToFloatArrayLittleEndian() {
        assertArrayEquals(new float[]{1.4E-45f}, Bytes.wrap(new byte[]{1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f}, Bytes.wrap(new byte[]{1, 1, 0, 0}, ByteOrder.LITTLE_ENDIAN).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{1.22888184E8f}, Bytes.wrap(new byte[]{(byte) 255, 99, (byte) 234, 76}, ByteOrder.LITTLE_ENDIAN).toFloatArray(), 0.01f);

        assertArrayEquals(new float[]{1.4E-45f, 1.4E-45f}, Bytes.wrap(new byte[]{1, 0, 0, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f, 1.4E-45f}, Bytes.wrap(new byte[]{1, 1, 0, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toFloatArray(), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f, 9.2196E-41f, 1.4E-45f}, Bytes.wrap(new byte[]{1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toFloatArray(), 0.01f);
    }

    @Test
    public void testToDoubleArray() {
        assertArrayEquals(new double[]{1.4E-45}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 1}).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{3.6E-43}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 1, 1}).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{4.228405109821336E-86}, Bytes.wrap(new byte[]{46, 53, 7, 98, 34, 12, 0, 69}).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{-2.385279556059394E-168}, Bytes.wrap(new byte[]{(byte) 157, 34, 1, 0, 76, (byte) 234, 99, (byte) 255}).toDoubleArray(), 0.01);

        assertArrayEquals(new double[]{1.4E-45, 1.4E-45}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1}).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{3.6E-43, 1.4E-45}, Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1}).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{5.43230922614E-312, 1.39067116189206E-309, 1.4E-45}, Bytes.wrap(new byte[]{
                0, 0, 1, 0, 0, 0, 1, 1,
                0, 1, 0, 0, 0, 1, 1, 1,
                0, 0, 0, 0, 0, 0, 0, 1}).toDoubleArray(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToDoubleArrayNotMod4Was9Byte() {
        Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 1}).toDoubleArray();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToDoubleArrayNotMod4Only7Byte() {
        Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 1}).toDoubleArray();
    }

    @Test
    public void testToDoubleEmptyArray() {
        assertArrayEquals(new double[0], Bytes.empty().toDoubleArray(), 0.01);
    }

    @Test
    public void testToDoubleArrayLittleEndian() {
        assertArrayEquals(new double[]{1.4E-45}, Bytes.wrap(new byte[]{1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{3.6E-43}, Bytes.wrap(new byte[]{1, 1, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{4.228405109821336E-86}, Bytes.wrap(new byte[]{69, 0, 12, 34, 98, 7, 53, 46}, ByteOrder.LITTLE_ENDIAN).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{-2.385279556059394E-168}, Bytes.wrap(new byte[]{(byte) 255, 99, (byte) 234, 76, 0, 1, 34, (byte) 157}, ByteOrder.LITTLE_ENDIAN).toDoubleArray(), 0.01);

        assertArrayEquals(new double[]{1.4E-45, 1.4E-45}, Bytes.wrap(new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{3.6E-43f, 1.4E-45}, Bytes.wrap(new byte[]{1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toDoubleArray(), 0.01);
        assertArrayEquals(new double[]{5.43230922614E-312, 1.39067116189206E-309, 1.4E-45}, Bytes.wrap(new byte[]{
                1, 1, 0, 0, 0, 1, 0, 0,
                1, 1, 1, 0, 0, 0, 1, 0,
                1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN).toDoubleArray(), 0.01);
    }
}
