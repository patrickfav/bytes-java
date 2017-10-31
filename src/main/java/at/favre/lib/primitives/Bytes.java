package at.favre.lib.primitives;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.*;

public final class Bytes implements Comparable<Bytes> {

    /* FACTORY ***************************************************************************************************/

    public static Bytes create(int length) {
        return create(length, (byte) 0);
    }

    public static Bytes create(int length, byte defaultValue) {
        byte[] array = new byte[length];
        if (defaultValue != 0) {
            Arrays.fill(array, defaultValue);
        }
        return wrap(array);
    }

    public static Bytes wrap(byte[] array) {
        Objects.requireNonNull(array, "passed array must not be null");
        return new Bytes(array);
    }

    public static Bytes wrap(byte[] array, int offset, int length) {
        Objects.requireNonNull(array, "passed array must not be null");
        byte[] part = new byte[length];
        System.arraycopy(array, offset, part, 0, length);
        return new Bytes(part);
    }

    public static Bytes wrap(byte[]... moreArrays) {
        return wrap(Util.concat(moreArrays));
    }

    public static Bytes from(Collection<Byte> bytesCollection) {
        return wrap(Util.toArray(bytesCollection));
    }

    public static Bytes from(byte b) {
        return wrap(new byte[]{b});
    }

    public static Bytes from(byte... manyBytes) {
        Objects.requireNonNull(manyBytes, "must at least pass a single byte");
        return wrap(manyBytes);
    }

    public static Bytes from(char char2Byte) {
        return wrap(ByteBuffer.allocate(2).putChar(char2Byte).array());
    }

    public static Bytes from(short short2Byte) {
        return wrap(ByteBuffer.allocate(2).putShort(short2Byte).array());
    }

    public static Bytes from(int integer4byte) {
        return wrap(ByteBuffer.allocate(4).putInt(integer4byte).array());
    }

    public static Bytes from(long long8byte) {
        return wrap(ByteBuffer.allocate(8).putLong(long8byte).array());
    }

    public static Bytes parseOctal(String octalString) {
        return parse(octalString, new ByteToTextEncoding.BaseRadixEncoder(8));
    }

    public static Bytes parseDec(String decString) {
        return parse(decString, new ByteToTextEncoding.BaseRadixEncoder(10));
    }

    public static Bytes parseHex(String hexString) {
        return parse(hexString, new ByteToTextEncoding.Hex());
    }

    public static Bytes parseBase36(String base36String) {
        return parse(base36String, new ByteToTextEncoding.BaseRadixEncoder(36));
    }

    public static Bytes parseBase64(String base64String) {
        return parse(base64String, new ByteToTextEncoding.Base64Encoding());
    }

    public static Bytes parse(String encoded, ByteToTextEncoding.Decoder decoder) {
        Objects.requireNonNull(encoded, "encoded data must not be null");
        Objects.requireNonNull(decoder, "passed decoder instance must no be null");

        return wrap(decoder.decode(encoded));
    }

    public static Bytes random(int length) {
        return random(length, new SecureRandom());
    }

    public static Bytes unsecureRandom(int length) {
        return random(length, new Random());
    }

    public static Bytes random(int length, Random secureRandom) {
        byte[] array = new byte[length];
        secureRandom.nextBytes(array);
        return wrap(array);
    }

    /* OBJECT ****************************************************************************************************/

    private final byte[] byteArray;

    private Bytes(byte[] array) {
        this.byteArray = array;
    }

    /* TRANSFORMER **********************************************************************************************/

    public Bytes concat(Bytes bytes) {
        return concat(bytes.array());
    }

    public Bytes concat(byte b) {
        return concat(new byte[]{b});
    }

    public Bytes concat(byte[] secondArray) {
        return transform(new BytesTransformer.ConcatTransformer(secondArray));
    }

    public Bytes xor(Bytes bytes) {
        return xor(bytes.array());
    }

    public Bytes xor(byte[] secondArray) {
        return transform(new BytesTransformer.BitWiseOperatorTransformer(secondArray, BytesTransformer.BitWiseOperatorTransformer.Mode.XOR));
    }

    public Bytes and(Bytes bytes) {
        return and(bytes.array());
    }

    public Bytes and(byte[] secondArray) {
        return transform(new BytesTransformer.BitWiseOperatorTransformer(secondArray, BytesTransformer.BitWiseOperatorTransformer.Mode.AND));
    }

    public Bytes or(Bytes bytes) {
        return and(bytes.array());
    }

    public Bytes or(byte[] secondArray) {
        return transform(new BytesTransformer.BitWiseOperatorTransformer(secondArray, BytesTransformer.BitWiseOperatorTransformer.Mode.OR));
    }

