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

public abstract class AbstractBytes {

    private final byte[] byteArray;
    private final ByteOrder byteOrder;

    /**
     * Creates a new immutable instance
     *
     * @param byteArray internal byte array
     * @param byteOrder the internal byte order - this is used to interpret given array, not to change it
     */
    public AbstractBytes(byte[] byteArray, ByteOrder byteOrder) {
        this.byteArray = byteArray;
        this.byteOrder = byteOrder;
    }

    abstract BytesFactory getFactory();

    public boolean isMutable() {
        return false;
    }

    public boolean isReadOnly() {
        return false;
    }
}
