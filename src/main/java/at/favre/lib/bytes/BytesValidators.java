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

/**
 * Util and easy access for {@link BytesValidators}
 */
public class BytesValidators {

    private BytesValidators() {
    }

    /**
     * Checks the length of a byte array
     *
     * @param value to check against
     * @return true if longer than given value
     */
    public static BytesValidator longerThan(int value) {
        return new BytesValidator.Length(value, BytesValidator.Length.Mode.GREATER_THAN);
    }

    public static BytesValidator shorterThan(int value) {
        return new BytesValidator.Length(value, BytesValidator.Length.Mode.SMALLER_THAN);
    }

    public static BytesValidator exactLength(int value) {
        return new BytesValidator.Length(value, BytesValidator.Length.Mode.EXACT);
    }

    public static BytesValidator onlyOf(byte value) {
        return new BytesValidator.IdenticalContent(value);
    }
}
