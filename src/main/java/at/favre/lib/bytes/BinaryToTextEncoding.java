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
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Interface for byte-to-text-encodings
 *
 * @see <a href="https://en.wikipedia.org/wiki/Binary-to-text_encoding">Binary To Text Encoding</a>
 */
public interface BinaryToTextEncoding {

    /**
     * Interface for encoding bytes
     */
    interface Encoder {

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
    interface Decoder {

        /**
         * Decodes given encoded string
         *
         * @param encoded string
         * @return byte array represented by given encoded string
         */
        byte[] decode(CharSequence encoded);
    }

    /**
     * Unifies both interfaces {@link Encoder} and {@link Decoder}
     */
    interface EncoderDecoder extends Encoder, Decoder {
    }

    /**
     * Hex or Base16
     */
    class Hex implements EncoderDecoder {
        private static final char[] LOOKUP_TABLE_LOWER = new char[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66};
        private static final char[] LOOKUP_TABLE_UPPER = new char[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46};
        private final boolean upperCase;

        public Hex() {
            this(true);
        }

        public Hex(boolean upperCase) {
            this.upperCase = upperCase;
        }

        @Override
        public String encode(byte[] byteArray, ByteOrder byteOrder) {

            final char[] buffer = new char[byteArray.length * 2];
            final char[] lookup = upperCase ? LOOKUP_TABLE_UPPER : LOOKUP_TABLE_LOWER;

            int index;
            for (int i = 0; i < byteArray.length; i++) {
                index = (byteOrder == ByteOrder.BIG_ENDIAN) ? i : byteArray.length - i - 1;

                buffer[i << 1] = lookup[(byteArray[index] >> 4) & 0xF];
                buffer[(i << 1) + 1] = lookup[(byteArray[index] & 0xF)];
            }
            return new String(buffer);
        }

        @Override
        public byte[] decode(CharSequence hexString) {

            int start;
            if (Objects.requireNonNull(hexString).length() > 2 &&
                    hexString.charAt(0) == '0' && hexString.charAt(1) == 'x') {
                start = 2;
            } else {
                start = 0;
            }


            int len = hexString.length();
            boolean isOddLength = len % 2 != 0;
            if (isOddLength) {
                start--;
            }

            byte[] data = new byte[(len - start) / 2];
            int first4Bits;
            int second4Bits;
            for (int i = start; i < len; i += 2) {
                if (i == start && isOddLength) {
                    first4Bits = 0;
                } else {
                    first4Bits = Character.digit(hexString.charAt(i), 16);
                }
                second4Bits = Character.digit(hexString.charAt(i + 1), 16);

                if (first4Bits == -1 || second4Bits == -1) {
                    if (i == start && isOddLength) {
                        throw new IllegalArgumentException("'" + hexString.charAt(i + 1) + "' at index " + (i + 1) + " is not hex formatted");
                    } else {
                        throw new IllegalArgumentException("'" + hexString.charAt(i) + hexString.charAt(i + 1) + "' at index " + i + " is not hex formatted");
                    }
                }

                data[(i - start) / 2] = (byte) ((first4Bits << 4) + second4Bits);
            }
            return data;
        }
    }

    /**
     * Simple Base64 encoder
     */
    class Base64Encoding implements EncoderDecoder {
        private final boolean urlSafe;
        private final boolean padding;

        Base64Encoding() {
            this(false, true);
        }

        Base64Encoding(boolean urlSafe, boolean padding) {
            this.urlSafe = urlSafe;
            this.padding = padding;
        }

        @Override
        public String encode(byte[] array, ByteOrder byteOrder) {
            return new String(Base64.encode((byteOrder == ByteOrder.BIG_ENDIAN) ? array : Bytes.from(array).reverse().array(), urlSafe, padding), StandardCharsets.US_ASCII);
        }

        @Override
        public byte[] decode(CharSequence encoded) {
            return Base64.decode(encoded);
        }
    }

    /**
     * Simple radix encoder which internally uses {@link BigInteger#toString(int)}
     */
    class BaseRadixNumber implements EncoderDecoder {
        private final int radix;

        BaseRadixNumber(int radix) {
            if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
                throw new IllegalArgumentException("supported radix is between " + Character.MIN_RADIX + " and " + Character.MAX_RADIX);
            }
            this.radix = radix;
        }

        @Override
        public String encode(byte[] array, ByteOrder byteOrder) {
            return new BigInteger(1, (byteOrder == ByteOrder.BIG_ENDIAN) ? array : Bytes.from(array).reverse().array()).toString(radix);
        }

        @Override
        public byte[] decode(CharSequence encoded) {
            byte[] array = new BigInteger(encoded.toString(), radix).toByteArray();
            if (array[0] == 0) {
                byte[] tmp = new byte[array.length - 1];
                System.arraycopy(array, 1, tmp, 0, tmp.length);
                array = tmp;
            }
            return array;
        }
    }
}
