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
        Byte[] byteArray = Bytes.from(array).toBoxedArray();
        assertEquals(array.length, byteArray.length);
        for (int i = 0; i < array.length; i++) {
            assertEquals(byteArray[i], Byte.valueOf(array[i]));
        }
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
        List<Byte> byteList = Bytes.from(array).toList();
        assertEquals(array.length, byteList.size());
        for (int i = 0; i < array.length; i++) {
            assertEquals(byteList.get(i), Byte.valueOf(array[i]));
        }
    }

    @Test
    public void toBitSet() {
        assertArrayEquals(example_bytes_empty, Bytes.from(example_bytes_empty).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_seven, Bytes.from(example_bytes_seven).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_eight, Bytes.from(example_bytes_eight).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_sixteen, Bytes.from(example_bytes_sixteen).toBitSet().toByteArray());
    }

    @Test
    public void toByte() {
        assertEquals(example_bytes_one[0], Bytes.from(example_bytes_one).toByte());
        assertEquals((byte) 0, Bytes.from(new byte[1]).toByte());
        assertEquals(-1, Bytes.from((byte) 0b1111_1111).toByte());

        try {
            Bytes.from(example_bytes_two).toByte();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void toUnsignedByte() {
        assertEquals(example_bytes_one[0], Bytes.from(example_bytes_one).toUnsignedByte());
        assertEquals((byte) 0, Bytes.from(new byte[1]).toUnsignedByte());
        assertEquals(255, Bytes.from((byte) 0b1111_1111).toUnsignedByte());

        try {
            Bytes.from(example_bytes_two).toUnsignedByte();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void toChar() {
        assertEquals(6767, Bytes.from(example_bytes_two).toChar());
        assertEquals(Bytes.from(example_bytes_two).toShort(), Bytes.from(example_bytes_two).toChar());
        assertEquals((char) 0, Bytes.from(new byte[2]).toChar());

        try {
            Bytes.from(new byte[3]).toChar();
            fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            Bytes.from(new byte[1]).toChar();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void toShort() {
        assertEquals(6767, Bytes.from(example_bytes_two).toShort());
        assertEquals(Bytes.from(example_bytes_one).toByte(), Bytes.from((byte) 0, example_bytes_one).toShort());
        assertEquals((short) 0, Bytes.from(new byte[2]).toShort());

        try {
            Bytes.from(new byte[3]).toShort();
            fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            Bytes.from(new byte[1]).toShort();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void toInt() {
        assertEquals(591065872, Bytes.from(example_bytes_four).toInt());
        assertEquals(Bytes.from(new byte[]{0, 0, 0, 0}, example_bytes_four).toLong(), Bytes.from(example_bytes_four).toInt());

        System.out.println(Bytes.from(new byte[]{0, 0, 0x01, 0x02}).resize(4).encodeHex());
        System.out.println(Bytes.from(new byte[]{0x01, 0x02, 0x03, 0x04}).resize(4).encodeHex());

        assertEquals(6767, Bytes.from(new byte[]{(byte) 0, (byte) 0}, example_bytes_two).toInt());
        assertEquals(0, Bytes.from(new byte[4]).toInt());

        try {
            Bytes.from(new byte[5]).toInt();
            fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            Bytes.from(new byte[3]).toInt();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void toLong() {
        assertEquals(-1237929515650418679L, Bytes.from(example_bytes_eight).toLong());

        assertEquals(example_bytes_one[0], Bytes.from(new byte[]{0, 0, 0, 0, 0, 0, 0}, example_bytes_one).toLong());
        assertEquals(6767, Bytes.from(new byte[]{0, 0, 0, 0, 0, 0}, example_bytes_two).toLong());
        assertEquals(0, Bytes.from(new byte[8]).toLong());

        try {
            Bytes.from(new byte[9]).toLong();
            fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            Bytes.from(new byte[7]).toLong();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void toFloat() {
        assertEquals(1.0134550690550691E-17, Bytes.from(example_bytes_four).toFloat(), 0.001);

        assertEquals(5.1E-322, Bytes.from(new byte[]{0, 0, 0}, example_bytes_one).toFloat(), 0.001);
        assertEquals(3.3433E-320, Bytes.from(new byte[]{0, 0}, example_bytes_two).toFloat(), 0.001);
        assertEquals(0, Bytes.from(new byte[4]).toFloat(), 0);

        try {
            Bytes.from(new byte[5]).toFloat();
            fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            Bytes.from(new byte[3]).toFloat();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void toDouble() {
        assertEquals(-6.659307728279082E225, Bytes.from(example_bytes_eight).toDouble(), 0.001);

        assertEquals(5.1E-322, Bytes.from(new byte[]{0, 0, 0, 0, 0, 0, 0}, example_bytes_one).toDouble(), 0.001);
        assertEquals(3.3433E-320, Bytes.from(new byte[]{0, 0, 0, 0, 0, 0}, example_bytes_two).toDouble(), 0.001);
        assertEquals(0, Bytes.from(new byte[8]).toDouble(), 0);

        try {
            Bytes.from(new byte[9]).toDouble();
            fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            Bytes.from(new byte[7]).toDouble();
            fail();
        } catch (IllegalStateException ignored) {
        }
    }
}