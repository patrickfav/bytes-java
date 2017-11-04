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
import java.security.SecureRandom;
import java.util.Comparator;

import static org.junit.Assert.*;

public class BytesTransformTest extends ABytesTest {

    @Test
    public void append() throws Exception {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).append(new byte[0]).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[0]).append(new byte[1]).array());
        assertArrayEquals(Util.concat(example_bytes_seven, example_bytes_one), Bytes.from(example_bytes_seven).append(example_bytes_one).array());
        assertArrayEquals(Util.concat(example_bytes_seven, example_bytes_two), Bytes.from(example_bytes_seven).append(example_bytes_two).array());

        assertArrayEquals(Util.concat(example_bytes_eight, example_bytes_sixteen), Bytes.from(example_bytes_eight).append(Bytes.from(example_bytes_sixteen)).array());
    }

    @Test
    public void appendPrimitives() throws Exception {
        assertArrayEquals(Util.concat(example_bytes_eight, new byte[]{1}), Bytes.from(example_bytes_eight).append((byte) 1).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(2).putChar((char) 1423).array()), Bytes.from(example_bytes_eight).append((char) 1423).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(2).putShort((short) 4129).array()), Bytes.from(example_bytes_eight).append((short) 4129).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(4).putInt(362173671).array()), Bytes.from(example_bytes_eight).append(362173671).array());
        assertArrayEquals(Util.concat(example_bytes_eight, ByteBuffer.allocate(8).putLong(0x6762173671L).array()), Bytes.from(example_bytes_eight).append(0x6762173671L).array());
    }

    @Test
    public void appendMulti() throws Exception {
        Bytes b = Bytes.random(2);
        for (int i = 0; i < 100; i++) {
            byte[] oldByteArray = b.copy().array();
            Bytes newByte = Bytes.random(i % 16 + 1);
            b = b.append(newByte);
            assertArrayEquals(Util.concat(oldByteArray, newByte.copy().array()), b.array());
        }
    }

    @Test
    public void resizeGrow() throws Exception {
        assertArrayEquals(new byte[8], Bytes.from(new byte[0]).resize(8).array());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).resize(1).array());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).resize(1).array());
        assertArrayEquals(Util.concat(new byte[7], example_bytes_one), Bytes.from(example_bytes_one).resize(8).array());
        assertArrayEquals(Util.concat(new byte[1], example_bytes_seven), Bytes.from(example_bytes_seven).resize(8).array());
        assertArrayEquals(Util.concat(new byte[1], example_bytes_sixteen), Bytes.from(example_bytes_sixteen).resize(17).array());
    }

    @Test
    public void resizeShrink() throws Exception {
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
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void resizeSameInstance() throws Exception {
        Bytes b = Bytes.from(example_bytes_sixteen);
        Bytes b2 = b.resize(16);
        assertSame(b.array(), b2.array());
    }

    @Test
    public void xor() throws Exception {
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
        } catch (Exception e) {
        }
    }

    @Test
    public void or() throws Exception {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).or(new byte[0]).array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).or(new byte[1]).array());

        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).or(new byte[1]).array());
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).or(new byte[2]).array());
        assertArrayEquals(example_bytes_sixteen, Bytes.from(example_bytes_sixteen).or(new byte[16]).array());

        assertArrayEquals(new byte[]{0x67}, Bytes.from(example_bytes_one).or(example_bytes_one).array());
        assertArrayEquals(new byte[]{0x67}, Bytes.from(example_bytes_one).or(Bytes.from(example_bytes_one)).array());
        assertArrayEquals(new byte[]{-65, -33}, Bytes.from(new byte[]{(byte) 0xAE, (byte) 0x1E}).or(new byte[]{(byte) 0x15, (byte) 0xD3}).array());

        try {
            Bytes.from(example_bytes_seven).or(example_bytes_eight);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void and() throws Exception {
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
        } catch (Exception e) {
        }
    }

    @Test
    public void negate() throws Exception {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).not().array());
        assertArrayEquals(new byte[]{(byte) 0xFF}, Bytes.from(new byte[1]).not().array());

        assertArrayEquals(new byte[]{-104}, Bytes.from(example_bytes_one).not().array());
        assertArrayEquals(new byte[]{-27, -112}, Bytes.from(example_bytes_two).not().array());
        assertArrayEquals(new byte[]{81, -31}, Bytes.from(new byte[]{(byte) 0xAE, (byte) 0x1E}).not().array());

        assertArrayNotEquals(new byte[0], Bytes.from(example_bytes_one).not().array());
    }

    @Test
    public void reverse() throws Exception {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).reverse().array());
        assertArrayEquals(new byte[1], Bytes.from(new byte[1]).reverse().array());

        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).reverse().array());
        assertArrayEquals(new byte[]{example_bytes_two[1], example_bytes_two[0]}, Bytes.from(example_bytes_two).reverse().array());
        assertArrayEquals(new byte[]{example_bytes_four[3], example_bytes_four[2], example_bytes_four[1], example_bytes_four[0]}, Bytes.from(example_bytes_four).reverse().array());

        assertArrayNotEquals(new byte[0], Bytes.from(example_bytes_one).reverse().array());
    }

    @Test
    public void copy() throws Exception {
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
    public void shuffle() throws Exception {
        assertArrayNotEquals(example_bytes_twentyfour, Bytes.from(example_bytes_twentyfour).shuffle().array());
        assertArrayNotEquals(example_bytes_twentyfour, Bytes.from(example_bytes_twentyfour).shuffle(new SecureRandom()).array());
        assertArrayNotEquals(new byte[24], Bytes.from(example_bytes_twentyfour).shuffle(new SecureRandom()).array());
    }

    @Test
    public void sort() throws Exception {
        byte[] sorted = new byte[]{0, 1, 2, 3, 4, 5, 6};
        assertArrayEquals(sorted, Bytes.from(sorted).shuffle().sort().array());
        assertArrayEquals(sorted, Bytes.from(new byte[]{6, 0, 3, 4, 1, 5, 2}).sort().array());
        assertArrayEquals(Bytes.from(sorted).reverse().array(), Bytes.from(new byte[]{6, 0, 3, 4, 1, 5, 2}).sort(new Comparator<Byte>() {
            @Override
            public int compare(Byte o1, Byte o2) {
                return o2.compareTo(o1);
            }
        }).array());
    }

    @Test
    public void leftShift() throws Exception {
        assertArrayEquals(new byte[]{2}, Bytes.from(1).leftShift(1).array());
        assertArrayEquals(new byte[]{4}, Bytes.from(1).leftShift(2).array());
        assertArrayEquals(new byte[]{8}, Bytes.from(1).leftShift(3).array());
        assertArrayEquals(new byte[]{example_bytes_two[0], example_bytes_two[1], 0}, Bytes.from(example_bytes_two).leftShift(8).array());
        assertArrayEquals(new byte[]{example_bytes_two[0], example_bytes_two[1], 0, 0}, Bytes.from(example_bytes_two).leftShift(16).array());
    }

    @Test
    public void rightShift() throws Exception {
        assertArrayEquals(new byte[]{4}, Bytes.from(8).rightShift(1).array());
        assertArrayEquals(new byte[]{2}, Bytes.from(8).rightShift(2).array());
        assertArrayEquals(new byte[]{1}, Bytes.from(8).rightShift(3).array());
        assertArrayEquals(new byte[]{0}, Bytes.from(8).rightShift(4).array());
        assertArrayEquals(new byte[]{example_bytes_two[0]}, Bytes.from(example_bytes_two).rightShift(8).array());
        assertArrayEquals(new byte[1], Bytes.from(example_bytes_two).rightShift(16).array());
    }

    @Test
    public void bitSwitch() throws Exception {
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
    public void bitSwitchOutOfBounds() throws Exception {
        Bytes.from(4).switchBit(32, true);
    }

    @Test
    public void transform() throws Exception {
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).transform(new BytesTransformer() {
            @Override
            public byte[] transform(byte[] currentArray, boolean inPlace) {
                return Bytes.from(currentArray).array();
            }
        }).array());
        assertArrayEquals(new byte[2], Bytes.from(example_bytes_two).transform(new BytesTransformer() {
            @Override
            public byte[] transform(byte[] currentArray, boolean inPlace) {
                return Bytes.allocate(currentArray.length).array();
            }
        }).array());
    }
}