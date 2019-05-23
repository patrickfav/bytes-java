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
    public void setUp() {
        encodingExample = new byte[]{0x4A, (byte) 0x94, (byte) 0xFD, (byte) 0xFF, 0x1E, (byte) 0xAF, (byte) 0xED};
    }

    @Test
    public void parseHex() {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertArrayEquals(defaultArray, Bytes.parseHex("0xA0E1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex("A0E1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex("a0e1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex(Bytes.parseHex("A0E1").encodeHex()).array());
        assertArrayEquals(defaultArray, Bytes.parseHex(Bytes.parseHex("a0E1").encodeHex()).array());
    }

    @Test
    public void parseHexOddNumberStrings() {
        assertArrayEquals(new byte[]{(byte) 0x00}, Bytes.parseHex("0").array());
        assertArrayEquals(new byte[]{(byte) 0x0E}, Bytes.parseHex("E").array());
        assertArrayEquals(new byte[]{(byte) 0x0A, (byte) 0x0E}, Bytes.parseHex("A0E").array());
        assertArrayEquals(new byte[]{(byte) 0x03, (byte) 0xEA, (byte) 0x0E}, Bytes.parseHex("3EA0E").array());
        assertArrayEquals(new byte[]{(byte) 0x00, (byte) 0xF3, (byte) 0xEA, (byte) 0x0E}, Bytes.parseHex("0F3EA0E").array());
        assertArrayEquals(new byte[]{(byte) 0x0A, (byte) 0xD0, (byte) 0xF3, (byte) 0xEA, (byte) 0x0E}, Bytes.parseHex("AD0F3EA0E").array());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseHexIllegalChars() {
        Bytes.parseHex("AX");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseHexIllegalChars2() {
        Bytes.parseHex("XX");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseHexIllegalChars3() {
        Bytes.parseHex("Y");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseHexIllegalChars4() {
        Bytes.parseHex("F3El0E");
    }

    @Test
    public void encodeHex() {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("a0e1", Bytes.from(defaultArray).encodeHex());
        assertEquals("A0E1", Bytes.from(defaultArray).encodeHex(true));
        assertEquals(Bytes.from(defaultArray).encodeHex(), Bytes.from(defaultArray).encodeHex(false));
        assertEquals("4a94fdff1eafed", Bytes.from(encodingExample).encodeHex());
    }

    @Test
    public void parseBase64() {
        assertArrayEquals(encodingExample, Bytes.parseBase64("SpT9/x6v7Q==").array());
        assertArrayEquals(encodingExample, Bytes.parseBase64("SpT9/x6v7Q").array());
        assertArrayEquals(encodingExample, Bytes.parseBase64("SpT9_x6v7Q==").array());
        assertArrayEquals(encodingExample, Bytes.parseBase64("SpT9_x6v7Q").array());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseBase64Invalid() {
        Bytes.parseBase64("☕");
    }

    @Test
    public void encodeBase64() {
        assertEquals("", Bytes.from(new byte[0]).encodeBase64());
        assertEquals("AA==", Bytes.from(new byte[1]).encodeBase64());
        assertEquals("SpT9/x6v7Q==", Bytes.from(encodingExample).encodeBase64());
    }

    @Test
    public void encodeBase64Url() {
        assertEquals("", Bytes.from(new byte[0]).encodeBase64Url());
        assertEquals("AA==", Bytes.from(new byte[1]).encodeBase64Url());
        assertEquals("SpT9_x6v7Q==", Bytes.from(encodingExample).encodeBase64Url());
    }

    @Test
    public void encodeBase64WithConfig() {
        assertEquals("", Bytes.from(new byte[0]).encodeBase64(true, true));
        assertEquals("AA==", Bytes.from(new byte[1]).encodeBase64(true, true));
        assertEquals("SpT9_x6v7Q==", Bytes.from(encodingExample).encodeBase64(true, true));

        assertEquals("", Bytes.from(new byte[0]).encodeBase64(true, false));
        assertEquals("AA", Bytes.from(new byte[1]).encodeBase64(true, false));
        assertEquals("SpT9_x6v7Q", Bytes.from(encodingExample).encodeBase64(true, false));

        assertEquals("", Bytes.from(new byte[0]).encodeBase64(false, true));
        assertEquals("AA==", Bytes.from(new byte[1]).encodeBase64(false, true));
        assertEquals("SpT9/x6v7Q==", Bytes.from(encodingExample).encodeBase64(false, true));

        assertEquals("", Bytes.from(new byte[0]).encodeBase64(false, false));
        assertEquals("AA", Bytes.from(new byte[1]).encodeBase64(false, false));
        assertEquals("SpT9/x6v7Q", Bytes.from(encodingExample).encodeBase64(false, false));
    }

    @Test
    public void parseBase32() {
        assertArrayEquals(encodingExample, Bytes.parseBase32("JKKP37Y6V7WQ====").array());
    }

    @Test
    public void encodeBase32() {
        assertEquals("JKKP37Y6V7WQ====", Bytes.from(encodingExample).encodeBase32());
    }

    @Test
    public void encodeBinary() {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("1010000011100001", Bytes.from(defaultArray).encodeBinary());
        assertEquals("1001010100101001111110111111111000111101010111111101101", Bytes.from(encodingExample).encodeBinary());
    }

    @Test
    public void parseOctal() {
        assertArrayEquals(encodingExample, Bytes.parseOctal("1124517677707527755").array());
    }

    @Test
    public void encodeOctal() {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("120341", Bytes.from(defaultArray).encodeOctal());
        assertEquals("1124517677707527755", Bytes.from(encodingExample).encodeOctal());
    }

    @Test
    public void parseDec() {
        assertArrayEquals(encodingExample, Bytes.parseDec("20992966904426477").array());
    }

    @Test
    public void encodeDec() {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("41185", Bytes.from(defaultArray).encodeDec());
        assertEquals("20992966904426477", Bytes.from(encodingExample).encodeDec());
    }

    @Test
    public void parseBase36() {
        assertArrayEquals(encodingExample, Bytes.parseBase36("5qpdvuwjvu5").array());
    }

    @Test
    public void encodeBase36() {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1, (byte) 0x13};
        assertEquals("69zbn", Bytes.from(defaultArray).encodeBase36());
        assertEquals("5qpdvuwjvu5", Bytes.from(encodingExample).encodeBase36());
    }

    @Test
    public void parseRadix() {
        assertArrayEquals(encodingExample, Bytes.parseRadix("1001010100101001111110111111111000111101010111111101101", 2).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("10202221221221000222101012210121012", 3).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("1022211033313333013222333231", 4).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("134003042232210013121402", 5).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("542412151505231515005", 6).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("1124517677707527755", 8).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("20992966904426477", 10).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("4a94fdff1eafed", 16).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("5iibpp5dgpgp", 26).array());
        assertArrayEquals(encodingExample, Bytes.parseRadix("5qpdvuwjvu5", 36).array());
    }

    @Test
    public void encodeRadix() {
        assertEquals("1001010100101001111110111111111000111101010111111101101", Bytes.from(encodingExample).encodeRadix(2));
        assertEquals("10202221221221000222101012210121012", Bytes.from(encodingExample).encodeRadix(3));
        assertEquals("1022211033313333013222333231", Bytes.from(encodingExample).encodeRadix(4));
        assertEquals("134003042232210013121402", Bytes.from(encodingExample).encodeRadix(5));
        assertEquals("542412151505231515005", Bytes.from(encodingExample).encodeRadix(6));
        assertEquals("1124517677707527755", Bytes.from(encodingExample).encodeRadix(8));
        assertEquals("20992966904426477", Bytes.from(encodingExample).encodeRadix(10));
        assertEquals("4a94fdff1eafed", Bytes.from(encodingExample).encodeRadix(16));
        assertEquals("5iibpp5dgpgp", Bytes.from(encodingExample).encodeRadix(26));
        assertEquals("5qpdvuwjvu5", Bytes.from(encodingExample).encodeRadix(36));
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeRadixIllegalTooHigh() {
        Bytes.from(encodingExample).encodeRadix(37);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeRadixIllegalTooLow() {
        Bytes.from(encodingExample).encodeRadix(1);
    }
}
