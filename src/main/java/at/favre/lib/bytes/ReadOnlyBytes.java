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
import java.nio.ReadOnlyBufferException;

/**
 * The read-only version of {@link Bytes} created by calling {@link #readOnly()}.
 * <p>
 * Read-only Bytes does not have access to the internal byte array ({@link #array()}
 * will throw an exception). Transformers will always create a copy and keep the
 * read-only status.
 */
public final class ReadOnlyBytes extends Bytes {

    /**
     * Creates a new read-only instance
     *
     * @param byteArray internal byte array
     * @param byteOrder the internal byte order - this is used to interpret given array, not to change it
     */
    ReadOnlyBytes(byte[] byteArray, ByteOrder byteOrder) {
        super(byteArray, byteOrder, new Factory());
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public byte[] array() {
        throw new ReadOnlyBufferException();
    }

    /**
     * Factory creating mutable byte types
     */
    private static class Factory implements BytesFactory {
        @Override
        public Bytes wrap(byte[] array, ByteOrder byteOrder) {
            return new ReadOnlyBytes(array, byteOrder);
        }

        @Override
        public Bytes wrap(Bytes other, byte[] array) {
            return wrap(array, other.byteOrder());
        }

        @Override
        public Bytes wrap(Bytes other) {
            return wrap(other.isMutable() ? other.copy().array() : other.array(), other.byteOrder());
        }
    }
}
