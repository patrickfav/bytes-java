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

import static org.junit.Assert.*;

public class BytesMiscTest extends ABytesTest {

    @Test
    public void testToString() throws Exception {
        testToString(Bytes.wrap(new byte[0]));
        testToString(Bytes.wrap(new byte[2]));
        testToString(Bytes.wrap(example_bytes_seven));
        testToString(Bytes.wrap(example2_bytes_seven));
        testToString(Bytes.wrap(example3_bytes_eight));
        testToString(Bytes.wrap(example4_bytes_sixteen));
    }

    private void testToString(Bytes bytes) {
        assertNotNull(bytes.toString());
        System.out.println(bytes.toString());
    }

    @Test
    public void testHashcode() throws Exception {
        assertEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.from(example_bytes_seven).hashCode());
        assertEquals(Bytes.wrap(example2_bytes_seven).hashCode(), Bytes.from(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example3_bytes_eight).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(0, Bytes.wrap(example2_bytes_seven).hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(Bytes.wrap(new byte[0]).equals(Bytes.wrap(new byte[0])));
        assertTrue(Bytes.wrap(new byte[16]).equals(Bytes.wrap(new byte[16])));
        assertTrue(Bytes.wrap(example_bytes_seven).equals(Bytes.from(example_bytes_seven)));
        assertTrue(Bytes.wrap(example2_bytes_seven).equals(Bytes.from(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_seven).equals(Bytes.wrap(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example3_bytes_eight).equals(Bytes.wrap(example2_bytes_seven)));
    }

    @Test
    public void testCompareTo() throws Exception {
        byte[] b1 = new byte[]{0x01};
        byte[] b2 = new byte[]{0x01, 0x02};

        assertTrue(-1 >= Bytes.from(b1).compareTo(Bytes.from(b2)));
        assertTrue(1 <= Bytes.from(b2).compareTo(Bytes.from(b1)));
        assertTrue(0 == Bytes.from(b1).compareTo(Bytes.from(b1)));

        byte[] bOne = new byte[]{0x01};
        byte[] bTwo = new byte[]{0x02};

        assertTrue(-1 >= Bytes.from(bOne).compareTo(Bytes.from(bTwo)));
        assertTrue(1 <= Bytes.from(bTwo).compareTo(Bytes.from(bOne)));
        assertTrue(0 == Bytes.from(bOne).compareTo(Bytes.from(bOne)));
    }

}