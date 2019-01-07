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

import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * Mutable version of {@link Bytes} created by calling {@link #mutable()}. If possible, all transformations are done in place, without creating a copy.
 * <p>
 * Adds additional mutator, which may change the internal array in-place, like {@link #wipe()}
 */
@SuppressWarnings("WeakerAccess")
public final class MutableBytes extends Bytes {

    MutableBytes(byte[] byteArray, ByteOrder byteOrder) {
        super(byteArray, byteOrder, new Factory());
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * Uses given array to overwrite internal array
     *
     * @param newArray used to overwrite internal
     * @return this instance
     * @throws IndexOutOfBoundsException if newArray.length &gt; internal length
     */
    public MutableBytes overwrite(byte[] newArray) {
        overwrite(newArray, 0);
        return this;
    }

    /**
     * Uses given array to overwrite internal array.
     *
     * @param newArray            used to overwrite internal
     * @param offsetInternalArray index of the internal array to start overwriting
     * @return this instance
     * @throws IndexOutOfBoundsException if newArray.length + offsetInternalArray &gt; internal length
     */
    public MutableBytes overwrite(byte[] newArray, int offsetInternalArray) {
        Objects.requireNonNull(newArray, "must provide non-null array as source");
        System.arraycopy(newArray, 0, internalArray(), offsetInternalArray, newArray.length);
        return this;
    }

    /**
     * Sets new byte to given index
     *
     * @param index   the index to change
     * @param newByte the new byte to set
     * @return this instance
     */
    public MutableBytes setByteAt(int index, byte newByte) {
        internalArray()[index] = newByte;
        return this;
    }

    /**
     * Fills the internal byte array with all zeros
     *
     * @return this instance
     */
    public MutableBytes wipe() {
        fill((byte) 0);
        return this;
    }

    /**
     * Fills the internal byte array with provided byte
     *
     * @param fillByte to fill with
     * @return this instance
     */
    public MutableBytes fill(byte fillByte) {
        Arrays.fill(internalArray(), fillByte);
        return this;
    }

    /**
     * Fills the internal byte array with random data provided by {@link SecureRandom}
     *
     * @return this instance
     */
    public MutableBytes secureWipe() {
        secureWipe(new SecureRandom());
        return this;
    }

    /**
     * Fills the internal byte array with random data provided by given random instance
     *
     * @param random to generate entropy
     * @return this instance
     */
    public MutableBytes secureWipe(SecureRandom random) {
        Objects.requireNonNull(random, "random param must not be null");
        if (length() > 0) {
            random.nextBytes(internalArray());
        }
        return this;
    }

    /**
     * Convert this instance to an immutable version with the same reference of the internal array and byte-order.
     * If the mutable instance is kept, it can be used to alter the internal array of the just created instance, so be
     * aware.
     *
     * @return immutable version of this instance
     */
    public Bytes immutable() {
        return Bytes.wrap(internalArray(), byteOrder());
    }

    @Override
    public int hashCode() {
        return Util.Obj.hashCode(internalArray(), byteOrder());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Factory creating mutable byte types
     */
    private static class Factory implements BytesFactory {
        @Override
        public Bytes wrap(byte[] array, ByteOrder byteOrder) {
            return new MutableBytes(array, byteOrder);
        }
    }
}
