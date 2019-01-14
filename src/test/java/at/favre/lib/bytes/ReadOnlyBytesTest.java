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

import static org.junit.Assert.*;

public class ReadOnlyBytesTest extends ABytesTest {
    @Test
    public void readOnlyShouldKeepProperty() {
        ReadOnlyBytes b = Bytes.of(example_bytes_seven).readOnly();
        assertSame(b, b.readOnly());
        assertFalse(b.isMutable());
        assertTrue(b.isReadOnly());
        assertTrue(b.copy().isReadOnly());
        assertTrue(b.reverse().isReadOnly());
        assertTrue(b.resize(7).isReadOnly());
        assertTrue(b.resize(6).isReadOnly());
        assertTrue(b.not().isReadOnly());
        assertTrue(b.leftShift(1).isReadOnly());
        assertTrue(b.rightShift(1).isReadOnly());
        assertTrue(b.and(Bytes.random(b.length())).isReadOnly());
        assertTrue(b.or(Bytes.random(b.length())).isReadOnly());
        assertTrue(b.xor(Bytes.random(b.length())).isReadOnly());
        assertTrue(b.append(3).isReadOnly());
        assertTrue(b.hashSha256().isReadOnly());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readOnlyDuplicateNotAllowed() {
        Bytes.of(example_bytes_seven).readOnly().duplicate();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readOnlyImmutableNotAllowed() {
        Bytes.of(example_bytes_seven).readOnly().immutable();
    }

    @Test
    public void readOnly() {
        assertFalse(Bytes.of(example_bytes_twentyfour).isReadOnly());
        assertTrue(Bytes.of(example_bytes_twentyfour).readOnly().isReadOnly());
        assertTrue(Bytes.of(example_bytes_twentyfour).readOnly().copy().isReadOnly());

        assertArrayEquals(example_bytes_twentyfour, Bytes.of(example_bytes_twentyfour).readOnly().internalArray());
        try {
            Bytes.of(example_bytes_twentyfour).readOnly().array();
            fail();
        } catch (UnsupportedOperationException ignored) {
        }

        Bytes b = Bytes.of(example_bytes_twentyfour).readOnly();
        assertSame(b, b.readOnly());
    }
}
