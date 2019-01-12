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

import java.util.Arrays;
import java.util.Collections;

import static at.favre.lib.bytes.BytesValidators.*;
import static org.junit.Assert.*;

public class BytesValidatorTest extends ABytesTest {

    @Test
    public void testOnlyOfValidator() {
        assertFalse(Bytes.allocate(0).validateNotOnlyZeros());
        assertFalse(Bytes.allocate(2).validateNotOnlyZeros());
        assertTrue(Bytes.wrap(example_bytes_seven).validateNotOnlyZeros());
        assertTrue(Bytes.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 1}).validateNotOnlyZeros());
        assertTrue(Bytes.random(128).validateNotOnlyZeros());

        assertTrue(Bytes.allocate(1).validate(onlyOf((byte) 0)));
        assertFalse(Bytes.allocate(1).validate(noneOf((byte) 0)));
        assertFalse(Bytes.allocate(1).validate(onlyOf((byte) 1)));
        assertTrue(Bytes.allocate(1).validate(noneOf((byte) 1)));
        assertTrue(Bytes.allocate(1).validate(noneOf((byte) 1)));
        assertTrue(Bytes.wrap(new byte[]{1, 1, 1, 1, 0, 1}).validate(notOnlyOf((byte) 1)));
        assertFalse(Bytes.wrap(new byte[]{1, 1, 1, 1, 1}).validate(notOnlyOf((byte) 1)));
        assertTrue(Bytes.wrap(new byte[]{1, 1, 1, 1, 1, 1}).validate(onlyOf((byte) 1)));
    }

    @Test
    public void testLengthValidators() {
        assertFalse(Bytes.allocate(0).validate(atLeast(1)));
        assertTrue(Bytes.allocate(1).validate(atLeast(1)));
        assertTrue(Bytes.allocate(2).validate(atLeast(1)));

        assertFalse(Bytes.allocate(2).validate(atMost(1)));
        assertTrue(Bytes.allocate(1).validate(atMost(1)));
        assertTrue(Bytes.allocate(0).validate(atMost(1)));

        assertFalse(Bytes.allocate(0).validate(exactLength(1)));
        assertTrue(Bytes.allocate(1).validate(exactLength(1)));
        assertFalse(Bytes.allocate(2).validate(exactLength(1)));
    }

    @Test
    public void testOrValidation() {
        assertTrue(Bytes.allocate(0).validate(or(exactLength(1), exactLength(0))));
        assertTrue(Bytes.allocate(2).validate(or(atLeast(3), onlyOf((byte) 0))));
        assertTrue(Bytes.allocate(3).validate(or(onlyOf((byte) 1), onlyOf((byte) 0))));
        assertTrue(Bytes.wrap(new byte[]{0, 0}).validate(or(onlyOf((byte) 1), onlyOf((byte) 0))));
        assertFalse(Bytes.wrap(new byte[]{1, 0}).validate(or(onlyOf((byte) 2), onlyOf((byte) 1), onlyOf((byte) 0))));
    }

    @Test
    public void testAndValidation() {
        assertFalse(Bytes.allocate(5).validate(and(atLeast(3), notOnlyOf((byte) 0))));
        assertFalse(Bytes.wrap(new byte[]{1, 0}).validate(and(atLeast(3), notOnlyOf((byte) 0))));
        assertTrue(Bytes.wrap(new byte[]{1, 0, 0}).validate(and(atLeast(3), notOnlyOf((byte) 0))));
        assertFalse(Bytes.allocate(21).validate(and(atLeast(3), atMost(20))));
    }

    @Test
    public void testNotValidation() {
        assertEquals(Bytes.allocate(2).validate(not(onlyOf((byte) 0))), Bytes.allocate(2).validate(notOnlyOf((byte) 0)));
        assertTrue(Bytes.allocate(2).validate(not(atLeast(16))));
        assertFalse(Bytes.allocate(2).validate(not(atMost(16))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogicalNoElements() {
        Bytes.allocate(2).validate(new BytesValidator.Logical(Collections.<BytesValidator>emptyList(), BytesValidator.Logical.Operator.AND));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogicalTooManyElements() {
        Bytes.allocate(2).validate(new BytesValidator.Logical(
                Arrays.<BytesValidator>asList(new BytesValidator.Length(2, BytesValidator.Length.Mode.GREATER_OR_EQ_THAN), new BytesValidator.Length(2, BytesValidator.Length.Mode.EXACT))
                , BytesValidator.Logical.Operator.NOT));
    }

    @Test
    public void testNestedValidation() {
        assertTrue(Bytes.allocate(16).validate(
                or(and(atLeast(8), not(onlyOf(((byte) 0)))),
                        or(exactLength(16), exactLength(12)))));

        assertTrue(Bytes.allocate(16).validate(or(exactLength(16), exactLength(12))));
        assertFalse(Bytes.allocate(16).validate(and(atLeast(8), not(onlyOf(((byte) 0))))));
        assertTrue(Bytes.allocate(16).validate(and(atLeast(8), onlyOf(((byte) 0)))));
        assertTrue(Bytes.allocate(16).validate(or(not(onlyOf(((byte) 0))), exactLength(16))));
    }

    @Test
    public void testStartWithValidate() {
        assertTrue(Bytes.wrap(new byte[]{0, 3, 0}).validate(startsWith((byte) 0, (byte) 3)));
        assertFalse(Bytes.wrap(new byte[]{0, 2, 0}).validate(startsWith((byte) 0, (byte) 3)));
        assertTrue(Bytes.wrap(new byte[]{0, 2, 0}).validate(startsWith((byte) 0)));
        assertFalse(Bytes.wrap(new byte[]{0, 2, 0}).validate(startsWith((byte) 2)));
        assertTrue(Bytes.allocate(16).validate(startsWith((byte) 0)));
        assertFalse(Bytes.allocate(16).validate(startsWith(Bytes.allocate(17).array())));
    }

    @Test
    public void testEndsWithValidate() {
        assertTrue(Bytes.wrap(new byte[]{1, 2, 3}).validate(endsWith((byte) 2, (byte) 3)));
        assertFalse(Bytes.wrap(new byte[]{0, 2, 0}).validate(endsWith((byte) 3, (byte) 0)));
        assertTrue(Bytes.wrap(new byte[]{0, 2, 0}).validate(endsWith((byte) 0)));
        assertFalse(Bytes.wrap(new byte[]{0, 2, 0}).validate(endsWith((byte) 2)));
        assertTrue(Bytes.allocate(16).validate(endsWith((byte) 0)));
        assertFalse(Bytes.allocate(16).validate(endsWith(Bytes.allocate(17).array())));
    }
}
