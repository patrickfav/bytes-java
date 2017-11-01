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
    public void toObjectArray() throws Exception {
        checkArray(example_bytes_empty);
        checkArray(example_bytes_one);
        checkArray(example_bytes_two);
        checkArray(example_bytes_seven);
        checkArray(example_bytes_eight);
        checkArray(example_bytes_sixteen);
    }

    private void checkArray(byte[] array) {
        Byte[] byteArray = Bytes.from(array).toObjectArray();
        assertEquals(array.length, byteArray.length);
        for (int i = 0; i < array.length; i++) {
            assertEquals(byteArray[i], Byte.valueOf(array[i]));
        }
    }

    @Test
    public void toList() throws Exception {
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
    public void toBitSet() throws Exception {
        assertArrayEquals(example_bytes_empty, Bytes.from(example_bytes_empty).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_seven, Bytes.from(example_bytes_seven).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_eight, Bytes.from(example_bytes_eight).toBitSet().toByteArray());
        assertArrayEquals(example_bytes_sixteen, Bytes.from(example_bytes_sixteen).toBitSet().toByteArray());
    }

    @Test
    public void toByte() throws Exception {
        assertEquals(example_bytes_one[0], Bytes.from(example_bytes_one).toByte());
        assertEquals((byte) 0, Bytes.from(new byte[1]).toByte());

        try {
            Bytes.from(example_bytes_two).toByte();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void toChar() throws Exception {
        assertEquals(6767, Bytes.from(example_bytes_two).toChar());
        assertEquals(Bytes.from(example_bytes_two).toShort(), Bytes.from(example_bytes_two).toChar());
        assertEquals((char) 0, Bytes.from(new byte[2]).toChar());

        try {
            Bytes.from(new byte[3]).toChar();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void toShort() throws Exception {
        assertEquals(6767, Bytes.from(example_bytes_two).toShort());
        assertEquals(Bytes.from(example_bytes_one).toByte(), Bytes.from(example_bytes_one).toShort());
        assertEquals((short) 0, Bytes.from(new byte[2]).toShort());

        try {
            Bytes.from(new byte[3]).toShort();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void toInt() throws Exception {
        assertEquals(591065872, Bytes.from(example_bytes_four).toInt());
        assertEquals(Bytes.from(example_bytes_four).toLong(), Bytes.from(example_bytes_four).toInt());

        System.out.println(Bytes.from(new byte[]{0x01, 0x02}).resize(4).encodeHex());
        System.out.println(Bytes.from(new byte[]{0x01, 0x02, 0x03, 0x04}).resize(4).encodeHex());

        assertEquals(6767, Bytes.from(example_bytes_two).toInt());
        assertEquals(0, Bytes.from(new byte[4]).toInt());

        try {
            Bytes.from(new byte[5]).toInt();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void toLong() throws Exception {
        assertEquals(-1237929515650418679L, Bytes.from(example_bytes_eight).toLong());

        assertEquals(example_bytes_one[0], Bytes.from(example_bytes_one).toLong());
        assertEquals(6767, Bytes.from(example_bytes_two).toLong());
        assertEquals(0, Bytes.from(new byte[4]).toLong());
        assertEquals(0, Bytes.from(new byte[8]).toLong());

        try {
            Bytes.from(new byte[9]).toLong();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }
}