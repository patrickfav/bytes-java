/*
 * Copyright 2018 Patrick Favre-Bulle
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

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

/**
 * Derived from Google Guava's common/io/ BaseEncoding
 * <p>
 * See: https://github.com/google/guava/blob/v26.0/guava/src/com/google/common/io/BaseEncoding.java
 */
final class BaseEncoding implements BinaryToTextEncoding.EncoderDecoder {
    private static final char ASCII_MAX = 127;

    static final Alphabet BASE32_RFC4848 = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray());
    static final char BASE32_RFC4848_PADDING = '=';

    private final Alphabet alphabet;
    private final Character paddingChar;

    BaseEncoding(Alphabet alphabet, Character paddingChar) {
        this.alphabet = Objects.requireNonNull(alphabet);
        this.paddingChar = paddingChar;
    }

    private int maxEncodedSize(int bytes) {
        return alphabet.charsPerChunk * divide(bytes, alphabet.bytesPerChunk);
    }

    @Override
    public String encode(byte[] array, ByteOrder byteOrder) {
        return encode(array, 0, array.length);
    }

    private String encode(byte[] bytes, int off, int len) {
        StringBuilder result = new StringBuilder(maxEncodedSize(len));
        try {
            encodeTo(result, bytes, off, len);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
        return result.toString();
    }

    private void encodeTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
        Objects.requireNonNull(target);
        for (int i = 0; i < len; i += alphabet.bytesPerChunk) {
            encodeChunkTo(target, bytes, off + i, Math.min(alphabet.bytesPerChunk, len - i));
        }
    }

    private void encodeChunkTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
        Objects.requireNonNull(target);
        long bitBuffer = 0;
        for (int i = 0; i < len; ++i) {
            bitBuffer |= bytes[off + i] & 0xFF;
            bitBuffer <<= 8; // Add additional zero byte in the end.
        }
        // Position of first character is length of bitBuffer minus bitsPerChar.
        final int bitOffset = (len + 1) * 8 - alphabet.bitsPerChar;
        int bitsProcessed = 0;
        while (bitsProcessed < len * 8) {
            int charIndex = (int) (bitBuffer >>> (bitOffset - bitsProcessed)) & alphabet.mask;
            target.append(alphabet.encode(charIndex));
            bitsProcessed += alphabet.bitsPerChar;
        }
        if (paddingChar != null) {
            while (bitsProcessed < alphabet.bytesPerChunk * 8) {
                target.append(paddingChar);
                bitsProcessed += alphabet.bitsPerChar;
            }
        }
    }

    private int maxDecodedSize(int chars) {
        return (int) ((alphabet.bitsPerChar * (long) chars + 7L) / 8L);
    }

    private String trimTrailingPadding(CharSequence chars) {
        Objects.requireNonNull(chars);
        if (paddingChar == null) {
            return chars.toString();
        }
        int l;
        for (l = chars.length() - 1; l >= 0; l--) {
            if (chars.charAt(l) != paddingChar) {
                break;
            }
        }
        return chars.subSequence(0, l + 1).toString();
    }

    @Override
    public byte[] decode(String encoded) {
        encoded = trimTrailingPadding(encoded);
        byte[] tmp = new byte[maxDecodedSize(encoded.length())];
        int len = decodeTo(tmp, encoded);
        return extract(tmp, len);
    }

    private static byte[] extract(byte[] result, int length) {
        if (length == result.length) {
            return result;
        } else {
            byte[] trunc = new byte[length];
            System.arraycopy(result, 0, trunc, 0, length);
            return trunc;
        }
    }

    private int decodeTo(byte[] target, CharSequence chars) {
        Objects.requireNonNull(target);
        chars = trimTrailingPadding(chars);
        int bytesWritten = 0;
        for (int charIdx = 0; charIdx < chars.length(); charIdx += alphabet.charsPerChunk) {
            long chunk = 0;
            int charsProcessed = 0;
            for (int i = 0; i < alphabet.charsPerChunk; i++) {
                chunk <<= alphabet.bitsPerChar;
                if (charIdx + i < chars.length()) {
                    chunk |= alphabet.decode(chars.charAt(charIdx + charsProcessed++));
                }
            }
            final int minOffset = alphabet.bytesPerChunk * 8 - charsProcessed * alphabet.bitsPerChar;
            for (int offset = (alphabet.bytesPerChunk - 1) * 8; offset >= minOffset; offset -= 8) {
                target[bytesWritten++] = (byte) ((chunk >>> offset) & 0xFF);
            }
        }
        return bytesWritten;
    }

    private static final class Alphabet {
        // this is meant to be immutable -- don't modify it!
        private final char[] chars;
        final int mask;
        final int bitsPerChar;
        final int charsPerChunk;
        final int bytesPerChunk;
        private final byte[] decodabet;

        Alphabet(char[] chars) {
            this.chars = Objects.requireNonNull(chars);
            this.bitsPerChar = log2(chars.length);

            /*
             * e.g. for base64, bitsPerChar == 6, charsPerChunk == 4, and bytesPerChunk == 3. This makes
             * for the smallest chunk size that still has charsPerChunk * bitsPerChar be a multiple of 8.
             */
            int gcd = Math.min(8, Integer.lowestOneBit(bitsPerChar));
            this.charsPerChunk = 8 / gcd;
            this.bytesPerChunk = bitsPerChar / gcd;
            this.mask = chars.length - 1;

            byte[] decodabet = new byte[ASCII_MAX + 1];
            Arrays.fill(decodabet, (byte) -1);
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                decodabet[c] = (byte) i;
            }
            this.decodabet = decodabet;
        }

        char encode(int bits) {
            return chars[bits];
        }

        int decode(char ch) {
            return (int) decodabet[ch];
        }
    }

    private static int divide(int p, int q) {
        int div = p / q;
        int rem = p - q * div; // equal to p % q

        if (rem == 0) {
            return div;
        }
        int signum = 1 | ((p ^ q) >> (Integer.SIZE - 1));
        return signum > 0 ? div + signum : div;
    }

    private static int log2(int x) {
        return (Integer.SIZE - 1) - Integer.numberOfLeadingZeros(x);
    }
}
