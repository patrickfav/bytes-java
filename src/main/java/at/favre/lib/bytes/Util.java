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

import java.io.*;
import java.nio.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

/**
 * Common Util methods to convert or modify byte arrays
 */
final class Util {

    /**
     * Util methods related general purpose byte utility.
     */
    static final class Byte {
        private Byte() {
        }

        /**
         * Returns the values from each provided byteArray combined into a single byteArray.
         * For example, {@code append(new byte[] {a, b}, new byte[] {}, new
         * byte[] {c}} returns the byteArray {@code {a, b, c}}.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param arrays zero or more {@code byte} arrays
         * @return a single byteArray containing all the values from the source arrays, in
         * order
         */
        static byte[] concat(byte[]... arrays) {
            int length = 0;
            for (byte[] array : arrays) {
                length += array.length;
            }
            byte[] result = new byte[length];
            int pos = 0;
            for (byte[] array : arrays) {
                System.arraycopy(array, 0, result, pos, array.length);
                pos += array.length;
            }
            return result;
        }

        /**
         * Combines a single argument with a vararg to a single array
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param firstByte first arg
         * @param moreBytes varargs
         * @return array containing all args
         */
        static byte[] concatVararg(byte firstByte, byte[] moreBytes) {
            if (moreBytes == null) {
                return new byte[]{firstByte};
            } else {
                return concat(new byte[]{firstByte}, moreBytes);
            }
        }

        /**
         * Returns the start position of the first occurrence of the specified {@code
         * target} within {@code array}, or {@code -1} if there is no such occurrence.
         * <p>
         * <p>More formally, returns the lowest index {@code i} such that {@code
         * java.util.Arrays.copyOfRange(array, i, i + target.length)} contains exactly
         * the same elements as {@code target}.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n*m)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array  the array to search for the sequence {@code target}
         * @param target the array to search for as a sub-sequence of {@code array}
         */
        static int indexOf(byte[] array, byte[] target, int start, int end) {
            Objects.requireNonNull(array, "array must not be null");
            Objects.requireNonNull(target, "target must not be null");
            if (target.length == 0 || start < 0) {
                return -1;
            }

            outer:
            for (int i = start; i < Math.min(end, array.length - target.length + 1); i++) {
                for (int j = 0; j < target.length; j++) {
                    if (array[i + j] != target[j]) {
                        continue outer;
                    }
                }
                return i;
            }
            return -1;
        }

