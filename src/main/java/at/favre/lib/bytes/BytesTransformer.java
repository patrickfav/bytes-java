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
import java.util.*;

/**
 * Interface for transforming {@link Bytes}
 */
public interface BytesTransformer {
    /**
     * Transform given victim in place, overwriting its internal byte array
     *
     * @param victim  to preform the transformation on
     * @param inPlace perform the operations directly on the victim's byte array to omit copying of the internal array
     * @return resulting bytes
     */
    Bytes transform(Bytes victim, boolean inPlace);

    /**
     * Simple transformer for bitwise operations on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bitwise_operators">Bitwise operation</a>
     */
    final class BitWiseOperatorTransformer implements BytesTransformer {

        enum Mode {
            AND, OR, XOR
        }

        private final byte[] secondArray;
        private final Mode mode;

        public BitWiseOperatorTransformer(byte[] secondArray, Mode mode) {
            Objects.requireNonNull(secondArray, "the second byte array must not be null");
            Objects.requireNonNull(mode, "passed bitwise mode must not be null");
            this.secondArray = secondArray;
            this.mode = mode;
        }

        @Override
        public Bytes transform(Bytes victim, boolean inPlace) {
            if (victim.length() != secondArray.length) {
                throw new IllegalArgumentException("all byte array must be of same length doing bit wise operation");
            }

            byte[] out = inPlace ? victim.internalArray() : new byte[victim.length()];

            for (int i = 0; i < victim.length(); i++) {
                switch (mode) {
                    case OR:
                        out[i] = (byte) (victim.internalArray()[i] | secondArray[i]);
                        break;
                    case AND:
                        out[i] = (byte) (victim.internalArray()[i] & secondArray[i]);
                        break;
                    case XOR:
                        out[i] = (byte) (victim.internalArray()[i] ^ secondArray[i]);
                        break;
                    default:
                        throw new IllegalArgumentException("unknown bitwise transform mode " + mode);
                }
            }

            return inPlace ? victim : new Bytes(out, victim);
        }
    }

    /**
     * Simple transformer for bitwise unary negation on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#NOT">Bitwise operators: NOT</a>
     */
    final class NegateTransformer implements BytesTransformer {
        @Override
        public Bytes transform(Bytes victim, boolean inPlace) {
            byte[] out = inPlace ? victim.internalArray() : victim.copy().internalArray();

            for (int i = 0; i < victim.length(); i++) {
                out[i] = (byte) ~out[i];
            }

            return inPlace ? victim : new Bytes(out, victim);
        }
    }

    /**
     * Simple transformer for bit shifting {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bit_shifts">Bit shifts</a>
     */
    final class ShiftTransformer implements BytesTransformer {
        enum Type {
            LEFT_SHIFT, RIGHT_SHIFT
        }

        private final int shiftCount;
        private final Type type;

        public ShiftTransformer(int shiftCount, Type type) {
            Objects.requireNonNull(type, "passed shift type must not be null");

            this.shiftCount = shiftCount;
            this.type = type;
        }

        @Override
        public Bytes transform(Bytes victim, boolean inPlace) {
            BigInteger bigInt;

            if (inPlace) {
                bigInt = new BigInteger(victim.internalArray());
            } else {
                bigInt = new BigInteger(victim.copy().internalArray());
            }

            switch (type) {
                case LEFT_SHIFT:
                    return Bytes.wrap(bigInt.shiftLeft(shiftCount).toByteArray());
                case RIGHT_SHIFT:
                    return Bytes.wrap(bigInt.shiftRight(shiftCount).toByteArray());
                default:
                    throw new IllegalArgumentException("unknown shift type " + type);
            }
        }
    }

    /**
     * Simple transformer for bitwise operations on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bitwise_operators">Bitwise operation</a>
     */
    final class ConcatTransformer implements BytesTransformer {
        private final byte[] secondArray;

        public ConcatTransformer(byte[] secondArrays) {
            Objects.requireNonNull(secondArrays, "the second byte array must not be null");
            this.secondArray = secondArrays;
        }

        @Override
        public Bytes transform(Bytes victim, boolean inPlace) {
            return Bytes.wrap(Util.concat(victim.internalArray(), secondArray));
        }
    }

    /**
     * Reverses the internal byte array
     */
    final class ReverseTransformer implements BytesTransformer {
        @Override
        public Bytes transform(Bytes victim, boolean inPlace) {
            byte[] out = inPlace ? victim.internalArray() : victim.copy().internalArray();

            for (int i = 0; i < out.length / 2; i++) {
                byte temp = out[i];
                out[i] = out[out.length - i - 1];
                out[out.length - i - 1] = temp;
            }
            return inPlace ? victim : new Bytes(out, victim);
        }
    }

    /**
     * Sorts the internal byte array with given {@link java.util.Comparator}
     */
    final class SortTransformer implements BytesTransformer {
        private final Comparator<Byte> comparator;

        public SortTransformer() {
            this(null);
        }

        public SortTransformer(Comparator<Byte> comparator) {
            this.comparator = comparator;
        }

        @Override
        public Bytes transform(Bytes victim, boolean inPlace) {

            if (comparator == null) {
                byte[] out = inPlace ? victim.internalArray() : victim.copy().internalArray();
                Arrays.sort(out);
                return inPlace ? victim : new Bytes(out, victim);
            } else {
                //no in-place implementation with comparator
                List<Byte> list = victim.toList();
                Collections.sort(list, comparator);
                return Bytes.from(list);
            }
        }
    }

    /**
     * Shuffles the internal byte array
     */
    final class ShuffleTransformer implements BytesTransformer {
        private final Random random;

        public ShuffleTransformer(Random random) {
            Objects.requireNonNull(random, "passed random must not be null");
            this.random = random;
        }

        @Override
        public Bytes transform(Bytes victim, boolean inPlace) {
            byte[] out = inPlace ? victim.internalArray() : victim.copy().internalArray();
            Util.shuffle(out, random);
            return inPlace ? victim : new Bytes(out, victim);
        }
    }
}