    public Bytes negate() {
        return transform(new BytesTransformer.NegateTransformer());
    }

    public Bytes leftShift(int shiftCount) {
        return transform(new BytesTransformer.ShiftTransformer(shiftCount, BytesTransformer.ShiftTransformer.Type.LEFT_SHIFT));
    }

    public Bytes rightShift(int shiftCount) {
        return transform(new BytesTransformer.ShiftTransformer(shiftCount, BytesTransformer.ShiftTransformer.Type.RIGHT_SHIFT));
    }

    public Bytes copy() {
        return wrap(Arrays.copyOf(array(), length()));
    }

    public Bytes copy(int offset, int length) {
        byte[] copy = new byte[length];
        System.arraycopy(array(), offset, copy, 0, length);
        return wrap(copy);
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain {@code (byte)0}.
     *
     * @param newByteLength the length of the copy to be returned
     * @return a copy with the desired size or "this" instance if newByteLength == current length
     */
    public Bytes resize(int newByteLength) {
        if (length() == newByteLength) {
            return this;
        }
        return wrap(Arrays.copyOf(array(), newByteLength));
    }

    public Bytes duplicate() {
        return wrap(array());
    }

    public Bytes transform(BytesTransformer transformer) {
        return transformer.transform(this);
    }

    /* ATTRIBUTES ************************************************************************************************/

    public int length() {
        return array().length;
    }

    public int lengthBit() {
        return length() * 8;
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public int indexOf(byte target) {
        return Util.indexOf(array(), target, 0, length());
    }

    public int indexOf(byte[] subArray) {
        return Util.indexOf(array(), subArray);
    }

    public int lastIndexOf(byte target) {
        return Util.lastIndexOf(array(), target, 0, length());
    }

    /* GETTER ************************************************************************************************/

    public ByteBuffer buffer() {
        return ByteBuffer.wrap(array());
    }

    public BigInteger bigInteger() {
        return new BigInteger(array());
    }

    public byte[] array() {
        return byteArray;
    }

    /* ENCODER ************************************************************************************************/

    public String encodeBinary() {
        return new ByteToTextEncoding.BaseRadixEncoder(2).encode(array());
    }

    public String encodeOctal() {
        return new ByteToTextEncoding.BaseRadixEncoder(8).encode(array());
    }

    public String encodeDec() {
        return new ByteToTextEncoding.BaseRadixEncoder(10).encode(array());
    }

    public String encodeHex() {
        return encodeHex(false);
    }

    public String encodeHex(boolean upperCase) {
        return new ByteToTextEncoding.Hex(upperCase).encode(array());
    }

    public String encodeBase36() {
        return new ByteToTextEncoding.BaseRadixEncoder(36).encode(array());
    }

    public String encodeBase64() {
        return new ByteToTextEncoding.Base64Encoding().encode(array());
    }

    public String encode(ByteToTextEncoding.Encoder encoder) {
        return encoder.encode(array());
    }

    /* CONVERTER ************************************************************************************************/

    public List<Byte> toList() {
        return Util.toList(array());
    }

    public char toChar() {
        if (length() > 2) {
            throw new UnsupportedOperationException("cannot convert to char if length > 2 byte");
        }
        return buffer().getChar();
    }

    public short toShort() {
        if (length() > 2) {
            throw new UnsupportedOperationException("cannot convert to short if length > 2 byte");
        }
        return buffer().getShort();
    }

    public int toInt() {
        if (length() > 4) {
            throw new UnsupportedOperationException("cannot convert to int if length > 4 byte");
        }
        return buffer().getInt();
    }

    public long toLong() {
        if (length() > 8) {
            throw new UnsupportedOperationException("cannot convert to long if length > 8 byte");
        }
        return buffer().getLong();
    }

    /* MUTATOR ************************************************************************************************/

    public void wipe() {
        Arrays.fill(array(), (byte) 0);
    }

    public void secureWipe() {
        secureWipe(new SecureRandom());
    }

    public void secureWipe(SecureRandom random) {
        random.nextBytes(array());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bytes bytes = (Bytes) o;

        return Arrays.equals(array(), bytes.array());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array());
    }

    @Override
    public int compareTo(Bytes o) {
        return buffer().compareTo(o.buffer());
    }

    static class Util {
        /**
         * Returns the values from each provided byteArray combined into a single byteArray.
         * For example, {@code concat(new byte[] {a, b}, new byte[] {}, new
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

        static List<Byte> toList(byte[] arr) {
            List<Byte> list = new ArrayList<>();
            for (byte b : arr) {
                list.add(b);
            }
            return list;
        }
    }
}