        /**
         * Returns the index of the last appearance of the value {@code target} in
         * {@code array}.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array  an array of {@code byte} values, possibly empty
         * @param target a primitive {@code byte} value
         * @return the greatest index {@code i} for which {@code array[i] == target},
         * or {@code -1} if no such index exists.
         */
        static int lastIndexOf(byte[] array, byte target, int start, int end) {
            for (int i = end - 1; i >= start; i--) {
                if (array[i] == target) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Counts the occurrence of target in the the in the subject array
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array  to count in
         * @param target to count
         * @return number of times target is in subject
         */
        static int countByte(byte[] array, byte target) {
            int count = 0;
            for (byte b : array) {
                if (b == target) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Counts the times given pattern (ie. an array) can be found in given array
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n*m)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array   to count in
         * @param pattern to match in array
         * @return number of times pattern is in subject
         */
        static int countByteArray(byte[] array, byte[] pattern) {
            Objects.requireNonNull(pattern, "pattern must not be null");
            if (pattern.length == 0 || array.length == 0) {
                return 0;
            }
            int count = 0;
            outer:
            for (int i = 0; i < array.length - pattern.length + 1; i++) {
                for (int j = 0; j < pattern.length; j++) {
                    if (array[i + j] != pattern[j]) {
                        continue outer;
                    }
                }
                count++;
            }
            return count;
        }

        /**
         * Simple Durstenfeld shuffle.
         * This will shuffle given array and will not make a copy, so beware.
         * <p>
         * See: https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>true</code></li>
         * </ul>
         * </p>
         *
         * @param array  to shuffle
         * @param random used to derive entropy - use {@link java.security.SecureRandom} instance if you want this to be secure
         */
        static void shuffle(byte[] array, Random random) {
            for (int i = array.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                byte a = array[index];
                array[index] = array[i];
                array[i] = a;
            }
        }

        /**
         * Reverses the elements of {@code array} between {@code fromIndex} inclusive and {@code toIndex}
         * exclusive. This is equivalent to {@code
         * Collections.reverse(Bytes.asList(array).subList(fromIndex, toIndex))}, but is likely to be more
         * efficient.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>true</code></li>
         * </ul>
         * </p>
         *
         * @throws IndexOutOfBoundsException if {@code fromIndex < 0}, {@code toIndex > array.length}, or
         *                                   {@code toIndex > fromIndex}
         */
        static void reverse(byte[] array, int fromIndex, int toIndex) {
            Objects.requireNonNull(array);
            for (int i = fromIndex, j = toIndex - 1; i < j; i++, j--) {
                byte tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
        }

        /**
         * Light shift of whole byte array by shiftBitCount bits.
         * This method will alter the input byte array.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>true</code></li>
         * </ul>
         * </p>
         *
         * @param byteArray     to shift
         * @param shiftBitCount how many bits to shift
         * @param byteOrder     endianness of given byte array
         * @return shifted byte array
         */
        static byte[] shiftLeft(byte[] byteArray, int shiftBitCount, ByteOrder byteOrder) {
            final int shiftMod = shiftBitCount % 8;
            final byte carryMask = (byte) ((1 << shiftMod) - 1);
            final int offsetBytes = (shiftBitCount / 8);

            int sourceIndex;
            if (byteOrder == ByteOrder.BIG_ENDIAN) {
                for (int i = 0; i < byteArray.length; i++) {
                    sourceIndex = i + offsetBytes;
                    if (sourceIndex >= byteArray.length) {
                        byteArray[i] = 0;
                    } else {
                        byte src = byteArray[sourceIndex];
                        byte dst = (byte) (src << shiftMod);
                        if (sourceIndex + 1 < byteArray.length) {
                            dst |= byteArray[sourceIndex + 1] >>> (8 - shiftMod) & carryMask;
                        }
                        byteArray[i] = dst;
                    }
                }
            } else {
                for (int i = byteArray.length - 1; i >= 0; i--) {
                    sourceIndex = i - offsetBytes;
                    if (sourceIndex < 0) {
                        byteArray[i] = 0;
                    } else {
                        byte src = byteArray[sourceIndex];
                        byte dst = (byte) (src << shiftMod);
                        if (sourceIndex - 1 >= 0) {
                            dst |= byteArray[sourceIndex - 1] >>> (8 - shiftMod) & carryMask;
                        }
                        byteArray[i] = dst;
                    }
                }
            }
            return byteArray;
        }

        /**
         * Unsigned/logical right shift of whole byte array by shiftBitCount bits.
         * This method will alter the input byte array.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>true</code></li>
         * </ul>
         * </p>
         *
         * @param byteArray     to shift
         * @param shiftBitCount how many bits to shift
         * @param byteOrder     endianness of given byte array
         * @return shifted byte array
         */
        static byte[] shiftRight(byte[] byteArray, int shiftBitCount, ByteOrder byteOrder) {
            final int shiftMod = shiftBitCount % 8;
            final byte carryMask = (byte) (0xFF << (8 - shiftMod));
            final int offsetBytes = (shiftBitCount / 8);

            int sourceIndex;
            if (byteOrder == ByteOrder.BIG_ENDIAN) {
                for (int i = byteArray.length - 1; i >= 0; i--) {
                    sourceIndex = i - offsetBytes;
                    if (sourceIndex < 0) {
                        byteArray[i] = 0;
                    } else {
                        byte src = byteArray[sourceIndex];
                        byte dst = (byte) ((0xff & src) >>> shiftMod);
                        if (sourceIndex - 1 >= 0) {
                            dst |= byteArray[sourceIndex - 1] << (8 - shiftMod) & carryMask;
                        }
                        byteArray[i] = dst;
                    }
                }
            } else {
                for (int i = 0; i < byteArray.length; i++) {
                    sourceIndex = i + offsetBytes;
                    if (sourceIndex >= byteArray.length) {
                        byteArray[i] = 0;
                    } else {
                        byte src = byteArray[sourceIndex];
                        byte dst = (byte) ((0xff & src) >>> shiftMod);
                        if (sourceIndex + 1 < byteArray.length) {
                            dst |= byteArray[sourceIndex + 1] << (8 - shiftMod) & carryMask;
                        }
                        byteArray[i] = dst;
                    }
                }
            }
            return byteArray;
        }

        /**
         * See https://codahale.com/a-lesson-in-timing-attacks/
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array        to check for equals
         * @param anotherArray to check against array
         * @return if both arrays have the same length and same length for every index
         */
        static boolean constantTimeEquals(byte[] array, byte[] anotherArray) {
            if (anotherArray == null || array.length != anotherArray.length) return false;

            int result = 0;
            for (int i = 0; i < array.length; i++) {
                result |= array[i] ^ anotherArray[i];
            }
            return result == 0;
        }

        /**
         * Calculates the entropy factor of a byte array.
         * <p>
         * This implementation will not create a copy of the internal array and will only internally initialize
         * a int array with 256 elements as temporary buffer.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array to calculate the entropy from
         * @return entropy factor, higher means higher entropy
         */
        static double entropy(byte[] array) {
            final int[] buffer = new int[256];
            Arrays.fill(buffer, -1);

            for (byte element : array) {
                int unsigned = 0xff & element;
                if (buffer[unsigned] == -1) {
                    buffer[unsigned] = 0;
                }
                buffer[unsigned]++;
            }

            double entropy = 0;
            for (int count : buffer) {
                if (count == -1) continue;
                double prob = (double) count / array.length;
                entropy -= prob * (Math.log(prob) / Math.log(2));
            }
            return entropy;
        }
    }

    /**
     * Util method related converting byte arrays to other types.
     */
    static final class Converter {
        private Converter() {
        }

        /**
         * Copies a collection of {@code Byte} instances into a new array of
         * primitive {@code byte} values.
         * <p>
         * <p>Elements are copied from the argument collection as if by {@code
         * collection.toArray()}.  Calling this method is as thread-safe as calling
         * that method.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param collection a collection of {@code Byte} objects
         * @return an array containing the same values as {@code collection}, in the
         * same order, converted to primitives
         * @throws NullPointerException if {@code collection} or any of its elements
         *                              is null
         */
        static byte[] toArray(Collection<java.lang.Byte> collection) {
            final int len = collection.size();
            final byte[] array = new byte[len];
            int i = 0;
            for (java.lang.Byte b : collection) {
                array[i] = b;
                i++;
            }
            return array;
        }

        /**
         * Converts this primitive array to an boxed object array.
         * Will create a new array and not reuse the array reference.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array to convert
         * @return new array
         */
        static java.lang.Byte[] toBoxedArray(byte[] array) {
            java.lang.Byte[] objectArray = new java.lang.Byte[array.length];
            for (int i = 0; i < array.length; i++) {
                objectArray[i] = array[i];
            }
            return objectArray;
        }

        /**
         * Converts given array to list of boxed bytes. Will create a new list
         * and not reuse the array reference.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param array to convert
         * @return list with same length and content as array
         */
        static List<java.lang.Byte> toList(byte[] array) {
            List<java.lang.Byte> list = new ArrayList<>(array.length);
            for (byte b : array) {
                list.add(b);
            }
            return list;
        }

        /**
         * Converts this object array to an primitives type array.
         * Will create a new array and not reuse the array reference.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param objectArray to convert
         * @return new array
         */
        static byte[] toPrimitiveArray(java.lang.Byte[] objectArray) {
            byte[] primitivesArray = new byte[objectArray.length];
            for (int i = 0; i < objectArray.length; i++) {
                primitivesArray[i] = objectArray[i];
            }
            return primitivesArray;
        }

        /**
         * Creates a byte array from given short array.
         * The resulting byte array will have length shortArray * 2.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param shortArray to convert
         * @return resulting byte array
         */
        static byte[] toByteArray(short[] shortArray) {
            byte[] primitivesArray = new byte[shortArray.length * 2];
            ByteBuffer buffer = ByteBuffer.allocate(2);
            for (int i = 0; i < shortArray.length; i++) {
                buffer.clear();
                byte[] shortBytes = buffer.putShort(shortArray[i]).array();
                System.arraycopy(shortBytes, 0, primitivesArray, (i * 2), shortBytes.length);
            }
            return primitivesArray;
        }

        /**
         * Creates a byte array from given int array.
         * The resulting byte array will have length intArray * 4.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param intArray to convert
         * @return resulting byte array
         */
        static byte[] toByteArray(int[] intArray) {
            byte[] primitivesArray = new byte[intArray.length * 4];
            ByteBuffer buffer = ByteBuffer.allocate(4);
            for (int i = 0; i < intArray.length; i++) {
                buffer.clear();
                byte[] intBytes = buffer.putInt(intArray[i]).array();
                System.arraycopy(intBytes, 0, primitivesArray, (i * 4), intBytes.length);
            }
            return primitivesArray;
        }

        /**
         * Creates a byte array from given float array.
         * The resulting byte array will have length floatArray * 4.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param floatArray to convert
         * @return resulting byte array
         */
        static byte[] toByteArray(float[] floatArray) {
            byte[] primitivesArray = new byte[floatArray.length * 4];
            ByteBuffer buffer = ByteBuffer.allocate(4);
            for (int i = 0; i < floatArray.length; i++) {
                buffer.clear();
                byte[] floatBytes = buffer.putFloat(floatArray[i]).array();
                System.arraycopy(floatBytes, 0, primitivesArray, (i * 4), floatBytes.length);
            }
            return primitivesArray;
        }

        /**
         * Creates a byte array from given long array.
         * The resulting byte array will have length longArray * 8
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param longArray to convert
         * @return resulting byte array
         */
        static byte[] toByteArray(long[] longArray) {
            byte[] primitivesArray = new byte[longArray.length * 8];
            ByteBuffer buffer = ByteBuffer.allocate(8);
            for (int i = 0; i < longArray.length; i++) {
                buffer.clear();
                byte[] longBytes = buffer.putLong(longArray[i]).array();
                System.arraycopy(longBytes, 0, primitivesArray, (i * 8), longBytes.length);
            }
            return primitivesArray;
        }

        /**
         * Creates a byte array from given double array.
         * The resulting byte array will have length doubleArray * 8.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param doubleArray to convert
         * @return resulting byte array
         */
        static byte[] toByteArray(double[] doubleArray) {
            byte[] primitivesArray = new byte[doubleArray.length * 8];
            ByteBuffer buffer = ByteBuffer.allocate(8);
            for (int i = 0; i < doubleArray.length; i++) {
                buffer.clear();
                byte[] doubleBytes = buffer.putDouble(doubleArray[i]).array();
                System.arraycopy(doubleBytes, 0, primitivesArray, (i * 8), doubleBytes.length);
            }
            return primitivesArray;
        }

        /**
         * Converts a char array to a byte array with given charset and range
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param charArray to get the byte array from
         * @param charset   charset to be used to decode the char array
         * @param offset    to start reading the char array from (must be smaller than length and gt 0)
         * @param length    from offset (must be between 0 and charArray.length)
         * @return byte array of encoded chars
         */
        static byte[] charToByteArray(char[] charArray, Charset charset, int offset, int length) {
            if (offset < 0 || offset > charArray.length)
                throw new IllegalArgumentException("offset must be gt 0 and smaller than array length");
            if (length < 0 || length > charArray.length)
                throw new IllegalArgumentException("length must be at least 1 and less than array length");
            if (offset + length > charArray.length)
                throw new IllegalArgumentException("length + offset must be smaller than array length");

            if (length == 0) return new byte[0];

            CharBuffer charBuffer = CharBuffer.wrap(charArray);

            if (offset != 0 || length != charBuffer.remaining()) {
                charBuffer = charBuffer.subSequence(offset, offset + length);
            }

            ByteBuffer bb = charset.encode(charBuffer);
            if (bb.capacity() != bb.limit()) {
                byte[] bytes = new byte[bb.remaining()];
                bb.get(bytes);
                return bytes;
            }
            return bb.array();
        }

        /**
         * Convert given byte array in given encoding to char array
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param bytes     as data source
         * @param charset   of the byte array
         * @param byteOrder the order of the bytes array
         * @return char array
         */
        static char[] byteToCharArray(byte[] bytes, Charset charset, ByteOrder byteOrder) {
            Objects.requireNonNull(bytes, "bytes must not be null");
            Objects.requireNonNull(charset, "charset must not be null");

            try {
                CharBuffer charBuffer = charset.newDecoder().decode(ByteBuffer.wrap(bytes).order(byteOrder));
                if (charBuffer.capacity() != charBuffer.limit()) {
                    char[] compacted = new char[charBuffer.remaining()];
                    charBuffer.get(compacted);
                    return compacted;
                }
                return charBuffer.array();
            } catch (CharacterCodingException e) {
                throw new IllegalStateException(e);
            }
        }

        /**
         * Converts the byte array to an int array. This will spread 4 bytes into a single int:
         *
         * <pre>
         *     [b1, b2, b3, b4] = [int1]
         * </pre>
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param bytes     to convert to int array, must be % 4 == 0 to work correctly
         * @param byteOrder of the byte array
         * @return int array
         */
        static int[] toIntArray(byte[] bytes, ByteOrder byteOrder) {
            IntBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder).asIntBuffer();
            int[] array = new int[buffer.remaining()];
            buffer.get(array);
            return array;
        }

        /**
         * Converts the byte array to an long array. This will spread 8 bytes into a single long:
         *
         * <pre>
         *     [b1, b2, b3, b4, b5, b6, b7, b8] = [long1]
         * </pre>
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param bytes     to convert to long array, must be % 8 == 0 to work correctly
         * @param byteOrder of the byte array
         * @return long array
         */
        static long[] toLongArray(byte[] bytes, ByteOrder byteOrder) {
            LongBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder).asLongBuffer();
            long[] array = new long[buffer.remaining()];
            buffer.get(array);
            return array;
        }

        /**
         * Converts the byte array to an float array. This will spread 4 bytes into a single float:
         *
         * <pre>
         *     [b1, b2, b3, b4] = [float1]
         * </pre>
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param bytes     to convert to float array, must be % 4 == 0 to work correctly
         * @param byteOrder of the byte array
         * @return float array
         */
        static float[] toFloatArray(byte[] bytes, ByteOrder byteOrder) {
            FloatBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder).asFloatBuffer();
            float[] array = new float[buffer.remaining()];
            buffer.get(array);
            return array;
        }

        /**
         * Converts the byte array to an double array. This will spread 8 bytes into a single double:
         *
         * <pre>
         *     [b1, b2, b3, b4, b5, b6, b7, b8] = [double1]
         * </pre>
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param bytes     to convert to double array, must be % 8 == 0 to work correctly
         * @param byteOrder of the byte array
         * @return double array
         */
        static double[] toDoubleArray(byte[] bytes, ByteOrder byteOrder) {
            DoubleBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder).asDoubleBuffer();
            double[] array = new double[buffer.remaining()];
            buffer.get(array);
            return array;
        }

