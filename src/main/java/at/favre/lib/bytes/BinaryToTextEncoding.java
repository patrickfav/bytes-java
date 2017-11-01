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

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Interface for byte-to-text-encodings
 *
 * @see <a href="https://en.wikipedia.org/wiki/Binary-to-text_encoding">Binary To Text Encoding</a>
 */
public final class BinaryToTextEncoding {

    /**
     * Interface for encoding bytes
     */
    public interface Encoder {

        /**
         * Encodes given array with given byte order to a string
         *
         * @param array     to encode
         * @param byteOrder the array is in
         * @return encoded string
         */
        String encode(byte[] array, ByteOrder byteOrder);
    }

    /**
     * Interface for decoding encoded strings
     */
    public interface Decoder {

        /**
         * Decodes given encoded string
         *
         * @param encoded string
         * @return byte array represented by given encoded string
         */
        byte[] decode(String encoded);
    }

    /**
     * Hex or Base16
     */
    public static class Hex implements Encoder, Decoder {
        private final boolean upperCase;

        public Hex() {
            this(true);
        }

        public Hex(boolean upperCase) {
            this.upperCase = upperCase;
        }

        @Override
        public String encode(byte[] byteArray, ByteOrder byteOrder) {
            StringBuilder sb = new StringBuilder(byteArray.length * 2);
            for (byte anArray : byteArray) {
                char first4Bit = Character.forDigit((anArray >> 4) & 0xF, 16);
                char last4Bit = Character.forDigit((anArray & 0xF), 16);
                if (upperCase) {
                    first4Bit = Character.toUpperCase(first4Bit);
                    last4Bit = Character.toUpperCase(last4Bit);
                }
                sb.append(first4Bit).append(last4Bit);
            }
            return sb.toString();
        }

        @Override
        public byte[] decode(String hexString) {
            Objects.requireNonNull(hexString, "hex input must not be null");
            if (hexString.length() % 2 != 0)
                throw new IllegalArgumentException("invalid hex string, must be mod 2 == 0");

            if (hexString.startsWith("0x")) {
                hexString = hexString.substring(2, hexString.length());
            }

            int len = hexString.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte)
                        ((Character.digit(hexString.charAt(i), 16) << 4)
                                + Character.digit(hexString.charAt(i + 1), 16));
            }
            return data;
        }
    }

    public static class Base64Encoding implements Encoder, Decoder {
        @Override
        public String encode(byte[] array, ByteOrder byteOrder) {
            return Base64.encode(array);
        }

        @Override
        public byte[] decode(String encoded) {
            return Base64.decode(encoded);
        }
    }

    public static class BaseRadix implements Encoder, Decoder {
        private final int radix;

        BaseRadix(int radix) {
            this.radix = radix;
        }

        @Override
        public String encode(byte[] array, ByteOrder byteOrder) {
            return new BigInteger(1, array).toString(radix);
        }

        @Override
        public byte[] decode(String encoded) {
            return new BigInteger(encoded, radix).toByteArray();
        }
    }
}
