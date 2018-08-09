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

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class BytesSharedDataConverterTest extends ABytesTest {

    @Test
    public void array() {
        assertArrayEquals(new byte[0], Bytes.from(new byte[0]).array());
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).array());
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).array());
        assertArrayEquals(example_bytes_four, Bytes.from(example_bytes_four).array());
        assertArrayEquals(example_bytes_seven, Bytes.from(example_bytes_seven).array());
        assertArrayEquals(example_bytes_eight, Bytes.from(example_bytes_eight).array());
        assertArrayEquals(example_bytes_sixteen, Bytes.from(example_bytes_sixteen).array());
    }

    @Test
    public void bigInteger() {
        assertArrayEquals(example_bytes_one, Bytes.from(example_bytes_one).toBigInteger().toByteArray());
        assertArrayEquals(example_bytes_two, Bytes.from(example_bytes_two).toBigInteger().toByteArray());
        assertArrayEquals(example_bytes_four, Bytes.from(example_bytes_four).toBigInteger().toByteArray());
        assertArrayEquals(example_bytes_seven, Bytes.from(example_bytes_seven).toBigInteger().toByteArray());
        assertArrayEquals(example_bytes_eight, Bytes.from(example_bytes_eight).toBigInteger().toByteArray());
        assertArrayEquals(example_bytes_sixteen, Bytes.from(example_bytes_sixteen).toBigInteger().toByteArray());
    }

    @Test
    public void buffer() {
        assertEquals(ByteBuffer.wrap(new byte[0]), Bytes.from(new byte[0]).buffer());
        assertEquals(ByteBuffer.wrap(example_bytes_one), Bytes.from(example_bytes_one).buffer());
        assertEquals(ByteBuffer.wrap(example_bytes_two), Bytes.from(example_bytes_two).buffer());
        assertEquals(ByteBuffer.wrap(example_bytes_four), Bytes.from(example_bytes_four).buffer());
        assertEquals(ByteBuffer.wrap(example_bytes_seven), Bytes.from(example_bytes_seven).buffer());
        assertEquals(ByteBuffer.wrap(example_bytes_eight), Bytes.from(example_bytes_eight).buffer());
        assertEquals(ByteBuffer.wrap(example_bytes_sixteen), Bytes.from(example_bytes_sixteen).buffer());
    }

    @Test
    public void duplicate() {
        Bytes b = Bytes.from(example_bytes_sixteen);
        Bytes b2 = b.duplicate();
        assertNotSame(b, b2);
        assertSame(b.array(), b2.array());
    }

    @Test
    public void inputStream() {
        assertArrayEquals(example_bytes_one, Util.readFromStream(Bytes.from(example_bytes_one).inputStream(), -1));
        assertArrayEquals(example_bytes_two, Util.readFromStream(Bytes.from(example_bytes_two).inputStream(), -1));
        assertArrayEquals(example_bytes_four, Util.readFromStream(Bytes.from(example_bytes_four).inputStream(), -1));
        assertArrayEquals(example_bytes_sixteen, Util.readFromStream(Bytes.from(example_bytes_sixteen).inputStream(), -1));
    }
}
