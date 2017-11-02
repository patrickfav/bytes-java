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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BytesByteOrderTest extends ABytesTest {

    @Test
    public void miscInput() throws Exception {
        testOrder(Bytes.from(350));
        testOrder(Bytes.from(172863182736L));
        testOrder(Bytes.from(example_bytes_one));
        testOrder(Bytes.from(example_bytes_four));
        testOrder(Bytes.from(example_bytes_sixteen));
        testOrder(Bytes.from(example_bytes_twentyfour));
    }

    private void testOrder(Bytes bytes) {
        Bytes bigEndian = bytes.copy().byteOrder(ByteOrder.BIG_ENDIAN);
        Bytes littleEndian = bytes.copy().byteOrder(ByteOrder.LITTLE_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, bigEndian.byteOrder());
        assertEquals(ByteOrder.LITTLE_ENDIAN, littleEndian.byteOrder());
        assertNotEquals(bigEndian, littleEndian);

        System.out.println("big endian: " + bigEndian);
        System.out.println("little endian: " + littleEndian);
    }

    @Test
    public void encodeBinary() throws Exception {
        Bytes b = Bytes.from(example_bytes_four);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeBinary(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).encodeBinary());
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeBinary(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).reverse().encodeBinary());
    }

    @Test
    public void encodeOct() throws Exception {
        Bytes b = Bytes.from(example_bytes_four);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeOctal(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).encodeOctal());
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeOctal(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).reverse().encodeOctal());
    }

    @Test
    public void encodeDec() throws Exception {
        Bytes b = Bytes.from(example_bytes_four);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeDec(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).encodeDec());
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeDec(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).reverse().encodeDec());
    }

    @Test
    public void encodeHex() throws Exception {
        Bytes b = Bytes.from(example_bytes_two);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeHex(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).encodeHex());
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeHex(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).reverse().encodeHex());
    }

    @Test
    public void encodeBase36() throws Exception {
        Bytes b = Bytes.from(example_bytes_four);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeBase36(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).encodeBase36());
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeBase36(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).reverse().encodeBase36());
    }

    @Test
    public void encodeBase64() throws Exception {
        Bytes b = Bytes.from(example_bytes_four);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeBase64(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).encodeBase64());
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).encodeBase64(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).reverse().encodeBase64());
    }

    @Test
    public void toByte() throws Exception {
        Bytes b = Bytes.from(example_bytes_one);
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).toByte(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).toByte());
    }

    @Test
    public void toChar() throws Exception {
        Bytes b = Bytes.from(example_bytes_two);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).toChar(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).toChar());
    }

    @Test
    public void toShort() throws Exception {
        Bytes b = Bytes.from(example_bytes_two);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).toShort(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).toShort());
    }

    @Test
    public void toInt() throws Exception {
        Bytes b = Bytes.from(example_bytes_four);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).toInt(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).toInt());
    }

    @Test
    public void toLong() throws Exception {
        Bytes b = Bytes.from(example_bytes_eight);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).toLong(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).toLong());
    }

    @Test
    public void bigInteger() throws Exception {
        Bytes b = Bytes.from(example_bytes_four);
        assertNotEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).bigInteger(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).bigInteger());
        assertEquals(b.byteOrder(ByteOrder.BIG_ENDIAN).bigInteger(), b.byteOrder(ByteOrder.LITTLE_ENDIAN).reverse().bigInteger());
    }

}