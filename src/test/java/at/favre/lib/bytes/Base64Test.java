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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

/**
 * Test cases from rfc4648
 */
public class Base64Test {

    @Test
    public void decode() {
        assertArrayEquals("".getBytes(), Base64.decode(""));
        assertArrayEquals("f".getBytes(), Base64.decode("Zg=="));
        assertArrayEquals("fo".getBytes(), Base64.decode("Zm8="));
        assertArrayEquals("foo".getBytes(), Base64.decode("Zm9v"));
        assertArrayEquals("foob".getBytes(), Base64.decode("Zm9vYg=="));
        assertArrayEquals("fooba".getBytes(), Base64.decode("Zm9vYmE="));
        assertArrayEquals("foobar".getBytes(), Base64.decode("Zm9vYmFy"));
        assertArrayEquals("k".getBytes(), Base64.decode("a+"));
        assertArrayEquals("i".getBytes(), Base64.decode("aZ\n"));
        assertArrayEquals("foob".getBytes(), Base64.decode("Zm9vYg=========="));
        assertArrayEquals(new byte[]{106, -64}, Base64.decode("a\rs\t\nC "));
        assertNull(Base64.decode("a\r\t\n "));
    }

    @Test
    public void encode() {
        assertArrayEquals(Bytes.from("").array(), Base64.encode("".getBytes()));
        assertArrayEquals(Bytes.from("Zg==").array(), Base64.encode("f".getBytes()));
        assertArrayEquals(Bytes.from("Zm8=").array(), Base64.encode("fo".getBytes()));
        assertArrayEquals(Bytes.from("Zm9v").array(), Base64.encode("foo".getBytes()));
        assertArrayEquals(Bytes.from("Zm9vYg==").array(), Base64.encode("foob".getBytes()));
        assertArrayEquals(Bytes.from("Zm9vYmE=").array(), Base64.encode("fooba".getBytes()));
        assertArrayEquals(Bytes.from("Zm9vYmFy").array(), Base64.encode("foobar".getBytes()));
        assertArrayEquals(Bytes.from("aQo=").array(), Base64.encode("i\n".getBytes()));
        assertArrayEquals(Bytes.from("aSA=").array(), Base64.encode("i ".getBytes()));
    }

}
