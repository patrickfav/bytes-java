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
    public void testBase16Reference() {
        BinaryToTextEncoding.EncoderDecoder base16Encoding = new BinaryToTextEncoding.Hex(true);
        // see: https://tools.ietf.org/html/rfc4648
        assertEquals("", base16Encoding.encode(Bytes.of("").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("66", base16Encoding.encode(Bytes.of("f").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("666F", base16Encoding.encode(Bytes.of("fo").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("666F6F", base16Encoding.encode(Bytes.of("foo").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("666F6F62", base16Encoding.encode(Bytes.of("foob").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("666F6F6261", base16Encoding.encode(Bytes.of("fooba").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("666F6F626172", base16Encoding.encode(Bytes.of("foobar").array(), ByteOrder.BIG_ENDIAN));

        assertArrayEquals(Bytes.of("").array(), base16Encoding.decode(""));
        assertArrayEquals(Bytes.of("f").array(), base16Encoding.decode("66"));
        assertArrayEquals(Bytes.of("fo").array(), base16Encoding.decode("666F"));
        assertArrayEquals(Bytes.of("foo").array(), base16Encoding.decode("666F6F"));
        assertArrayEquals(Bytes.of("foob").array(), base16Encoding.decode("666F6F62"));
        assertArrayEquals(Bytes.of("fooba").array(), base16Encoding.decode("666F6F6261"));
        assertArrayEquals(Bytes.of("foobar").array(), base16Encoding.decode("666F6F626172"));
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
    public void encodeDecodeBase64Random() {
        BinaryToTextEncoding.EncoderDecoder encoderPad = new BinaryToTextEncoding.Base64Encoding(false, true);
        BinaryToTextEncoding.EncoderDecoder encoderUrlPad = new BinaryToTextEncoding.Base64Encoding(true, true);
        BinaryToTextEncoding.EncoderDecoder encoderNoPad = new BinaryToTextEncoding.Base64Encoding(false, false);

        for (int i = 0; i < 32; i += 4) {
            testRndEncodeDecode(encoderPad, i);
            testRndEncodeDecode(encoderUrlPad, i);
            testRndEncodeDecode(encoderNoPad, i);
        }
    }

    @Test
    public void testBase64Reference() {
        BinaryToTextEncoding.EncoderDecoder base64Encoding = new BinaryToTextEncoding.Base64Encoding();
        // see: https://tools.ietf.org/html/rfc4648
        assertEquals("", base64Encoding.encode(Bytes.of("").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("Zg==", base64Encoding.encode(Bytes.of("f").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("Zm8=", base64Encoding.encode(Bytes.of("fo").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("Zm9v", base64Encoding.encode(Bytes.of("foo").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("Zm9vYg==", base64Encoding.encode(Bytes.of("foob").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("Zm9vYmE=", base64Encoding.encode(Bytes.of("fooba").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("Zm9vYmFy", base64Encoding.encode(Bytes.of("foobar").array(), ByteOrder.BIG_ENDIAN));

        assertArrayEquals(Bytes.of("").array(), base64Encoding.decode(""));
        assertArrayEquals(Bytes.of("f").array(), base64Encoding.decode("Zg=="));
        assertArrayEquals(Bytes.of("fo").array(), base64Encoding.decode("Zm8="));
        assertArrayEquals(Bytes.of("foo").array(), base64Encoding.decode("Zm9v"));
        assertArrayEquals(Bytes.of("foob").array(), base64Encoding.decode("Zm9vYg=="));
        assertArrayEquals(Bytes.of("fooba").array(), base64Encoding.decode("Zm9vYmE="));
        assertArrayEquals(Bytes.of("foobar").array(), base64Encoding.decode("Zm9vYmFy"));
    }

    @Test
    public void encodeDecodeHex() {
        for (int i = 4; i < 32; i += 4) {
            testRndEncodeDecode(new BinaryToTextEncoding.Hex(), i);
            testRndEncodeDecode(new BinaryToTextEncoding.Hex(true), i);
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

    @Test
    public void testEncodeDecodeRndBase32() {
        BaseEncoding base32Encoding = new BaseEncoding(BaseEncoding.BASE32_RFC4848, BaseEncoding.BASE32_RFC4848_PADDING);
        for (int i = 0; i < 128; i++) {
            testRndEncodeDecode(base32Encoding, i);
        }
    }

    private byte[] testRndEncodeDecode(BinaryToTextEncoding.EncoderDecoder encoder, int dataLength) {
        Bytes rnd = Bytes.random(dataLength);
        String encoded = encoder.encode(rnd.array(), ByteOrder.BIG_ENDIAN);
        byte[] decoded = encoder.decode(encoded);
        assertEquals(rnd, Bytes.wrap(decoded));
        return decoded;
    }

    @Test
    public void testBase32Reference() {
        BinaryToTextEncoding.EncoderDecoder base32Encoding = new BaseEncoding(BaseEncoding.BASE32_RFC4848, BaseEncoding.BASE32_RFC4848_PADDING);
        // see: https://tools.ietf.org/html/rfc4648
        assertEquals("", base32Encoding.encode(Bytes.of("").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("MY======", base32Encoding.encode(Bytes.of("f").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("MZXQ====", base32Encoding.encode(Bytes.of("fo").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("MZXW6===", base32Encoding.encode(Bytes.of("foo").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("MZXW6YQ=", base32Encoding.encode(Bytes.of("foob").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("MZXW6YTB", base32Encoding.encode(Bytes.of("fooba").array(), ByteOrder.BIG_ENDIAN));
        assertEquals("MZXW6YTBOI======", base32Encoding.encode(Bytes.of("foobar").array(), ByteOrder.BIG_ENDIAN));

        assertArrayEquals(Bytes.of("").array(), base32Encoding.decode(""));
        assertArrayEquals(Bytes.of("f").array(), base32Encoding.decode("MY======"));
        assertArrayEquals(Bytes.of("fo").array(), base32Encoding.decode("MZXQ===="));
        assertArrayEquals(Bytes.of("foo").array(), base32Encoding.decode("MZXW6==="));
        assertArrayEquals(Bytes.of("foob").array(), base32Encoding.decode("MZXW6YQ="));
        assertArrayEquals(Bytes.of("fooba").array(), base32Encoding.decode("MZXW6YTB"));
        assertArrayEquals(Bytes.of("foobar").array(), base32Encoding.decode("MZXW6YTBOI======"));
    }

    @Test
    public void testBase64BigData() {
        for (int i = 0; i < 5; i++) {
            byte[] out = testRndEncodeDecode(new BinaryToTextEncoding.Base64Encoding(), 1024 * 1024);
            System.out.println(out.length);
        }
    }

    @Test
    public void testBase32BigData() {
        for (int i = 0; i < 5; i++) {
            byte[] out = testRndEncodeDecode(new BaseEncoding(BaseEncoding.BASE32_RFC4848, BaseEncoding.BASE32_RFC4848_PADDING), 1024 * 1024);
            System.out.println(out.length);
        }
    }
}
