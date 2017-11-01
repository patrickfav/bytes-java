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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BytesByteOrderTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void fromLong() throws Exception {
        long number = 172863182736L;
        System.out.println(Bytes.from(number).encodeHex());
        System.out.println(Bytes.from(number).toLong());
        assertArrayEquals(Bytes.parseHex("000000283f72d790").array(), Bytes.from(number).array());
        assertEquals(number, Bytes.from(number).toLong());

        byte[] defaultArray = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};

        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.BIG_ENDIAN)).encodeHex());
        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.BIG_ENDIAN).getInt()).encodeHex());
        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.LITTLE_ENDIAN)).encodeHex());
        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.LITTLE_ENDIAN).getInt()).encodeHex());
    }
}