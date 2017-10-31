package at.favre.lib.primitives.bytes;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.*;

public class Bytes implements Comparable<Bytes> {

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

    public static Bytes from(ByteBuffer buffer) {
        return wrap(buffer.array());
    }

    public static Bytes parseOctal(String octalString) {
        return parse(octalString, new ByteToTextEncoding.BaseRadix(8));
    }

    public static Bytes parseDec(String decString) {
        return parse(decString, new ByteToTextEncoding.BaseRadix(10));
    }

    public static Bytes parseHex(String hexString) {
        return parse(hexString, new ByteToTextEncoding.Hex());
    }

    public static Bytes parseBase36(String base36String) {
        return parse(base36String, new ByteToTextEncoding.BaseRadix(36));
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

    public static Bytes nonSecureRandom(int length) {
        return random(length, new Random());
    }

    public static Bytes random(int length, Random secureRandom) {
        byte[] array = new byte[length];
        secureRandom.nextBytes(array);
        return wrap(array);
    }

    /* OBJECT ****************************************************************************************************/

    private final byte[] byteArray;
    private final ByteOrder byteOrder;
    private final boolean mutable;

    Bytes(byte[] array) {
        this(array, ByteOrder.BIG_ENDIAN, false);
    }

    Bytes(byte[] byteArray, ByteOrder byteOrder) {
        this(byteArray, byteOrder, false);
    }

    Bytes(byte[] byteArray, ByteOrder byteOrder, boolean mutable) {
        this.byteArray = byteArray;
        this.byteOrder = byteOrder;
        this.mutable = mutable;
    }

    /* TRANSFORMER **********************************************************************************************/

    public Bytes append(Bytes bytes) {
        return append(bytes.array());
    }

    public Bytes append(byte b) {
        return append(new byte[]{b});
    }

    public Bytes append(short short2Bytes) {
        return append(Bytes.from(short2Bytes));
    }

    public Bytes append(int integer4Bytes) {
        return append(Bytes.from(integer4Bytes));
    }

    public Bytes append(long long8Bytes) {
        return append(Bytes.from(long8Bytes));
    }

    public Bytes append(byte[] secondArray) {
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

    public Bytes reverse() {
        return transform(new BytesTransformer.ReverseTransformer());
    }

    public Bytes sort(Comparator<Byte> comparator) {
        return transform(new BytesTransformer.SortTransformer(comparator));
    }

    public Bytes sort() {
        return transform(new BytesTransformer.SortTransformer());
    }

    public Bytes shuffle(Random random) {
        return transform(new BytesTransformer.ShuffleTransformer(random));
    }

    public Bytes shuffle() {
        return transform(new BytesTransformer.ShuffleTransformer(new SecureRandom()));
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
        return transformer.transform(this, mutable);
    }

    public Bytes byteOrder(ByteOrder byteOrder) {
        if (byteOrder != this.byteOrder) {
            return new Bytes(array(), byteOrder);
        }
        return this;
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

    public ByteOrder byteOrder() {
        return byteOrder;
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
        return new ByteToTextEncoding.BaseRadix(2).encode(array());
    }

    public String encodeOctal() {
        return new ByteToTextEncoding.BaseRadix(8).encode(array());
    }

    public String encodeDec() {
        return new ByteToTextEncoding.BaseRadix(10).encode(array());
    }

    public String encodeHex() {
        return encodeHex(false);
    }

    public String encodeHex(boolean upperCase) {
        return new ByteToTextEncoding.Hex(upperCase).encode(array());
    }

    public String encodeBase36() {
        return new ByteToTextEncoding.BaseRadix(36).encode(array());
    }

    public String encodeBase64() {
        return new ByteToTextEncoding.Base64Encoding().encode(array());
    }

    public String encode(ByteToTextEncoding.Encoder encoder) {
        return encoder.encode(array());
    }

    /* CONVERTER ************************************************************************************************/

    public MutableBytes toMutable() {
        if (this instanceof MutableBytes) {
            return (MutableBytes) this;
        } else {
            return new MutableBytes(array(), byteOrder);
        }
    }

    public List<Byte> toList() {
        return Util.toList(array());
    }

    public Byte[] toObjectArray() {
        return Util.toObjectArray(array());
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

}
