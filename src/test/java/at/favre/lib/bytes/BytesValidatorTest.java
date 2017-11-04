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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BytesValidatorTest extends ABytesTest {

    @Test
    public void testOnlyOfValidator() throws Exception {
        assertFalse(Bytes.allocate(0).validateNotOnlyZeros());
        assertFalse(Bytes.allocate(2).validateNotOnlyZeros());
        assertTrue(Bytes.wrap(example_bytes_seven).validateNotOnlyZeros());
        assertTrue(Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 1}).validateNotOnlyZeros());
        assertTrue(Bytes.random(128).validateNotOnlyZeros());

        assertTrue(Bytes.allocate(1).validate(BytesValidators.onlyOf((byte) 0)));
        assertFalse(Bytes.allocate(1).validate(BytesValidators.noneOf((byte) 0)));
        assertFalse(Bytes.allocate(1).validate(BytesValidators.onlyOf((byte) 1)));
        assertTrue(Bytes.allocate(1).validate(BytesValidators.noneOf((byte) 1)));
        assertTrue(Bytes.allocate(1).validate(BytesValidators.noneOf((byte) 1)));
        assertTrue(Bytes.wrap(new byte[]{1, 1, 1, 1, 0, 1}).validate(BytesValidators.notOnlyOf((byte) 1)));
        assertFalse(Bytes.wrap(new byte[]{1, 1, 1, 1, 1}).validate(BytesValidators.notOnlyOf((byte) 1)));
        assertTrue(Bytes.wrap(new byte[]{1, 1, 1, 1, 1, 1}).validate(BytesValidators.onlyOf((byte) 1)));
    }

    @Test
    public void testLengthValidators() throws Exception {
        assertFalse(Bytes.allocate(0).validate(BytesValidators.atLeast(1)));
        assertTrue(Bytes.allocate(1).validate(BytesValidators.atLeast(1)));
        assertTrue(Bytes.allocate(2).validate(BytesValidators.atLeast(1)));

        assertFalse(Bytes.allocate(2).validate(BytesValidators.atMost(1)));
        assertTrue(Bytes.allocate(1).validate(BytesValidators.atMost(1)));
        assertTrue(Bytes.allocate(0).validate(BytesValidators.atMost(1)));

        assertFalse(Bytes.allocate(0).validate(BytesValidators.exactLength(1)));
        assertTrue(Bytes.allocate(1).validate(BytesValidators.exactLength(1)));
        assertFalse(Bytes.allocate(2).validate(BytesValidators.exactLength(1)));
    }
}