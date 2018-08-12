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

public class BinaryToTextEncodingTest {
    @Test
    public void encodeHex() {
        assertEquals("010203", new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
        assertEquals("030201", new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeHexShouldFail() {
        new BinaryToTextEncoding.Hex(false).decode("AAI=");
    }

    @Test
    public void encodeBaseRadix() {
        assertEquals("100211", new BinaryToTextEncoding.BaseRadixNumber(16).encode(new byte[]{16, 2, 17}, ByteOrder.BIG_ENDIAN));
        assertEquals("110210", new BinaryToTextEncoding.BaseRadixNumber(16).encode(new byte[]{16, 2, 17}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.BaseRadixNumber(2).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.BaseRadixNumber(2).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }

    @Test
    public void encodeDecodeRadix() {
        int leadingZeroHits = 0;
        int encodings = 0;
        for (int i = 0; i < 64; i++) {
            Bytes rnd = Bytes.random(i % 256);
            System.out.println("\n\nNEW TEST: " + i + " bytes\n");
            for (int j = 2; j <= 36; j++) {
                encodings++;
                BinaryToTextEncoding.EncoderDecoder encoding = new BinaryToTextEncoding.BaseRadixNumber(j);
                String encodedBigEndian = encoding.encode(rnd.array(), ByteOrder.BIG_ENDIAN);
                byte[] decoded = encoding.decode(encodedBigEndian);
                System.out.println("radix" + j + ":\t" + encodedBigEndian);
                System.out.println("orig   :\t" + rnd.encodeHex());
                System.out.println("enc    :\t" + Bytes.wrap(decoded).encodeHex());


                if (rnd.length() <= 0 || rnd.byteAt(0) != 0) {
                    assertArrayEquals(rnd.array(), decoded);
                } else { //since this is a number, we allow different lengths due to leading zero
                    leadingZeroHits++;
                    assertArrayEquals(rnd.resize(rnd.length() - 1).array(), decoded);
                }
            }
        }
        System.out.println(leadingZeroHits + " leading zero mismatches of " + encodings + " encodings");
    }

    @Test
    public void encodeDecodeRadixZeros() {
        Bytes bytes = Bytes.wrap(new byte[]{0, 0, 0, 0});
        BinaryToTextEncoding.EncoderDecoder encoding = new BinaryToTextEncoding.BaseRadixNumber(36);
        String encodedBigEndian = encoding.encode(bytes.array(), ByteOrder.BIG_ENDIAN);
        byte[] decoded = encoding.decode(encodedBigEndian);

        System.out.println("radix36:\t" + encodedBigEndian);
        System.out.println("orig   :\t" + bytes.encodeHex());
        System.out.println("enc    :\t" + Bytes.wrap(decoded).encodeHex());
        assertArrayEquals(new byte[]{}, decoded);
    }

    @Test
    public void encodeDecodeBase64() {
        BinaryToTextEncoding.EncoderDecoder encoderPad = new BinaryToTextEncoding.Base64Encoding(false, true);
        BinaryToTextEncoding.EncoderDecoder encoderUrlPad = new BinaryToTextEncoding.Base64Encoding(true, true);
        BinaryToTextEncoding.EncoderDecoder encoderNoPad = new BinaryToTextEncoding.Base64Encoding(false, false);

        for (int i = 0; i < 32; i += 4) {
            Bytes rnd = Bytes.random(i);
            String encodedBigEndian = encoderPad.encode(rnd.array(), ByteOrder.BIG_ENDIAN);
            byte[] decoded = encoderPad.decode(encodedBigEndian);
            assertEquals(rnd, Bytes.wrap(decoded));

            String encodedBigEndianUrlPad = encoderUrlPad.encode(rnd.array(), ByteOrder.BIG_ENDIAN);
            byte[] decodedUrlPad = encoderPad.decode(encodedBigEndianUrlPad);
            assertEquals(rnd, Bytes.wrap(decodedUrlPad));

            String encodedBigEndianNoPad = encoderNoPad.encode(rnd.array(), ByteOrder.BIG_ENDIAN);
            byte[] decodedNoPad = encoderPad.decode(encodedBigEndianNoPad);
            assertEquals(rnd, Bytes.wrap(decodedNoPad));
        }
    }

    @Test
    public void encodeDecodeHex() {
        for (int i = 4; i < 32; i += 4) {
            Bytes rnd = Bytes.random(i);
            BinaryToTextEncoding.EncoderDecoder encoding = new BinaryToTextEncoding.Hex();
            String encodedBigEndian = encoding.encode(rnd.array(), ByteOrder.BIG_ENDIAN);
            byte[] decoded = encoding.decode(encodedBigEndian);
            assertEquals(rnd, Bytes.wrap(decoded));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidRadix16() {
        new BinaryToTextEncoding.BaseRadixNumber(16).decode("AAI=");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidRadix36() {
        new BinaryToTextEncoding.BaseRadixNumber(36).decode("AAI=");
    }

    @Test
    public void encodeBase64() {
        assertEquals("EAIR", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{16, 2, 17}, ByteOrder.BIG_ENDIAN));
        assertEquals("EQIQ", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{17, 2, 16}, ByteOrder.BIG_ENDIAN));
        assertEquals("EQIQ", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{16, 2, 17}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidBase64() {
        new BinaryToTextEncoding.Base64Encoding().decode("(&´´");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeHalfInvalidBase64() {
        new BinaryToTextEncoding.Base64Encoding().decode("EAI`");
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeRadixIllegalTooHigh2() {
        new BinaryToTextEncoding.BaseRadixNumber(38);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeRadixIllegalTooHigh() {
        new BinaryToTextEncoding.BaseRadixNumber(37);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeRadixIllegalTooLow() {
        new BinaryToTextEncoding.BaseRadixNumber(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeRadixIllegalTooLow2() {
        new BinaryToTextEncoding.BaseRadixNumber(0);
    }
}
