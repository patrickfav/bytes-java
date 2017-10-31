package at.favre.lib.primitives;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.*;

public final class Bytes implements Comparable<Bytes> {

    /* FACTORY ***************************************************************************************************/

    public static Bytes wrap(byte[] array) {
        Objects.requireNonNull(array, "passed array must not be null");
        return new Bytes(array);
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

    public static Bytes from(long integer8byte) {
        return wrap(ByteBuffer.allocate(8).putLong(integer8byte).array());
    }

    public static Bytes parseHex(String hexString) {
        return parse(hexString, new ByteToTextEncoding.Hex());
    }

    public static Bytes parse(String encoded, ByteToTextEncoding.Decoder decoder) {
        Objects.requireNonNull(encoded, "encoded data must not be null");
        Objects.requireNonNull(decoder, "passed decoder instance must no be null");

        return wrap(decoder.decode(encoded));
    }

    public static Bytes random(int length) {
        return random(length, new SecureRandom());
    }

    public static Bytes random(int length, SecureRandom secureRandom) {
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

    public Bytes concat(byte[] secondArray) {
        return wrap(Util.concat(byteArray, secondArray));
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

    public Bytes copy() {
        return wrap(Arrays.copyOf(byteArray, byteArray.length));
    }

    public Bytes duplicate() {
        return wrap(array());
    }

    public Bytes transform(BytesTransformer transformer) {
        return transformer.transform(this);
    }

    /* ATTRIBUTES ************************************************************************************************/

    public int length() {
        return byteArray.length;
    }

    public int lengthBit() {
        return length() * 8;
    }

    public boolean isEmpty() {
        return byteArray.length == 0;
    }

    public int indexOf(byte target) {
        return Util.indexOf(byteArray, target, 0, byteArray.length);
    }

    /* CONVERTER ************************************************************************************************/

    public ByteBuffer buffer() {
        return ByteBuffer.wrap(byteArray);
    }

    public byte[] array() {
        return byteArray;
    }

    public String encodeHex() {
        return encodeHex(true);
    }

    public String encodeHex(boolean lowerCase) {
        return new ByteToTextEncoding.Hex(lowerCase).encode(array());
    }

    public String encode(ByteToTextEncoding.Encoder encoder) {
        return encoder.encode(array());
    }

    public List<Byte> toList() {
        return Util.toList(byteArray);
    }

    public int toInt() {
        if (byteArray.length > 4) {
            throw new UnsupportedOperationException("cannot convert to int if length > 4 byte");
        }
        return Util.byteToInt(byteArray);
    }

    public long toLong() {
        if (byteArray.length > 8) {
            throw new UnsupportedOperationException("cannot convert to long if length > 8 byte");
        }
        return Util.byteToLong(byteArray);
    }

    public void wipe() {
        Arrays.fill(byteArray, (byte) 0);
    }

    public void secureWipe() {
        secureWipe(new SecureRandom());
    }

    public void secureWipe(SecureRandom random) {
        random.nextBytes(byteArray);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bytes bytes = (Bytes) o;

        return Arrays.equals(byteArray, bytes.byteArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteArray);
    }

    @Override
    public int compareTo(Bytes o) {
        return buffer().compareTo(o.buffer());
    }

    static class Util {
        static int byteToInt(byte[] bytes) {
            return (int) byteToLong(bytes);
        }

        static long byteToLong(byte[] bytes) {
            int returnVal = 0;

            for (int i = 0; i < bytes.length; i++) {
                returnVal += ((bytes[i] & 0xff)) << ((bytes.length - 1 - i) * 8);
            }
            return returnVal;
        }

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
