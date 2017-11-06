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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Interface for transforming {@link Bytes}
 */
public interface BytesTransformer {
    /**
     * Transform given victim in place, overwriting its internal byte array
     *
     * @param currentArray to preform the transformation on
     * @param inPlace      perform the operations directly on the victim's byte array to omit copying of the internal array
     * @return resulting bytes (either the overwritten instance or a new one)
     */
    byte[] transform(byte[] currentArray, boolean inPlace);

    /**
     * If this transformer supports transformation without creation a new array
     *
     * @return true if supported
     */
    boolean supportInPlaceTransformation();

    /**
     * Simple transformer for bitwise operations on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bitwise_operators">Bitwise operation</a>
     */
    final class BitWiseOperatorTransformer implements BytesTransformer {

        public enum Mode {
            AND, OR, XOR
        }

        private final byte[] secondArray;
        private final Mode mode;

        BitWiseOperatorTransformer(byte[] secondArray, Mode mode) {
            Objects.requireNonNull(secondArray, "the second byte array must not be null");
            Objects.requireNonNull(mode, "passed bitwise mode must not be null");
            this.secondArray = secondArray;
            this.mode = mode;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            if (currentArray.length != secondArray.length) {
                throw new IllegalArgumentException("all byte array must be of same length doing bit wise operation");
            }

            byte[] out = inPlace ? currentArray : new byte[currentArray.length];

            for (int i = 0; i < currentArray.length; i++) {
                switch (mode) {
                    default:
                    case OR:
                        out[i] = (byte) (currentArray[i] | secondArray[i]);
                        break;
                    case AND:
                        out[i] = (byte) (currentArray[i] & secondArray[i]);
                        break;
                    case XOR:
                        out[i] = (byte) (currentArray[i] ^ secondArray[i]);
                        break;
                }
            }

            return out;
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return true;
        }
    }

    /**
     * Simple transformer for bitwise unary negation on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#NOT">Bitwise operators: NOT</a>
     */
    final class NegateTransformer implements BytesTransformer {
        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            byte[] out = inPlace ? currentArray : Bytes.from(currentArray).array();

            for (int i = 0; i < out.length; i++) {
                out[i] = (byte) ~out[i];
            }

            return out;
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return true;
        }
    }

    /**
     * Simple transformer for bit shifting {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bit_shifts">Bit shifts</a>
     */
    final class ShiftTransformer implements BytesTransformer {
        public enum Type {
            LEFT_SHIFT, RIGHT_SHIFT
        }

        private final int shiftCount;
        private final Type type;

        ShiftTransformer(int shiftCount, Type type) {
            Objects.requireNonNull(type, "passed shift type must not be null");

            this.shiftCount = shiftCount;
            this.type = type;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            BigInteger bigInt = new BigInteger(currentArray);

            switch (type) {
                default:
                case LEFT_SHIFT:
                    return bigInt.shiftLeft(shiftCount).toByteArray();
                case RIGHT_SHIFT:
                    return bigInt.shiftRight(shiftCount).toByteArray();
            }
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return true;
        }
    }

    /**
     * Simple transformer for bitwise operations on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bitwise_operators">Bitwise operation</a>
     */
    final class ConcatTransformer implements BytesTransformer {
        private final byte[] secondArray;

        ConcatTransformer(byte[] secondArrays) {
            Objects.requireNonNull(secondArrays, "the second byte array must not be null");
            this.secondArray = secondArrays;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            return Util.concat(currentArray, secondArray);
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return false;
        }
    }

    /**
     * Reverses the internal byte array
     */
    final class ReverseTransformer implements BytesTransformer {
        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            byte[] out = inPlace ? currentArray : Bytes.from(currentArray).array();

            for (int i = 0; i < out.length / 2; i++) {
                byte temp = out[i];
                out[i] = out[out.length - i - 1];
                out[out.length - i - 1] = temp;
            }
            return out;
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return true;
        }
    }

    /**
     * Creates a new instance with a copy of the internal byte array and all other attributes.
     */
    final class CopyTransformer implements BytesTransformer {
        final int offset;
        final int length;

        CopyTransformer(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            byte[] copy = new byte[length];
            System.arraycopy(currentArray, offset, copy, 0, copy.length);
            return copy;
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return false;
        }
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain {@code (byte)0}.
     * <p>
     * If if the internal array will be grown, zero bytes will be added on the left,
     * keeping the value the same.
     */
    final class ResizeTransformer implements BytesTransformer {
        private final int newSize;

        ResizeTransformer(int newSize) {
            this.newSize = newSize;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            if (currentArray.length == newSize) {
                return currentArray;
            }

            if (newSize < 0) {
                throw new IllegalArgumentException("cannot resize to smaller than 0");
            }

            if (newSize == 0) {
                return new byte[0];
            }

            byte[] resizedArray = new byte[newSize];
            if (newSize > currentArray.length) {
                System.arraycopy(currentArray, 0, resizedArray, Math.max(0, Math.abs(newSize - currentArray.length)), Math.min(newSize, currentArray.length));
            } else {
                System.arraycopy(currentArray, Math.max(0, Math.abs(newSize - currentArray.length)), resizedArray, Math.min(0, Math.abs(newSize - currentArray.length)), Math.min(newSize, currentArray.length));
            }

            return resizedArray;
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return false;
        }
    }

    /**
     * Switches bits on specific position of an array
     */
    class BitSwitchTransformer implements BytesTransformer {
        private final int position;
        private final Boolean newBitValue;

        BitSwitchTransformer(int position, Boolean newBitValue) {
            this.position = position;
            this.newBitValue = newBitValue;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            byte[] out = inPlace ? currentArray : Bytes.from(currentArray).array();

            if (position < 0 || position >= 8 * currentArray.length) {
                throw new IllegalArgumentException("bit index " + (position * 8) + " out of bounds");
            }

            int bytePosition = currentArray.length - 1 - position / 8;
            if (newBitValue == null) {
                out[bytePosition] ^= (1 << position % 8);
            } else if (newBitValue) {
                out[bytePosition] |= (1 << position % 8);
            } else {
                out[bytePosition] &= ~(1 << position % 8);
            }
            return out;
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return true;
        }
    }

    /**
     * Converts to hash
     */
    class MessageDigestTransformer implements BytesTransformer {
        final static String ALGORITHM_SHA_256 = "SHA-256";

        private final MessageDigest messageDigest;

        MessageDigestTransformer(String digestName) {
            try {
                this.messageDigest = MessageDigest.getInstance(digestName);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("could not get message digest algorithm " + digestName, e);
            }
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            messageDigest.update(currentArray);
            return messageDigest.digest();
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return false;
        }
    }
}
