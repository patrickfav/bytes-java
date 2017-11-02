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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BytesParseAndEncodingTest extends ABytesTest {
    private byte[] encodingExample;

    @Before
    public void setUp() throws Exception {
        encodingExample = new byte[]{0x4A, (byte) 0x94, (byte) 0xFD, (byte) 0xFF, 0x1E, (byte) 0xAF, (byte) 0xED};
    }

    @Test
    public void parseHex() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertArrayEquals(defaultArray, Bytes.parseHex("A0E1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex("a0e1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex("0xA0E1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex(Bytes.parseHex("A0E1").encodeHex()).array());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseHexInvalid() throws Exception {
        Bytes.parseHex("A0E");
    }

    @Test
    public void encodeHex() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("a0e1", Bytes.from(defaultArray).encodeHex());
        assertEquals("A0E1", Bytes.from(defaultArray).encodeHex(true));
        assertEquals(Bytes.from(defaultArray).encodeHex(), Bytes.from(defaultArray).encodeHex(false));
        assertEquals("4a94fdff1eafed", Bytes.from(encodingExample).encodeHex());
    }

    @Test
    public void parseBase64() throws Exception {
        assertArrayEquals(encodingExample, Bytes.parseBase64("SpT9/x6v7Q==").array());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseBase64Invalid() throws Exception {
        Bytes.parseBase64("☕");
    }

    @Test
    public void encodeBase64() throws Exception {
        assertEquals("SpT9/x6v7Q==", Bytes.from(encodingExample).encodeBase64());
    }

    @Test
    public void encodeBinary() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("1010000011100001", Bytes.from(defaultArray).encodeBinary());
        assertEquals("1001010100101001111110111111111000111101010111111101101", Bytes.from(encodingExample).encodeBinary());
    }

    @Test
    public void parseOctal() throws Exception {
        assertArrayEquals(encodingExample, Bytes.parseOctal("1124517677707527755").array());
    }

    @Test
    public void encodeOctal() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("120341", Bytes.from(defaultArray).encodeOctal());
        assertEquals("1124517677707527755", Bytes.from(encodingExample).encodeOctal());
    }

    @Test
    public void parseDec() throws Exception {
        assertArrayEquals(encodingExample, Bytes.parseDec("20992966904426477").array());
    }

    @Test
    public void encodeDec() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("41185", Bytes.from(defaultArray).encodeDec());
        assertEquals("20992966904426477", Bytes.from(encodingExample).encodeDec());
    }

    @Test
    public void parseBase36() throws Exception {
        assertArrayEquals(encodingExample, Bytes.parseBase36("5qpdvuwjvu5").array());
    }

    @Test
    public void encodeBase36() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1, (byte) 0x13};
        assertEquals("69zbn", Bytes.from(defaultArray).encodeBase36());
        assertEquals("5qpdvuwjvu5", Bytes.from(encodingExample).encodeBase36());
    }
}