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
     * @param byteLength to check against
     * @return true if longer or equal to given value
     */
    public static BytesValidator atLeast(int byteLength) {
        return new BytesValidator.Length(byteLength, BytesValidator.Length.Mode.GREATER_OR_EQ_THAN);
    }

    /**
     * Checks the length of a byte array
     *
     * @param byteLength to check against
     * @return true if smaller or equal to given value
     */
    public static BytesValidator atMost(int byteLength) {
        return new BytesValidator.Length(byteLength, BytesValidator.Length.Mode.SMALLER_OR_EQ_THAN);
    }

    /**
     * Checks the length of a byte array
     *
     * @param byteLength to check against
     * @return true if equal to given value
     */
    public static BytesValidator exactLength(int byteLength) {
        return new BytesValidator.Length(byteLength, BytesValidator.Length.Mode.EXACT);
    }

    /**
     * Checks individual byte content
     *
     * @param refByte to check against
     * @return true if array only consists of refByte
     */
    public static BytesValidator onlyOf(byte refByte) {
        return new BytesValidator.IdenticalContent(refByte, BytesValidator.IdenticalContent.Mode.ONLY_OF);
    }

    /**
     * Checks individual byte content
     *
     * @param refByte to check against
     * @return true if array has at least one byte that is not refByte
     */
    public static BytesValidator notOnlyOf(byte refByte) {
        return new BytesValidator.IdenticalContent(refByte, BytesValidator.IdenticalContent.Mode.NOT_ONLY_OF);
    }

    /**
     * Checks individual byte content
     *
     * @param refByte to check against
     * @return true if array has no value refByte
     */
    public static BytesValidator noneOf(byte refByte) {
        return new BytesValidator.IdenticalContent(refByte, BytesValidator.IdenticalContent.Mode.NONE_OF);
    }
}
