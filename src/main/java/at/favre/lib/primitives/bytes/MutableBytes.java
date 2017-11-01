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

package at.favre.lib.primitives.bytes;

import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * Mutable version of {@link Bytes}. If possible, all transformations are done in place, without creating a copy.
 * <p>
 * Adds additional mutator, which may change the internal array in-place, like {@link #wipe()}
 */
public final class MutableBytes extends Bytes {

    MutableBytes(byte[] byteArray, ByteOrder byteOrder) {
        super(byteArray, byteOrder, true, false);
    }

    /**
     * Uses given array to overwrite internal array
     *
     * @param newArray used to overwrite internal
     * @throws IndexOutOfBoundsException if newArray.length > internal length
     */
    public void overwrite(byte[] newArray) {
        overwrite(newArray, 0);
    }

    /**
     * Uses given array to overwrite internal array.
     *
     * @param newArray            used to overwrite internal
     * @param offsetInternalArray index of the internal array to start overwriting
     * @throws IndexOutOfBoundsException if newArray.length + offsetInternalArray > internal length
     */
    public void overwrite(byte[] newArray, int offsetInternalArray) {
        Objects.requireNonNull(newArray, "must provide non-null array as source");
        System.arraycopy(newArray, 0, internalArray(), offsetInternalArray, newArray.length);
    }

    /**
     * Fills the internal byte array with all zeros
     */
    public void wipe() {
        fill((byte) 0);
    }

    /**
     * Fills the internal byte array with provided byte
     *
     * @param fillByte to fill with
     */
    public void fill(byte fillByte) {
        Arrays.fill(internalArray(), fillByte);
    }

    /**
     * Fills the internal byte array with random data provided by {@link SecureRandom}
     */
    public void secureWipe() {
        secureWipe(new SecureRandom());
    }

    /**
     * Fills the internal byte array with random data provided by given random instance
     *
     * @param random to generate entropy
     */
    public void secureWipe(SecureRandom random) {
        Objects.requireNonNull(random, "must non-null random");
        random.nextBytes(internalArray());
    }
}