        /**
         * Converts the byte array to a short array. This will spread 2 bytes into a single short:
         *
         * <pre>
         *     [b1, b2] = [short1]
         * </pre>
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(n)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param bytes     to convert to short array, must be % 2 == 0 to work correctly
         * @param byteOrder of the byte array
         * @return short array
         */
        static short[] toShortArray(byte[] bytes, ByteOrder byteOrder) {
            ShortBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder).asShortBuffer();
            short[] array = new short[buffer.remaining()];
            buffer.get(array);
            return array;
        }

        /**
         * Convert UUID to a newly generated 16 byte long array representation. Puts the 8 byte most significant bits and
         * 8 byte least significant bits into an byte array.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(1)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param uuid to convert to array
         * @return buffer containing the 16 bytes
         */
        static ByteBuffer toBytesFromUUID(UUID uuid) {
            ByteBuffer bb = ByteBuffer.allocate(16);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());
            return bb;
        }
    }

    /**
     * Util method related to Object class methods.
     */
    static final class Obj {
        private Obj() {
        }

        /**
         * Equals method comparing 2 byte arrays.
         * This utilizes a quick return of the array differs on any given property so not suitable
         * for security relevant checks. See  {@link Util.Byte#constantTimeEquals(byte[], byte[])}
         * for that.
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param obj          subject a
         * @param anotherArray subject b to compare to a
         * @return if a.len == b.len and for every 0..len a[i] == b[i]
         */
        static boolean equals(byte[] obj, java.lang.Byte[] anotherArray) {
            if (anotherArray == null) return false;
            if (obj.length != anotherArray.length) return false;
            for (int i = 0; i < obj.length; i++) {
                if (anotherArray[i] == null || obj[i] != anotherArray[i]) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Hashcode implementation for a byte array and given byte order
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(n)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param byteArray to calculate hashCode of
         * @param byteOrder to calculate hashCode of
         * @return hashCode
         */
        static int hashCode(byte[] byteArray, ByteOrder byteOrder) {
            int result = Arrays.hashCode(byteArray);
            result = 31 * result + (byteOrder != null ? byteOrder.hashCode() : 0);
            return result;
        }

        /**
         * Shows the length and a preview of max 8 bytes of the given byte
         *
         * <p>
         * <strong>Analysis</strong>
         * <ul>
         * <li>Time Complexity: <code>O(1)</code></li>
         * <li>Space Complexity: <code>O(1)</code></li>
         * <li>Alters Parameters: <code>false</code></li>
         * </ul>
         * </p>
         *
         * @param bytes to convert to string
         * @return string representation
         */
        static String toString(Bytes bytes) {
            String preview;
            if (bytes.isEmpty()) {
                preview = "";
            } else if (bytes.length() > 8) {
                preview = "(0x" + bytes.copy(0, 4).encodeHex() + "..." + bytes.copy(bytes.length() - 4, 4).encodeHex() + ")";
            } else {
                preview = "(0x" + bytes.encodeHex() + ")";
            }

            return bytes.length() + " " + (bytes.length() == 1 ? "byte" : "bytes") + " " + preview;
        }
    }

    /**
     * Util method related check and validate byte arrays.
     */
    static final class Validation {
        private Validation() {
        }

        /**
         * Check if a length of an primitive (e.g. int = 4 byte) fits in given length from given start index.
         * Throws exception with descriptive exception message.
         *
         * @param length          of the whole array
         * @param index           to start from array length
         * @param primitiveLength length of the primitive type to check
         * @param type            for easier debugging the human readable type of the checked primitive
         *                        to put in exception message
         * @throws IndexOutOfBoundsException if index + primitiveLength > length
         */
        static void checkIndexBounds(int length, int index, int primitiveLength, String type) {
            if (index < 0 || index + primitiveLength > length) {
                throw new IndexOutOfBoundsException("cannot get " + type + " from index out of bounds: " + index);
            }
        }

        /**
         * Check if given length is an expected length.
         * Throws exception with descriptive exception message.
         *
         * @param length         of the whole array
         * @param expectedLength how length is expected
         * @param type           for easier debugging the human readable type of the checked primitive
         *                       to put in exception message
         * @throws IllegalArgumentException if length != expectedLength
         */
        static void checkExactLength(int length, int expectedLength, String type) {
            if (length != expectedLength) {
                throw new IllegalArgumentException("cannot convert to " + type + " if length != " + expectedLength + " bytes (was " + length + ")");
            }
        }

        /**
         * Checks if given length is divisible by mod factor (with zero rest).
         * This can be used to check of a byte array can be converted to an e.g. int array which is
         * multiples of 4.
         *
         * @param length       of the byte array
         * @param modFactor    to divide the length
         * @param errorSubject human readable message of the exact error subject
         * @throws IllegalArgumentException if length % modFactor != 0
         */
        static void checkModLength(int length, int modFactor, String errorSubject) {
            if (length % modFactor != 0) {
                throw new IllegalArgumentException("Illegal length for " + errorSubject + ". Byte array length must be multiple of " + modFactor + ", length was " + length);
            }
        }

        /**
         * Check if the file exists and is a file.
         *
         * @param file to check
         * @throws IllegalArgumentException if either file is null, does not exists or is not a file
         */
        private static void checkFileExists(java.io.File file) {
            if (file == null || !file.exists() || !file.isFile()) {
                throw new IllegalArgumentException("file must not be null, has to exist and must be a file (not a directory) " + file);
            }
        }
    }

    /**
     * Util method related file operations.
     */
    static final class File {
        private static final int BUF_SIZE = 0x1000; // 4K

        private File() {
        }

        /**
         * Read bytes, buffered, from given input stream. Pass -1 to read the whole stream or limit with length
         * parameter.
         *
         * @param inputStream     to read from
         * @param maxLengthToRead how many bytes to read from input stream; pass -1 to read whole stream
         * @return all bytes from the stream (possibly limited by maxLengthToRead); output length is never longer than stream size
         */
        static byte[] readFromStream(InputStream inputStream, final int maxLengthToRead) {
            final boolean readWholeStream = maxLengthToRead == -1;
            int remaining = maxLengthToRead;
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream(readWholeStream ? 32 : maxLengthToRead);
                byte[] buf = new byte[0];
                while (readWholeStream || remaining > 0) {
                    int bufSize = Math.min(BUF_SIZE, readWholeStream ? BUF_SIZE : remaining);
                    if (buf.length != bufSize) {
                        buf = new byte[bufSize];
                    }
                    int r = inputStream.read(buf);
                    if (r == -1) {
                        break;
                    }
                    remaining -= r;
                    out.write(buf, 0, r);
                }
                return out.toByteArray();
            } catch (Exception e) {
                throw new IllegalStateException("could not read from input stream", e);
            }
        }

        /**
         * Read all bytes until length from given byte array.
         *
         * @param dataInput to read from
         * @return all bytes from the dataInput
         */
        static byte[] readFromDataInput(DataInput dataInput, int length) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(length);
            try {
                byte[] buf;
                int remaining = length;
                for (int i = 0; i < length; i++) {
                    buf = new byte[Math.min(remaining, BUF_SIZE)];
                    dataInput.readFully(buf);
                    out.write(buf);
                    remaining -= buf.length;
                }
                return out.toByteArray();
            } catch (Exception e) {
                throw new IllegalStateException("could not read from data input", e);
            }
        }

        /**
         * Reads all bytes from a file
         *
         * @param file the file to read
         * @return byte content
         */
        static byte[] readFromFile(java.io.File file) {
            Validation.checkFileExists(file);

            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new IllegalStateException("could not read from file", e);
            }
        }

        /**
         * Reads bytes from file with given offset and max length
         *
         * @param file   to read bytes from
         * @param offset to read
         * @param length from offset
         * @return byte array with length length
         */
        static byte[] readFromFile(java.io.File file, int offset, int length) {
            Validation.checkFileExists(file);
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(offset);
                return readFromDataInput(raf, length);
            } catch (Exception e) {
                throw new IllegalStateException("could not read from random access file", e);
            }
        }

    }

    private Util() {
    }

    /**
     * A simple iterator for the bytes class, which does not support remove
     */
    static final class BytesIterator implements Iterator<java.lang.Byte> {
        private final byte[] array;
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int cursor = 0;

        BytesIterator(byte[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return cursor != array.length;
        }

        @Override
        public java.lang.Byte next() {
            try {
                int i = cursor;
                java.lang.Byte next = array[i];
                cursor = i + 1;
                return next;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("The Bytes iterator does not support removing");
        }
    }
}
