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
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static at.favre.lib.bytes.BytesTransformers.*;
import static org.junit.Assert.*;

public class BytesTransformTest extends ABytesTest {

    @Test
    public void append() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).append(new byte[0]).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[0]).append(new byte[1]).array());
        assertArrayEquals(Util.concat(example_bytes_seven, example_bytes_one), Bytes.from(example_bytes_seven).append(example_bytes_one).array());
        assertArrayEquals(Util.concat(example_bytes_seven, example_bytes_two), Bytes.from(example_bytes_seven).append(example_bytes_two).array());

        assertArrayEquals(Util.concat(example_bytes_eight, example_bytes_sixteen), Bytes.from(example_bytes_eight).append(Bytes.from(example_bytes_sixteen)).array());
    }

    @Test
    public void appendMultipleByteArrays() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).append(new byte[0], new byte[0]).array());
        assertArrayEquals(new byte[]{0x0, 0x01, 0x02, 0x03}, Bytes.from(new byte[]{0x0}).append(new byte[]{0x1}, new byte[]{0x2}, new byte[]{0x3}).array());
        assertArrayEquals(Util.concat(example_bytes_seven, example_bytes_one, example_bytes_sixteen), Bytes.from(example_bytes_seven).append(example_bytes_one, example_bytes_sixteen).array());
        assertArrayEquals(Util.concat(example_bytes_sixteen, example_bytes_sixteen, example_bytes_sixteen), Bytes.from(example_bytes_sixteen).append(example_bytes_sixteen, example_bytes_sixteen).array());
        assertArrayEquals(Util.concat(example_bytes_two, example_bytes_seven, example_bytes_twentyfour, example_bytes_eight), Bytes.from(example_bytes_two).append(example_bytes_seven, example_bytes_twentyfour, example_bytes_eight).array());
        assertArrayEquals(Util.concat(example_bytes_two, example_bytes_seven, example_bytes_twentyfour, example_bytes_one, example_bytes_sixteen), Bytes.from(example_bytes_two).append(example_bytes_seven, example_bytes_twentyfour).append(example_bytes_one, example_bytes_sixteen).array());
    }

    @Test
    public void appendNullSafe() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).appendNullSafe(new byte[0]).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[0]).appendNullSafe(new byte[1]).array());
        assertArrayEquals(Util.concat(example_bytes_seven, example_bytes_one), Bytes.from(example_bytes_seven).appendNullSafe(example_bytes_one).array());
        assertArrayEquals(Util.concat(example_bytes_seven, example_bytes_two), Bytes.from(example_bytes_seven).appendNullSafe(example_bytes_two).array());
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).appendNullSafe(null).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).appendNullSafe(null).array());
        assertArrayEquals(example_bytes_seven, Bytes.from(example_bytes_seven).appendNullSafe(null).array());
    }

    @Test
    public void appendPrimitives() {
        assertArrayEquals(Util.concat(example_bytes_eight, new byte[]{1}), Bytes.from(example_bytes_eight).append((byte) 1).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(2).putChar((char) 1423).array()), Bytes.from(example_bytes_eight).append((char) 1423).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(2).putShort((short) 4129).array()), Bytes.from(example_bytes_eight).append((short) 4129).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(4).putInt(362173671).array()), Bytes.from(example_bytes_eight).append(362173671).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(8).putLong(0x6762173671L).array()), Bytes.from(example_bytes_eight).append(0x6762173671L).array());
    }

    @Test
    public void appendString() {
        assertArrayEquals(new byte[]{0, 0, 48}, Bytes.from(new byte[2]).append("0").array());
        assertArrayEquals(new byte[]{48}, Bytes.from(new byte[0]).append("0").array());
        assertArrayEquals(new byte[]{71, 117, 116}, Bytes.from(new byte[0]).append("Gut").array());
        assertArrayEquals(new byte[]{71, -30, -99, -92}, Bytes.from(new byte[0]).append("G❤").array());

        assertArrayEquals(new byte[]{48}, Bytes.from(new byte[0]).append("0", StandardCharsets.US_ASCII).array());
        assertArrayEquals(new byte[]{71, 117, 116}, Bytes.from(new byte[0]).append("Gut", StandardCharsets.US_ASCII).array());
        assertArrayEquals(new byte[]{71, 117, 116}, Bytes.from(new byte[0]).append("Gut", StandardCharsets.UTF_8).array());
        assertArrayEquals(new byte[]{71, 117, 116}, Bytes.from(new byte[0]).append("Gut", StandardCharsets.ISO_8859_1).array());
        assertArrayEquals(new byte[]{71, -4, 116}, Bytes.from(new byte[0]).append("Güt", StandardCharsets.ISO_8859_1).array());
        assertArrayEquals(new byte[]{71, -61, -68, 116}, Bytes.from(new byte[0]).append("Güt", StandardCharsets.UTF_8).array());
    }

    @Test
    public void appendMulti() {
        Bytes b = Bytes.random(2);
        for (int i = 0; i < 100; i++) {
            byte[] oldByteArray = b.copy().array();
            Bytes newByte = Bytes.random(i % 16 + 1);
            b = b.append(newByte);
            assertArrayEquals(Util.concat(oldByteArray, newByte.copy().array()), b.array());
        }
    }

    @Test
    public void resizeGrowLsb() {
        assertArrayEquals(new byte[8], Bytes.from(new byte[0]).resize(8).array());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).resize(1).array());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).resize(1).array());
        assertArrayEquals(Util.concat(new byte[7], example_bytes_one), Bytes.from(example_bytes_one).resize(8).array());
        assertArrayEquals(Util.concat(new byte[1], example_bytes_seven), Bytes.from(example_bytes_seven).resize(8).array());
        assertArrayEquals(Util.concat(new byte[1], example_bytes_sixteen), Bytes.from(example_bytes_sixteen).resize(17).array());
    }

    @Test
    public void resizeGrowMsb() {
        assertArrayEquals(new byte[8], Bytes.from(new byte[0]).resize(8, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).resize(1, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).resize(1, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(Util.concat(example_bytes_one, new byte[7]), Bytes.from(example_bytes_one).resize(8, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(Util.concat(example_bytes_seven, new byte[1]), Bytes.from(example_bytes_seven).resize(8, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(Util.concat(example_bytes_sixteen, new byte[1]), Bytes.from(example_bytes_sixteen).resize(17, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
    }

    @Test
    public void resizeShrinkLsb() {
        assertArrayEquals(new byte[0], Bytes.from(example_bytes_one).resize(0).array());
        assertArrayEquals(new byte[]{example_bytes_two[1]}, Bytes.from(example_bytes_two).resize(1).array());
        assertArrayEquals(new byte[]{example_bytes_four[3]}, Bytes.from(example_bytes_four).resize(1).array());
        assertArrayEquals(new byte[]{example_bytes_four[2], example_bytes_four[3]}, Bytes.from(example_bytes_four).resize(2).array());
        assertArrayEquals(new byte[]{example_bytes_sixteen[14], example_bytes_sixteen[15]}, Bytes.from(example_bytes_sixteen).resize(2).array());
        assertArrayEquals(new byte[]{example_bytes_sixteen[13], example_bytes_sixteen[14], example_bytes_sixteen[15]}, Bytes.from(example_bytes_sixteen).resize(3).array());
        assertArrayEquals(example_bytes_four, Bytes.from(example_bytes_four).resize(8).resize(4).array());

        try {
            Bytes.from(new byte[0]).resize(-1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void resizeShrinkMsb() {
        assertArrayEquals(new byte[0], Bytes.from(example_bytes_one).resize(0, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(new byte[]{example_bytes_two[0]}, Bytes.from(example_bytes_two).resize(1, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(new byte[]{example_bytes_four[0]}, Bytes.from(example_bytes_four).resize(1, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());
        assertArrayEquals(new byte[]{example_bytes_four[0], example_bytes_four[1]}, Bytes.from(example_bytes_four).resize(2, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX).array());

        try {
            Bytes.from(new byte[0]).resize(-1, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_ZERO_INDEX);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void resizeSameInstance() {
        Bytes b = Bytes.from(example_bytes_sixteen);
        Bytes b2 = b.resize(16);
        assertSame(b.array(), b2.array());
    }

    @Test
    public void xor() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).xor(new byte[0]).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).xor(new byte[1]).array());

        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).xor(new byte[1]).array());
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).xor(new byte[2]).array());
        assertArrayEquals(example_bytes_sixteen, Bytes.from(example_bytes_sixteen).xor(new byte[16]).array());

        assertArrayEquals(new byte[1], Bytes.from(example_bytes_one).xor(example_bytes_one).array());
        assertArrayEquals(new byte[2], Bytes.from(example_bytes_two).xor(example_bytes_two).array());
        assertArrayEquals(new byte[16], Bytes.from(example_bytes_sixteen).xor(example_bytes_sixteen).array());

        assertArrayEquals(new byte[]{-69, -51}, Bytes.from(new byte[]{(byte) 0xAE, (byte) 0x1E}).xor(new byte[]{(byte) 0x15, (byte) 0xD3}).array());

        assertArrayEquals(new byte[1], Bytes.from(example_bytes_one).xor(Bytes.from(example_bytes_one)).array());
        assertArrayEquals(new byte[16], Bytes.from(example_bytes_sixteen).xor(Bytes.from(example_bytes_sixteen)).array());

        try {
            Bytes.from(example_bytes_seven).xor(example_bytes_eight);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void or() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).or(new byte[0]).array());
        assertArrayEquals(new byte[]{1}, Bytes.from(new byte[]{1}).or(new byte[]{0}).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).or(new byte[1]).array());

        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).or(Bytes.wrap(new byte[0])).array());
        assertArrayEquals(new byte[]{1}, Bytes.from(new byte[]{1}).or(Bytes.wrap(new byte[]{0})).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).or(Bytes.wrap(new byte[1])).array());

        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).or(new byte[1]).array());
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).or(new byte[2]).array());
        assertArrayEquals(example_bytes_sixteen, Bytes.from(example_bytes_sixteen).or(new byte[16]).array());

        assertArrayEquals(new byte[]{0x67}, Bytes.from(example_bytes_one).or(example_bytes_one).array());
        assertArrayEquals(new byte[]{0x67}, Bytes.from(example_bytes_one).or(Bytes.from(example_bytes_one)).array());
        assertArrayEquals(new byte[]{-65, -33}, Bytes.from(new byte[]{(byte) 0xAE, (byte) 0x1E}).or(new byte[]{(byte) 0x15, (byte) 0xD3}).array());

        try {
            Bytes.from(example_bytes_seven).or(example_bytes_eight);
            fail();
        } catch (Exception ignore) {
        }
    }

    @Test
    public void and() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).and(new byte[0]).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).and(new byte[1]).array());

        assertArrayEquals(new byte[1], Bytes.from(example_bytes_one).and(new byte[1]).array());
        assertArrayEquals(new byte[2], Bytes.from(example_bytes_two).and(new byte[2]).array());
        assertArrayEquals(new byte[16], Bytes.from(example_bytes_sixteen).and(new byte[16]).array());

        assertArrayEquals(new byte[]{0x67}, Bytes.from(example_bytes_one).and(example_bytes_one).array());
        assertArrayEquals(new byte[]{0x67}, Bytes.from(example_bytes_one).and(Bytes.from(example_bytes_one)).array());
        assertArrayEquals(new byte[]{4, 18}, Bytes.from(new byte[]{(byte) 0xAE, (byte) 0x1E}).and(new byte[]{(byte) 0x15, (byte) 0xD3}).array());

        try {
            Bytes.from(example_bytes_seven).and(example_bytes_eight);
            fail();
        } catch (Exception ignore) {
        }
    }

    @Test
    public void negate() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).not().array());
        assertArrayEquals(new byte[]{(byte) 0xFF}, Bytes.from(new byte[1]).not().array());

        assertArrayEquals(new byte[]{-104}, Bytes.from(example_bytes_one).not().array());
        assertArrayEquals(new byte[]{-27, -112}, Bytes.from(example_bytes_two).not().array());
        assertArrayEquals(new byte[]{81, -31}, Bytes.from(new byte[]{(byte) 0xAE, (byte) 0x1E}).not().array());

        assertArrayNotEquals(new byte[0], Bytes.from(example_bytes_one).not().array());
    }

    @Test
    public void reverse() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).reverse().array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).reverse().array());

        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).reverse().array());
        assertArrayEquals(new byte[]{example_bytes_two[1], example_bytes_two[0]}, Bytes.from(example_bytes_two).reverse().array());
        assertArrayEquals(new byte[]{example_bytes_four[3], example_bytes_four[2], example_bytes_four[1], example_bytes_four[0]}, Bytes.from(example_bytes_four).reverse().array());

        assertArrayNotEquals(new byte[0], Bytes.from(example_bytes_one).reverse().array());
    }

    @Test
    public void copy() {
        assertArrayEquals(new byte[0], Bytes.wrap(new byte[0]).copy().array());

        assertArrayEquals(example_bytes_one, Bytes.wrap(example_bytes_one).copy().array());
        assertNotSame(example_bytes_one, Bytes.wrap(example_bytes_one).copy().array());

        assertArrayEquals(example_bytes_two, Bytes.wrap(example_bytes_two).copy().array());
        assertNotSame(example_bytes_two, Bytes.wrap(example_bytes_two).copy().array());

        assertArrayEquals(example_bytes_seven, Bytes.wrap(example_bytes_seven).copy().array());
        assertNotSame(example_bytes_seven, Bytes.wrap(example_bytes_seven).copy().array());

        assertArrayEquals(new byte[]{example_bytes_seven[0]}, Bytes.wrap(example_bytes_seven).copy(0, 1).array());
        assertArrayEquals(new byte[]{example_bytes_seven[3], example_bytes_seven[4], example_bytes_seven[5], example_bytes_seven[6]}, Bytes.wrap(example_bytes_seven).copy(3, 4).array());
    }

    @Test
    public void shuffleTest() {
        assertArrayNotEquals(example_bytes_twentyfour, Bytes.from(example_bytes_twentyfour).transform(shuffle()).array());
        assertArrayNotEquals(example_bytes_twentyfour, Bytes.from(example_bytes_twentyfour).transform(shuffle(new SecureRandom())).array());
        assertArrayNotEquals(new byte[24], Bytes.from(example_bytes_twentyfour).transform(shuffle(new SecureRandom())).array());
    }

    @Test
    public void sortSignedTest() {
        byte[] sorted = new byte[]{-2, -1, 0, 1, 2, 3, 4, 5, 6};
        assertArrayEquals(sorted, Bytes.from(sorted).transform(shuffle()).transform(sort()).array());
        assertArrayEquals(sorted, Bytes.from(new byte[]{6, 0, 3, -2, -1, 4, 1, 5, 2}).transform(sort()).array());
        assertArrayEquals(Bytes.from(sorted).reverse().array(), Bytes.from(new byte[]{6, -2, -1, 0, 3, 4, 1, 5, 2}).transform(sort(new Comparator<Byte>() {
            @Override
            public int compare(Byte o1, Byte o2) {
                return o2.compareTo(o1);
            }
        })).array());

        byte[] checkSignedSorted = new byte[]{(byte) 0x80, (byte) 0xFE, (byte) 0xFF, 0x00, 0x01};
        assertArrayEquals(checkSignedSorted, Bytes.from(checkSignedSorted).transform(shuffle()).transform(sort()).array());
    }

    @Test
    public void sortUnsignedTest() {
        byte[] sorted = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, (byte) 0x80, (byte) 0xAE, (byte) 0xFF};
        assertArrayEquals(sorted, Bytes.from(sorted).transform(shuffle()).transform(sortUnsigned()).array());
        assertArrayEquals(sorted, Bytes.from(new byte[]{(byte) 0x80, (byte) 0xAE, (byte) 0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06}).transform(sortUnsigned()).array());
    }

    @Test
    public void leftShift() {
        assertArrayEquals(new byte[]{2}, Bytes.from((byte) 1).leftShift(1).array());
        assertArrayEquals(new byte[]{4}, Bytes.from((byte) 1).leftShift(2).array());
        assertArrayEquals(new byte[]{8}, Bytes.from((byte) 1).leftShift(3).array());
        assertArrayEquals(new byte[]{example_bytes_two[1], 0}, Bytes.from(example_bytes_two).leftShift(8).array());
        assertArrayEquals(new byte[]{0, 0}, Bytes.from(example_bytes_two).leftShift(16).array());
    }

    @Test
    public void rightShift() {
        assertArrayEquals(new byte[]{4}, Bytes.from((byte) 8).rightShift(1).array());
        assertArrayEquals(new byte[]{2}, Bytes.from((byte) 8).rightShift(2).array());
        assertArrayEquals(new byte[]{1}, Bytes.from((byte) 8).rightShift(3).array());
        assertArrayEquals(new byte[]{0}, Bytes.from((byte) 8).rightShift(4).array());
        assertArrayEquals(new byte[]{0, example_bytes_two[0]}, Bytes.from(example_bytes_two).rightShift(8).array());
        assertArrayEquals(new byte[2], Bytes.from(example_bytes_two).rightShift(16).array());
    }

    @Test
    public void bitSwitch() {
        assertEquals(1, Bytes.from(0).switchBit(0, true).toInt());

        for (int i = 0; i < 63; i++) {
            for (long j = 1; j < 33; j++) {
                assertEquals("bit position " + i + " is wrong",
                        BigInteger.valueOf(j).setBit(i).longValue(),
                        Bytes.from(j).switchBit(i, true).toLong());
                assertEquals("bit position " + i + " is wrong",
                        BigInteger.valueOf(j).flipBit(i).longValue(),
                        Bytes.from(j).switchBit(i).toLong());
                assertEquals("bit position " + i + " is wrong",
                        BigInteger.valueOf(j).clearBit(i).longValue(),
                        Bytes.from(j).switchBit(i, false).toLong());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void bitSwitchOutOfBounds() {
        Bytes.from(4).switchBit(32, true);
    }

    @Test
    public void hashSha1() {
        assertEquals(Bytes.parseHex("da39a3ee5e6b4b0d3255bfef95601890afd80709"), Bytes.from("").hashSha1());
        assertEquals(Bytes.parseHex("2628013771c4ffda4336231805f9d6c42e40ef86"), Bytes.from("ö9h%6Ghh1\"").hashSha1());
        assertEquals(Bytes.parseHex("15d418a940d699df0cde6304829b2cce5ed4a9ad"), Bytes.from("897SHALkjdn ,n--   kasdjöa").hashSha1());
    }

    @Test
    public void hashMd5() {
        assertEquals(Bytes.parseHex("d41d8cd98f00b204e9800998ecf8427e"), Bytes.from("").hashMd5());
        assertEquals(Bytes.parseHex("ff38205f1cb22f588d8bc9ae21f22092"), Bytes.from("ö9h%6Ghh1\"").hashMd5());
        assertEquals(Bytes.parseHex("9DFF192C3CE8554DBB1ADCC7721B4B78"), Bytes.from("897SHALkjdn ,n--   kasdjöa").hashMd5());
    }

    @Test
    public void hash256() {
        assertEquals(Bytes.parseHex("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"), Bytes.from("").hashSha256());
        assertEquals(Bytes.parseHex("e362eea626386c93a54c9b82e6b896c0350fbff0ee12f284660253aac0908cfb"), Bytes.from("ö9h%6Ghh1\"").hashSha256());
        assertEquals(Bytes.parseHex("48D6BE81CB2EF8488BA2E3BF4050EE21BF9D33D85DB0E556E4AE5992243B8F35"), Bytes.from("897SHALkjdn ,n--   kasdjöa").hashSha256());
    }

    @Test
    public void hashCustom() {
        assertEquals(Bytes.parseHex("cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"), Bytes.from("").hash("SHA-512"));
        assertEquals(Bytes.parseHex("106747C3DDC117091BEF8D21AEBAA8D314656D3AE1135AB36F4C0B07A264127CF625FE616751BEC66B43032B904E2D3B6C21BF14E078F6BB775A72503F48111D"), Bytes.from("ö9h%6Ghh1\"").hash("SHA-512"));
        assertEquals(Bytes.parseHex("d41d8cd98f00b204e9800998ecf8427e"), Bytes.from("").hash("MD5"));
        assertEquals(Bytes.parseHex("ff38205f1cb22f588d8bc9ae21f22092"), Bytes.from("ö9h%6Ghh1\"").hash("MD5"));
        assertEquals(Bytes.parseHex("38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b"), Bytes.from("").hash("SHA-384"));
        assertEquals(Bytes.parseHex("ec89d0d6b067f7f2e240ea7587933d92347fce4bdab68784bd2373dc1cccaa0238c0556b045acb1632080fac788d429d"), Bytes.from("ö9h%6Ghh1\"").hash("SHA-384"));
    }

    @Test
    public void checksumTest() {
        Checksum crc32Checksum = new CRC32();
        crc32Checksum.update(example2_bytes_seven, 0, example2_bytes_seven.length);
        assertEquals(crc32Checksum.getValue(), Bytes.from(example2_bytes_seven).transform(checksumCrc32()).resize(8).toLong());
        assertEquals(Bytes.from(example2_bytes_seven, Bytes.from(crc32Checksum.getValue()).resize(4).array()), Bytes.from(example2_bytes_seven).transform(checksumAppendCrc32()));

        Checksum adlerChecksum = new Adler32();
        adlerChecksum.update(example2_bytes_seven, 0, example2_bytes_seven.length);
        assertEquals(Bytes.from(adlerChecksum.getValue()).resize(4),
                Bytes.from(example2_bytes_seven).transform(checksum(new Adler32(), ChecksumTransformer.Mode.TRANSFORM, 4)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksumTestIllegalByteLengthTooShort() {
        Bytes.from(example2_bytes_seven).transform(checksum(new CRC32(), ChecksumTransformer.Mode.TRANSFORM, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksumTestIllegalByteLengthTooLong() {
        Bytes.from(example2_bytes_seven).transform(checksum(new CRC32(), ChecksumTransformer.Mode.TRANSFORM, 9));
    }

    @Test
    public void testCompress() {
        for (int i = 1; i < 10; i++) {
            testCompressInternal(256 * i);
        }
    }

    private void testCompressInternal(int length) {
        Bytes emptyArray = Bytes.allocate(length);
        byte[] compressed = Bytes.from(emptyArray).transform(compressGzip()).array();
        byte[] uncompressed = Bytes.wrap(compressed).transform(decompressGzip()).array();
        assertArrayEquals(emptyArray.array(), uncompressed);
        assertArrayNotEquals(compressed, uncompressed);
        assertTrue("compressed is not smaller " + compressed.length + " vs " + uncompressed.length, compressed.length < uncompressed.length);
    }

    @Test
    public void transform() {
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).transform(new BytesTransformer() {
            @Override
            public byte[] transform(byte[] currentArray, boolean inPlace) {
                return Bytes.from(currentArray).array();
            }

            @Override
            public boolean supportInPlaceTransformation() {
                return false;
            }
        }).array());
        assertArrayEquals(new byte[2], Bytes.from(example_bytes_two).transform(new BytesTransformer() {
            @Override
            public byte[] transform(byte[] currentArray, boolean inPlace) {
                return Bytes.allocate(currentArray.length).array();
            }

            @Override
            public boolean supportInPlaceTransformation() {
                return false;
            }
        }).array());
    }

    @Test
    public void transformerInPlaceTest() {
        assertTrue(new BytesTransformer.BitSwitchTransformer(0, true).supportInPlaceTransformation());
        assertTrue(new BytesTransformer.BitWiseOperatorTransformer(new byte[]{}, BytesTransformer.BitWiseOperatorTransformer.Mode.XOR).supportInPlaceTransformation());
        assertTrue(new BytesTransformer.NegateTransformer().supportInPlaceTransformation());
        assertTrue(new BytesTransformer.ShiftTransformer(0, BytesTransformer.ShiftTransformer.Type.LEFT_SHIFT).supportInPlaceTransformation());
        assertTrue(new BytesTransformer.ReverseTransformer().supportInPlaceTransformation());

        assertFalse(new BytesTransformer.MessageDigestTransformer("SHA1").supportInPlaceTransformation());
        assertFalse(new BytesTransformer.CopyTransformer(0, 0).supportInPlaceTransformation());
        assertFalse(new BytesTransformer.ResizeTransformer(0, BytesTransformer.ResizeTransformer.Mode.RESIZE_KEEP_FROM_MAX_LENGTH).supportInPlaceTransformation());
        assertFalse(new BytesTransformer.ConcatTransformer(new byte[]{}).supportInPlaceTransformation());

        assertFalse(new BytesTransformers.GzipCompressor(false).supportInPlaceTransformation());
        assertFalse(new BytesTransformers.ChecksumTransformer(new CRC32(), ChecksumTransformer.Mode.TRANSFORM, 4).supportInPlaceTransformation());
        assertTrue(new BytesTransformers.SortTransformer().supportInPlaceTransformation());
        assertFalse(new BytesTransformers.SortTransformer(new Comparator<Byte>() {
            @Override
            public int compare(Byte o1, Byte o2) {
                return 0;
            }
        }).supportInPlaceTransformation());
        assertTrue(new BytesTransformers.ShuffleTransformer(new SecureRandom()).supportInPlaceTransformation());
    }

    @Test
    public void transformHmac() {
        System.out.println(Bytes.parseHex("d8b6239569b184eb7991").transform(new HmacTransformer(Bytes.parseHex("671536819982").array(), "HmacSHA256")).encodeHex());

        assertEquals(Bytes.parseHex("d8f0eda7a00192091ad8fefa501753ae"), Bytes.allocate(16).transform(new HmacTransformer(new byte[16], "HmacMd5")));
        assertEquals(Bytes.parseHex("c69c13e005ae8ec628ec1869f334ca056bb38958"), Bytes.allocate(16).transform(new HmacTransformer(new byte[20], "HmacSHA1")));
        assertEquals(Bytes.parseHex("c69c13e005ae8ec628ec1869f334ca056bb38958"), Bytes.allocate(16).transform(BytesTransformers.hmacSha1(new byte[20])));
        assertEquals(Bytes.parseHex("853c7403937d8b6239569b184eb7993fc5f751aefcea28f2c863858e2d29c50b"), Bytes.allocate(16).transform(new HmacTransformer(new byte[32], "HmacSHA256")));
        assertEquals(Bytes.parseHex("9aff87db4fd8df58c9081d8386ccc71c9a0f5fe9491235b7bb17e1be20bbe82b"), Bytes.parseHex("d8b6239569b184eb7991").transform(new HmacTransformer(Bytes.parseHex("671536819982").array(), "HmacSHA256")));
        assertEquals(Bytes.parseHex("9aff87db4fd8df58c9081d8386ccc71c9a0f5fe9491235b7bb17e1be20bbe82b"), Bytes.parseHex("d8b6239569b184eb7991").transform(BytesTransformers.hmacSha256(Bytes.parseHex("671536819982").array())));
        assertEquals(Bytes.parseHex("9aff87db4fd8df58c9081d8386ccc71c9a0f5fe9491235b7bb17e1be20bbe82b"), Bytes.parseHex("d8b6239569b184eb7991").transform(BytesTransformers.hmac(Bytes.parseHex("671536819982").array(), "HmacSHA256")));

        //reference test vectors - see https://tools.ietf.org/html/rfc2104
        assertEquals(Bytes.parseHex("9294727a3638bb1c13f48ef8158bfc9d"), Bytes.from("Hi There").transform(new HmacTransformer(Bytes.parseHex("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b").array(), "HmacMd5")));
        assertEquals(Bytes.parseHex("56be34521d144c88dbb8c733f0e8b3f6"), Bytes.parseHex("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD").transform(new HmacTransformer(Bytes.parseHex("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").array(), "HmacMd5")));
    }
}
