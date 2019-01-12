/*
 * Copyright 2019 Patrick Favre-Bulle
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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.*;

public class ImmutableBytesTest extends ABytesTest {
    @Test
    public void immutableShouldKeepProperty() {
        ImmutableBytes b = Bytes.from(example_bytes_seven).immutable();
        assertSame(b, b.immutable());
        assertFalse(b.isReadOnly());
        assertFalse(b.isMutable());
        assertFalse(b.copy().isMutable());
        assertFalse(b.reverse().isMutable());
        assertFalse(b.resize(7).isMutable());
        assertFalse(b.resize(6).isMutable());
        assertFalse(b.not().isMutable());
        assertFalse(b.leftShift(1).isMutable());
        assertFalse(b.rightShift(1).isMutable());
        assertFalse(b.and(Bytes.random(b.length())).isMutable());
        assertFalse(b.or(Bytes.random(b.length())).isMutable());
        assertFalse(b.xor(Bytes.random(b.length())).isMutable());
        assertFalse(b.append(3).isMutable());
        assertFalse(b.hashSha256().isMutable());
    }

    @Test
    public void testConvertImmutable() {
        Bytes b = Bytes.from(example_bytes_seven);
        Bytes m = b.copy();
        assertEquals(b, m);
        assertTrue(b.equalsContent(m));
        assertEquals(b.byteOrder(), m.byteOrder());
        assertTrue(b.isMutable());

        Bytes m2i = m.immutable();
        assertNotEquals(m2i, m);
        assertNotEquals(m2i, b);
        assertNotSame(m2i, b);
        assertTrue(m2i.equalsContent(m));
        assertEquals(m2i.byteOrder(), m.byteOrder());
        assertFalse(m2i.isMutable());

        assertEquals(m.length(), m2i.length());
        assertEquals(m.length(), b.length());

        assertNotEquals(example_bytes_seven[0], 0);
        assertEquals(example_bytes_seven[0], b.byteAt(0));
        assertEquals(example_bytes_seven[0], m.byteAt(0));
        assertEquals(example_bytes_seven[0], m2i.byteAt(0));

        m.fill((byte) 0);

        assertEquals(example_bytes_seven[0], b.byteAt(0));
        assertEquals(0, m.byteAt(0));
        assertNotEquals(0, m2i.byteAt(0));
    }

    Bytes fromAndTest(byte[] bytes) {
        Bytes b = Bytes.from(bytes);
        assertArrayEquals(bytes, b.array());
        return b;
    }

    @Test
    public void testAutoCloseableShouldThrowException() {
        try {
            try (Bytes b = Bytes.wrap(new byte[16]).immutable()) {
                SecretKey s = new SecretKeySpec(b.array(), "AES");
            }
            fail();
        } catch (UnsupportedOperationException ignored) {

        }

    }
}
