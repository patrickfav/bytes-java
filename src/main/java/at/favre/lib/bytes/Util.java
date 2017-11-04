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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

/**
 * Common Util methods to convert or modify byte arrays
 */
final class Util {
    private Util() {
    }

    /**
     * Returns the values from each provided byteArray combined into a single byteArray.
     * For example, {@code append(new byte[] {a, b}, new byte[] {}, new
     * byte[] {c}} returns the byteArray {@code {a, b, c}}.
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
     * Returns the index of the first appearance of the value {@code target} in
     * {@code array}.
     *
     * @param array  an array of {@code byte} values, possibly empty
     * @param target a primitive {@code byte} value
     * @return the least index {@code i} for which {@code array[i] == target}, or
     * {@code -1} if no such index exists.
     */
    static int indexOf(byte[] array, byte target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the start position of the first occurrence of the specified {@code
     * target} within {@code array}, or {@code -1} if there is no such occurrence.
     * <p>
     * <p>More formally, returns the lowest index {@code i} such that {@code
     * java.util.Arrays.copyOfRange(array, i, i + target.length)} contains exactly
     * the same elements as {@code target}.
     *
     * @param array  the array to search for the sequence {@code target}
     * @param target the array to search for as a sub-sequence of {@code array}
     */
    public static int indexOf(byte[] array, byte[] target) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(target, "target must not be null");
        if (target.length == 0) {
            return 0;
        }

        outer:
        for (int i = 0; i < array.length - target.length + 1; i++) {
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
     * Copies a collection of {@code Byte} instances into a new array of
     * primitive {@code byte} values.
     * <p>
     * <p>Elements are copied from the argument collection as if by {@code
     * collection.toArray()}.  Calling this method is as thread-safe as calling
     * that method.
     *
     * @param collection a collection of {@code Byte} objects
     * @return an array containing the same values as {@code collection}, in the
     * same order, converted to primitives
     * @throws NullPointerException if {@code collection} or any of its elements
     *                              is null
     */
    static byte[] toArray(Collection<Byte> collection) {
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        byte[] array = new byte[len];
        for (int i = 0; i < len; i++) {
            array[i] = (Byte) boxedArray[i];
        }
        return array;
    }

    /**
     * Converts given array to list of boxed bytes. Will create a new list
     * and not reuse the array reference.
     *
     * @param array to convert
     * @return list with same length and content as array
     */
    static List<Byte> toList(byte[] array) {
        List<Byte> list = new ArrayList<>(array.length);
        for (byte b : array) {
            list.add(b);
        }
        return list;
    }

    /**
     * Converts this primitive array to an boxed object array.
     * Will create a new array and not reuse the array reference.
     *
     * @param array to convert
     * @return new array
     */
    static Byte[] toObjectArray(byte[] array) {
        Byte[] objectArray = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            objectArray[i] = array[i];
        }
        return objectArray;
    }

    /**
     * Converts this object array to an primitives type array.
     * Will create a new array and not reuse the array reference.
     *
     * @param objectArray to convert
     * @return new array
     */
    static byte[] toPrimitiveArray(Byte[] objectArray) {
        byte[] primitivesArray = new byte[objectArray.length];
        for (int i = 0; i < objectArray.length; i++) {
            primitivesArray[i] = objectArray[i];
        }
        return primitivesArray;
    }

    /**
     * Creates a byte array from given int array.
     * The resulting byte array will have length intArray * 4.
     *
     * @param intArray to convert
     * @return resulting byte array
     */
    static byte[] toByteArray(int[] intArray) {
        byte[] primitivesArray = new byte[intArray.length * 4];
        for (int i = 0; i < intArray.length; i++) {
            byte[] intBytes = ByteBuffer.allocate(4).putInt(intArray[i]).array();
            for (int j = 0; j < intBytes.length; j++) {
                primitivesArray[(i * 4) + j] = intBytes[j];
            }
        }
        return primitivesArray;
    }

    /**
     * Creates a byte array from given long array.
     * The resulting byte array will have length longArray * 8
     *
     * @param longArray to convert
     * @return resulting byte array
     */
    static byte[] toByteArray(long[] longArray) {
        byte[] primitivesArray = new byte[longArray.length * 8];
        for (int i = 0; i < longArray.length; i++) {
            byte[] longBytes = ByteBuffer.allocate(8).putLong(longArray[i]).array();
            for (int j = 0; j < longBytes.length; j++) {
                primitivesArray[(i * 8) + j] = longBytes[j];
            }
        }
        return primitivesArray;
    }

    /**
     * Simple Durstenfeld shuffle
     * <p>
     * See: https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
     *
     * @param array
     * @param random
     */
    static void shuffle(byte[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            byte a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    private static final int BUF_SIZE = 0x1000; // 4K

    /**
     * Read all bytes, buffered, from given input stream
     *
     * @param inputStream to read from
     * @return all bytes from the stream
     */
    static byte[] readFromStream(InputStream inputStream) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[BUF_SIZE];
            while (true) {
                int r = inputStream.read(buf);
                if (r == -1) {
                    break;
                }
                out.write(buf, 0, r);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("could not read from input stream", e);
        }
    }

    /**
     * Combines a single argument with a vararg to a single array
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
     * Reads all bytes from a file
     *
     * @param file the file to read
     * @return byte content
     */
    static byte[] readFromFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("file must not be null, has to exist and must be a file (not a directory) " + file);
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("could not read from file", e);
        }
    }

    /*
    =================================================================================================
     Copyright 2011 Twitter, Inc.
     -------------------------------------------------------------------------------------------------
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this work except in compliance with the License.
     You may obtain a copy of the License in the LICENSE file, or at:

      http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
     =================================================================================================
     */
    public static final class Entropy<T> {
        private final Map<T, Integer> map = new HashMap<>();
        private int total = 0;

        private double Log2(double n) {
            return Math.log(n) / Math.log(2);
        }

        public Entropy(Iterable<T> elements) {
            for (T element : elements) {
                if (!map.containsKey(element)) {
                    map.put(element, 0);
                }
                map.put(element, map.get(element) + 1);
                total++;
            }
        }

        public double entropy() {
            double entropy = 0;
            for (int count : map.values()) {
                double prob = (double) count / total;
                entropy -= prob * Log2(prob);
            }
            return entropy;
        }
    }
}
