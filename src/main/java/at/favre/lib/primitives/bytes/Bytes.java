package at.favre.lib.primitives.bytes;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * Bytes is wrapper class for an byte-array that allows a lot of convenience operations on it:
 * <p>
 * <ul>
 * <li>Creation from various source: arrays, primitives, parsed or random</li>
 * <li>Encoding in many formats: hex, base64, etc.</li>
 * <li>Helper functions like: indexOf, count, entropy</li>
 * <li>Transformations like: append, reverse, xor, and, resize, ...</li>
 * <li>Conversation to other types: primitives, List, object array, ByteBuffer, BigInteger, ...</li>
 * <li>Making it mutable</li>
 * </ul>
 * <p>
 * It supports byte ordering (little/big endianness).
 * <p>
 * This class is immutable as long as the internal array is not changed from outside (which can't be assured, when
 * using using <code>wrap()</code>). It is possible to create a mutable version (see {@link MutableBytes}).
 * <p>
 * <strong>Example:</strong>
 * <p>
 * <pre>
 *     Bytes b = Bytes.from(array);
 *     b.negate();
 *     System.out.println(b.encodeHex());
 * </pre>
 */
public class Bytes implements Comparable<Bytes> {

    /* FACTORY ***************************************************************************************************/

    /**
     * Creates a new instance with an empty array filled zeros.
     *
     * @param length of the internal array
     * @return new instance
     */
    public static Bytes create(int length) {
        return create(length, (byte) 0);
    }

    /**
     * Creates a new instance with an empty array filled with given defaultValue
     *
     * @param length       of the internal array
     * @param defaultValue to fill with
     * @return new instance
     */
    public static Bytes create(int length, byte defaultValue) {
        byte[] array = new byte[length];
        if (defaultValue != 0) {
            Arrays.fill(array, defaultValue);
        }
        return wrap(array);
    }

    /**
     * Creates a new instance with given byte array. This array will be used directly, no copying happens so mind
     * the possible side effects.
     *
     * @param array to use directly
     * @return new instance
     */
    public static Bytes wrap(byte[] array) {
        Objects.requireNonNull(array, "passed array must not be null");
        return new Bytes(array);
    }

    /**
     * Creates a new instance from a slice of given array
     *
     * @param array  to slice
     * @param offset stat position
     * @param length length
     * @return new instance
     */
    public static Bytes wrap(byte[] array, int offset, int length) {
        Objects.requireNonNull(array, "passed array must not be null");
        byte[] part = new byte[length];
        System.arraycopy(array, offset, part, 0, length);
        return new Bytes(part);
    }

    /**
     * Creates a new instance from given array of byte arrays
     *
     * @param moreArrays must not be null
     * @return new instance
     */
    public static Bytes wrap(byte[]... moreArrays) {
        return wrap(Util.concat(moreArrays));
    }

    /**
     * Creates a new instance from given collections. This will create a lot of auto-unboxing events,
     * so use with care with bigger lists.
     *
     * @param bytesCollection
     * @return new instance
     */
    public static Bytes from(Collection<Byte> bytesCollection) {
        return wrap(Util.toArray(bytesCollection));
    }

    /**
     * Creates a new single array element array instance from given byte
     *
     * @param singleByte
     * @return new instance
     */
    public static Bytes from(byte singleByte) {
        return wrap(new byte[]{singleByte});
    }

    /**
     * Creates a new instance from given collections of single bytes
     *
     * @param manyBytes must not be null
     * @return new instance
     */
    public static Bytes from(byte... manyBytes) {
        Objects.requireNonNull(manyBytes, "must at least pass a single byte");
        return wrap(manyBytes);
    }

    /**
     * Creates a new instance from given unsigned 2 byte char.
     *
     * @param char2Byte
     * @return new instance
     */
    public static Bytes from(char char2Byte) {
        return wrap(ByteBuffer.allocate(2).putChar(char2Byte).array());
    }

    /**
     * Creates a new instance from given 2 byte short.
     *
     * @param short2Byte
     * @return new instance
     */
    public static Bytes from(short short2Byte) {
        return wrap(ByteBuffer.allocate(2).putShort(short2Byte).array());
    }

    /**
     * Creates a new instance from given 4 byte integer.
     *
     * @param integer4byte
     * @return new instance
     */
    public static Bytes from(int integer4byte) {
        return wrap(ByteBuffer.allocate(4).putInt(integer4byte).array());
    }

    /**
     * Creates a new instance from given 8 byte long.
     *
     * @param long8byte
     * @return new instance
     */
    public static Bytes from(long long8byte) {
        return wrap(ByteBuffer.allocate(8).putLong(long8byte).array());
    }

    /**
     * Creates a new instance from given ByteBuffer.
     *
     * @param buffer to get the byte array from
     * @return new instance
     */
    public static Bytes from(ByteBuffer buffer) {
        return wrap(buffer.array());
    }

    /**
     * Creates a new instance from given utf-8 encoded string
     *
     * @param utf8String to get the internal byte array from
     * @return new instance
     */
    public static Bytes from(String utf8String) {
        return wrap(utf8String.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Parsing of octal encoded byte arrays.
     *
     * @param octalString the encoded string
     * @return decoded instance
     */
    public static Bytes parseOctal(String octalString) {
        return parse(octalString, new ByteToTextEncoding.BaseRadix(8));
    }

    /**
     * Parsing of decimal encoded byte arrays.
     *
     * @param decString the encoded string
     * @return decoded instance
     */
    public static Bytes parseDec(String decString) {
        return parse(decString, new ByteToTextEncoding.BaseRadix(10));
    }

    /**
     * Parsing of base16/HEX encoded byte arrays. Will accept upper- and lowercase variant and ignores
     * possible "0x" prefix.
     *
     * @param hexString the encoded string
     * @return decoded instance
     */
    public static Bytes parseHex(String hexString) {
        return parse(hexString, new ByteToTextEncoding.Hex());
    }

    /**
     * Parsing of base36 encoded byte arrays.
     *
     * @param base36String the encoded string
     * @return decoded instance
     */
    public static Bytes parseBase36(String base36String) {
        return parse(base36String, new ByteToTextEncoding.BaseRadix(36));
    }

    /**
     * Parsing of base64 encoded byte arrays.
     *
     * @param base64String the encoded string
     * @return decoded instance
     */
    public static Bytes parseBase64(String base64String) {
        return parse(base64String, new ByteToTextEncoding.Base64Encoding());
    }

    /**
     * Parsing of arbitrary encoded format
     *
     * @param encoded the encoded string
     * @param decoder the decoder used to decode the string
     * @return decoded instance
     */
    public static Bytes parse(String encoded, ByteToTextEncoding.Decoder decoder) {
        Objects.requireNonNull(encoded, "encoded data must not be null");
        Objects.requireNonNull(decoder, "passed decoder instance must no be null");

        return wrap(decoder.decode(encoded));
    }

    /**
     * A new instance with random bytes. Uses a cryptographically secure {@link SecureRandom} instance.
     *
     * @param length desired array length
     * @return random instance
     */
    public static Bytes random(int length) {
        return random(length, new SecureRandom());
    }

    /**
     * A new instance with random bytes. Uses a non-cryptographically secure {@link Random} instance.
     *
     * @param length desired array length
     * @return random instance
     */
    public static Bytes nonSecureRandom(int length) {
        return random(length, new Random());
    }

    /**
     * A new instance with random bytes.
     *
     * @param length desired array length
     * @param random to create the entropy for the random bytes
     * @return random instance
     */
    public static Bytes random(int length, Random random) {
        byte[] array = new byte[length];
        random.nextBytes(array);
        return wrap(array);
    }

    /* OBJECT ****************************************************************************************************/

    private final byte[] byteArray;
    private final ByteOrder byteOrder;
    private final boolean mutable;

    /**
     * Creates a new immutable instance with Big-Endian ordering
     *
     * @param byteArray internal byte array
     */
    Bytes(byte[] byteArray) {
        this(byteArray, ByteOrder.BIG_ENDIAN, false);
    }

    /**
     * Creates a new immutable instance
     *
     * @param byteArray internal byte array
     * @param byteOrder the internal byte order - this is used to interpret given array, not to change it
     */
    Bytes(byte[] byteArray, ByteOrder byteOrder) {
        this(byteArray, byteOrder, false);
    }

    /**
     * Creates a new instance
     *
     * @param byteArray internal byte array
     * @param byteOrder the internal byte order - this is used to interpret given array, not to change it
     * @param mutable   if the internal state can be changed
     */
    Bytes(byte[] byteArray, ByteOrder byteOrder, boolean mutable) {
        this.byteArray = byteArray;
        this.byteOrder = byteOrder;
        this.mutable = mutable;
    }

    /* TRANSFORMER **********************************************************************************************/

    /**
     * Creates a new instance with the current array appended with the provided data (ie. append at the end)
     *
     * @param bytes to append
     * @return appended instance
     */
    public Bytes append(Bytes bytes) {
        return append(bytes.array());
    }

    /**
     * Creates a new instance with the current array appended with the provided data (ie. append at the end)
     *
     * @param singleByte to append
     * @return appended instance
     */
    public Bytes append(byte singleByte) {
        return append(new byte[]{singleByte});
    }

    /**
     * Creates a new instance with the current array appended with the provided data (ie. append at the end)
     *
     * @param short2Bytes to append
     * @return appended instance
     */
    public Bytes append(short short2Bytes) {
        return append(Bytes.from(short2Bytes));
    }

    /**
     * Creates a new instance with the current array appended with the provided data (ie. append at the end)
     *
     * @param integer4Bytes to append
     * @return appended instance
     */
    public Bytes append(int integer4Bytes) {
        return append(Bytes.from(integer4Bytes));
    }

    /**
     * Creates a new instance with the current array appended with the provided data (ie. append at the end)
     *
     * @param long8Bytes to append
     * @return appended instance
     */
    public Bytes append(long long8Bytes) {
        return append(Bytes.from(long8Bytes));
    }

    /**
     * Creates a new instance with the current array appended with the provided data (ie. append at the end)
     *
     * @param secondArray to append
     * @return appended instance
     */
    public Bytes append(byte[] secondArray) {
        return transform(new BytesTransformer.ConcatTransformer(secondArray));
    }

    /**
     * Bitwise XOR operation on the whole internal byte array.
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param bytes must be of same length as this instance
     * @return xor'ed instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#XOR">Bitwise operators: XOR</a>
     */
    public Bytes xor(Bytes bytes) {
        return xor(bytes.array());
    }

    /**
     * Bitwise XOR operation on the whole internal byte array.
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param secondArray must be of same length as this instance
     * @return xor'ed instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#XOR">Bitwise operators: XOR</a>
     */
    public Bytes xor(byte[] secondArray) {
        return transform(new BytesTransformer.BitWiseOperatorTransformer(secondArray, BytesTransformer.BitWiseOperatorTransformer.Mode.XOR));
    }

    /**
     * Bitwise AND operation on the whole internal byte array.
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param bytes must be of same length as this instance
     * @return and'ed instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#AND">Bitwise operators: AND</a>
     */
    public Bytes and(Bytes bytes) {
        return and(bytes.array());
    }

    /**
     * Bitwise AND operation on the whole internal byte array.
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param secondArray must be of same length as this instance
     * @return and'ed instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#AND">Bitwise operators: AND</a>
     */
    public Bytes and(byte[] secondArray) {
        return transform(new BytesTransformer.BitWiseOperatorTransformer(secondArray, BytesTransformer.BitWiseOperatorTransformer.Mode.AND));
    }

    /**
     * Bitwise OR operation on the whole internal byte array.
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param bytes must be of same length as this instance
     * @return or'ed instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#OR">Bitwise operators: OR</a>
     */
    public Bytes or(Bytes bytes) {
        return and(bytes.array());
    }

    /**
     * Bitwise OR operation on the whole internal byte array.
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param secondArray must be of same length as this instance
     * @return or'ed instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#OR">Bitwise operators: OR</a>
     */
    public Bytes or(byte[] secondArray) {
        return transform(new BytesTransformer.BitWiseOperatorTransformer(secondArray, BytesTransformer.BitWiseOperatorTransformer.Mode.OR));
    }

    /**
     * Bitwise negate operation on the whole internal byte array.
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @return negated instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#NOT">Bitwise operators: NOT</a>
     */
    public Bytes negate() {
        return transform(new BytesTransformer.NegateTransformer());
    }

    /**
     * Bitwise left shifting of internal byte array.
     * <p>
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param shiftCount how many bits
     * @return shifted instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bit_shifts">Bit shifts</a>
     */
    public Bytes leftShift(int shiftCount) {
        return transform(new BytesTransformer.ShiftTransformer(shiftCount, BytesTransformer.ShiftTransformer.Type.LEFT_SHIFT));
    }

    /**
     * Bitwise right shifting of internal byte array.
     * <p>
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param shiftCount how many bits
     * @return shifted instance
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bit_shifts">Bit shifts</a>
     */
    public Bytes rightShift(int shiftCount) {
        return transform(new BytesTransformer.ShiftTransformer(shiftCount, BytesTransformer.ShiftTransformer.Type.RIGHT_SHIFT));
    }

    /**
     * Creates a new instance with a copy of the internal byte array.
     *
     * @return copied instance
     */
    public Bytes copy() {
        return wrap(Arrays.copyOf(array(), length()));
    }

    /**
     * Creates a new instance with a copy of the internal byte array.
     *
     * @param offset starting position in the source array
     * @param length of the new instance
     * @return copied instance
     */
    public Bytes copy(int offset, int length) {
        byte[] copy = new byte[length];
        System.arraycopy(array(), offset, copy, 0, copy.length);
        return wrap(copy);
    }

    /**
     * Reverses the internal byte array.
     * <p>
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @return reversed instance
     */
    public Bytes reverse() {
        return transform(new BytesTransformer.ReverseTransformer());
    }

    /**
     * Sorts the internal byte array according to given comparator.
     * <p>
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param comparator to sort the bytes
     * @return sorted instance
     */
    public Bytes sort(Comparator<Byte> comparator) {
        return transform(new BytesTransformer.SortTransformer(comparator));
    }

    /**
     * Sorts the internal byte array with it's natural ordering
     * <p>
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @return sorted instance
     */
    public Bytes sort() {
        return transform(new BytesTransformer.SortTransformer());
    }

    /**
     * Shuffles the internal byte array
     * <p>
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @param random used to create entropy for the shuffle
     * @return a shuffled instance
     */
    public Bytes shuffle(Random random) {
        return transform(new BytesTransformer.ShuffleTransformer(random));
    }

    /**
     * Shuffles the internal byte array with a new {@link SecureRandom} instance.
     * <p>
     * See the considerations about possible in-place operation in {@link #transform(BytesTransformer)}.
     *
     * @return a shuffled instance
     */
    public Bytes shuffle() {
        return transform(new BytesTransformer.ShuffleTransformer(new SecureRandom()));
    }

    /**
     * Generic transformation of this instance.
     * <p>
     * This transformation might be done in-place (ie. without copying the internal array and overwriting its old state),
     * or on a copy of the internal data, depending on the type (e.g. {@link MutableBytes}) and if the operation can be done
     * in-place. Therefore the caller has to ensure that certain side-effects, which occur due to the changing of the internal
     * data, do not create bugs in his/her code. Usually immutability is prefered, but when handling many or big byte arrays,
     * mutability enables drastically better performance.
     *
     * @param transformer used to transform this instance
     * @return the transformed instance (might be the same, or a new one)
     */
    public Bytes transform(BytesTransformer transformer) {
        return transformer.transform(this, mutable);
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

    /**
     * Create a new instance with the same underlying byte array.
     *
     * @return new instance backed by the same data
     */
    public Bytes duplicate() {
        return wrap(array());
    }

    /**
     * Set the byte order or endianness of this instance. Default in Java is {@link ByteOrder#BIG_ENDIAN}.
     * <p>
     * This option is important for all encoding and conversation methods.
     *
     * @return a new instance with the same underlying array and new order, or "this" if order is the same
     * @see <a href="https://en.wikipedia.org/wiki/Endianness">Endianness</a>
     */
    public Bytes byteOrder(ByteOrder byteOrder) {
        if (byteOrder != this.byteOrder) {
            return new Bytes(array(), byteOrder);
        }
        return this;
    }

    /* ATTRIBUTES ************************************************************************************************/

    /**
     * The byte length of the underlying byte array.
     *
     * @return byte length
     */
    public int length() {
        return array().length;
    }

    /**
     * The bit length of the underlying byte array.
     *
     * @return bit length
     */
    public int lengthBit() {
        return length() * 8;
    }

    /**
     * Checks the internal array for emptiness.
     *
     * @return if the underlying byte array has a length of 0
     */
    public boolean isEmpty() {
        return length() == 0;
    }

    /**
     * Get the set byte order or endianness. Default in Java is {@link ByteOrder#BIG_ENDIAN}.
     *
     * @return either {@link ByteOrder#BIG_ENDIAN} or {@link ByteOrder#LITTLE_ENDIAN}
     * @see <a href="https://en.wikipedia.org/wiki/Endianness">Endianness</a>
     */
    public ByteOrder byteOrder() {
        return byteOrder;
    }

    /**
     * Returns the index of the first appearance of the value {@code target} in
     * {@code array}.
     *
     * @param target a primitive {@code byte} value
     * @return the least index {@code i} for which {@code array[i] == target}, or
     * {@code -1} if no such index exists.
     */
    public int indexOf(byte target) {
        return Util.indexOf(array(), target, 0, length());
    }

    /**
     * Returns the start position of the first occurrence of the specified {@code
     * target} within {@code array}, or {@code -1} if there is no such occurrence.
     * <p>
     * <p>More formally, returns the lowest index {@code i} such that {@code
     * java.util.Arrays.copyOfRange(array, i, i + target.length)} contains exactly
     * the same elements as {@code target}.
     *
     * @param subArray the array to search for as a sub-sequence of {@code array}
     */
    public int indexOf(byte[] subArray) {
        return Util.indexOf(array(), subArray);
    }

    /**
     * Returns the index of the last appearance of the value {@code target} in
     * {@code array}.
     *
     * @param target a primitive {@code byte} value
     * @return the greatest index {@code i} for which {@code array[i] == target},
     * or {@code -1} if no such index exists.
     */
    public int lastIndexOf(byte target) {
        return Util.lastIndexOf(array(), target, 0, length());
    }

    /**
     * Returns the {@code byte} value at the specified index.
     * An index ranges from {@code 0} to {@code length() - 1}. The first {@code char} value of the sequence
     * is at index {@code 0}, the next at index {@code 1}, and so on, as for array indexing.
     *
     * @param index the index of the {@code byte} value.
     * @return the {@code byte} value at the specified index of the underlying byte array.
     * @throws IndexOutOfBoundsException if the {@code index} argument is negative or not less than the length of this string.
     */
    public byte byteAt(int index) {
        if (index < 0 || index > length()) {
            throw new IndexOutOfBoundsException("cannot get byte from index out of bounds: " + index);
        }
        return array()[index];
    }

    /**
     * Traverses the internal byte array counts the occurrences of given byte.
     * This has a time complexity of O(n).
     *
     * @param target byte to count
     * @return the count of given target in the byte array
     */
    public int count(byte target) {
        int count = 0;
        for (byte b : array()) {
            if (b == target) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the entropy of the internal byte array. This might be useful for judging the internal data
     * for using e.g. in security relevant use case. In statistical mechanics, entropy is related to the number of
     * microscopic configurations Ω that a thermodynamic system can have when in a state as specified by some macroscopic
     * variables. Specifically, assuming for simplicity that each of the microscopic configurations is equally probable,
     * the entropy of the system is the natural logarithm of that number of configurations, multiplied by the Boltzmann constant kB.
     * <p>
     * This implementation requires O(n) time and space complexity.
     *
     * @return entropy value; higher is more entropy (simply: more different values)
     * @see <a href="https://en.wikipedia.org/wiki/Entropy">Entropy</a>
     */
    public double entropy() {
        return new Util.Entropy<>(toList()).entropy();
    }

    /* GETTER ************************************************************************************************/

    /**
     * The internal byte array wrapped in a {@link ByteBuffer} instance.
     * Changes to it will be directly mirrored in this {@link Bytes} instance.
     * <p>
     * This will honor the set {@link #byteOrder()}.
     *
     * @return byte buffer
     */
    public ByteBuffer buffer() {
        return ByteBuffer.wrap(array()).order(byteOrder);
    }

    /**
     * The internal byte array wrapped in a {@link BigInteger} instance.
     * <p>
     * If the internal byte order is {@link ByteOrder#LITTLE_ENDIAN}, a copy of the internal
     * array will be reversed and used as backing array with the big integer. Otherwise the internal
     * array will be used directly.
     *
     * @return big integer
     */
    public BigInteger bigInteger() {
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return new BigInteger(new BytesTransformer.ReverseTransformer().transform(this, false).array());
        } else {
            return new BigInteger(array());
        }
    }

    /**
     * The reference of te internal byte-array. This call requires no conversation or additional memory allocation.
     * Changes to it will be directly mirrored in this {@link Bytes} instance.
     *
     * @return the direct reference of the internal byte array
     */
    public byte[] array() {
        return byteArray;
    }

    /* ENCODER ************************************************************************************************/

    /**
     * Binary (aka "1" and "0") representation. This is especially useful for debugging purposes.
     * Binary has a space efficiency of 12.5%.
     * <p>
     * Example: <code>10011100</code>
     *
     * @return binary string
     * @see <a href="https://en.wikipedia.org/wiki/Binary_number">Binary number</a>
     */
    public String encodeBinary() {
        return new ByteToTextEncoding.BaseRadix(2).encode(array());
    }

    /**
     * Octal (0-7) representation. Octal has a space efficiency of 37.5%.
     * <p>
     * Example: <code>1124517677707527755</code>
     *
     * @return octal number as string
     * @see <a href="https://en.wikipedia.org/wiki/Octal">Octal</a>
     */
    public String encodeOctal() {
        return new ByteToTextEncoding.BaseRadix(8).encode(array());
    }

    /**
     * Decimal (0-9) representation. It has a space efficiency of 41.5%.
     * <p>
     * Example: <code>20992966904426477</code>
     *
     * @return decimal number as string
     * @see <a href="https://en.wikipedia.org/wiki/Decimal">Decimal</a>
     */
    public String encodeDec() {
        return new ByteToTextEncoding.BaseRadix(10).encode(array());
    }

    /**
     * Base16 or Hex representation in lowercase. 2 characters represent a single byte, it therefore has an efficiency of 50%.
     * <p>
     * Example: <code>4a94fdff1eafed</code>
     *
     * @return hex string
     * @see <a href="https://en.wikipedia.org/wiki/Hexadecimal">Hexadecimal</a>
     */
    public String encodeHex() {
        return encodeHex(false);
    }

    /**
     * Base16 or Hex representation. See {@link #encodeHex()}.
     * <p>
     * Example: <code>4A94FDFF1EAFED</code>
     *
     * @param upperCase if the output character should be in uppercase
     * @return hex string
     */
    public String encodeHex(boolean upperCase) {
        return new ByteToTextEncoding.Hex(upperCase).encode(array());
    }

    /**
     * Base36 (aka Hexatrigesimal) representation. The choice of 36 is convenient in that the digits can be
     * represented using the Arabic numerals 0–9 and the Latin letters A–Z. This encoding has a space efficiency of 64.6%.
     * <p>
     * Example: <code>5qpdvuwjvu5</code>
     *
     * @return base36 string
     * @see <a href="https://en.wikipedia.org/wiki/Base36">Base36</a>
     */
    public String encodeBase36() {
        return new ByteToTextEncoding.BaseRadix(36).encode(array());
    }

    /**
     * Base64 representation with padding. This is *NOT* the url safe variation. This encoding has a space efficiency of 75%.
     * <p>
     * Example: <code>SpT9/x6v7Q==</code>
     *
     * @return base64 string
     * @see <a href="https://en.wikipedia.org/wiki/Base64">Base64</a>
     */
    public String encodeBase64() {
        return new ByteToTextEncoding.Base64Encoding().encode(array());
    }

    /**
     * UTF-8 representation of this byte array
     *
     * @return utf-8 encoded string
     * @see <a href="https://en.wikipedia.org/wiki/UTF-8">UTF-8</a>
     */
    public String encodeUtf8() {
        return new String(array(), StandardCharsets.UTF_8);
    }

    /**
     * Encode the internal byte-array with given encoder.
     *
     * @param encoder the encoder implementation
     * @return byte-to-text representation
     */
    public String encode(ByteToTextEncoding.Encoder encoder) {
        return encoder.encode(array());
    }

    /* CONVERTER ************************************************************************************************/

    /**
     * Returns a mutable version of this instance with sharing the same underlying byte-array.
     * If you want the mutable version to be a copy, call {@link #copy()} first.
     *
     * @return new mutable instance with same reference to internal byte array, or "this" if this is already of type {@link MutableBytes}
     */
    public MutableBytes toMutable() {
        if (this instanceof MutableBytes) {
            return (MutableBytes) this;
        } else {
            return new MutableBytes(array(), byteOrder);
        }
    }

    /**
     * Returns a copy of the internal byte-array as {@link List} collection type
     * This requires a time and space complexity of O(n).
     *
     * @return copy of internal array as list
     */
    public List<Byte> toList() {
        return Util.toList(array());
    }

    /**
     * Returns a copy of the internal byte-array as boxed primitive array.
     * This requires a time and space complexity of O(n).
     *
     * @return copy of internal array as object array
     */
    public Byte[] toObjectArray() {
        return Util.toObjectArray(array());
    }

    /**
     * If the underlying byte array is smaller than 2 byte / 16 bit returns unsigned two-complement
     * representation for a Java char integer value. The output is dependent on the set {@link #byteOrder()}.
     *
     * @return the int representation
     * @throws UnsupportedOperationException if byte array is longer than 2 byte
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Primitive Types</a>
     */
    public char toChar() {
        if (length() > 2) {
            throw new UnsupportedOperationException("cannot convert to char if length > 2 byte");
        }
        return buffer().getChar();
    }

    /**
     * If the underlying byte array is smaller than 2 byte / 16 bit returns signed two-complement
     * representation for a Java short integer value. The output is dependent on the set {@link #byteOrder()}.
     *
     * @return the int representation
     * @throws UnsupportedOperationException if byte array is longer than 2 byte
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Primitive Types</a>
     */
    public short toShort() {
        if (length() > 2) {
            throw new UnsupportedOperationException("cannot convert to short if length > 2 byte");
        }
        return buffer().getShort();
    }

    /**
     * If the underlying byte array is smaller than 4 byte / 32 bit returns signed two-complement
     * representation for a Java signed integer value. The output is dependent on the set {@link #byteOrder()}.
     *
     * @return the int representation
     * @throws UnsupportedOperationException if byte array is longer than 4 byte
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Primitive Types</a>
     */
    public int toInt() {
        if (length() > 4) {
            throw new UnsupportedOperationException("cannot convert to int if length > 4 byte");
        }
        return buffer().getInt();
    }

    /**
     * If the underlying byte array is smaller than 8 byte / 64 bit returns signed two-complement
     * representation for a Java signed long integer value. The output is dependent on the set {@link #byteOrder()}.
     *
     * @return the long representation
     * @throws UnsupportedOperationException if byte array is longer than 8 byte
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Primitive Types</a>
     */
    public long toLong() {
        if (length() > 8) {
            throw new UnsupportedOperationException("cannot convert to long if length > 8 byte");
        }
        return buffer().getLong();
    }

    /**
     * Compares this bytes instance to another.
     * <p>
     * <p> Two byte bytes are compared by comparing their sequences of
     * remaining elements lexicographically, without regard to the starting
     * position of each sequence within its corresponding buffer.
     * Pairs of {@code byte} elements are compared as if by invoking
     * {@link Byte#compare(byte, byte)}.
     * <p>
     * Uses {@link ByteBuffer#compareTo(Object)} internally.
     *
     * @return A negative integer, zero, or a positive integer as this buffer
     * is less than, equal to, or greater than the given buffer
     */
    @Override
    public int compareTo(Bytes o) {
        return buffer().compareTo(o.buffer());
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

    /**
     * A memory safe toString implementation, which only shows the byte length and at most 8 bytes preview in hex
     * representation.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        String preview;
        if (isEmpty()) {
            preview = "";
        } else if (length() > 8) {
            preview = "(0x" + copy(0, 4).encodeHex() + "..." + copy(length() - 4, 4).encodeHex() + ")";
        } else {
            preview = "(0x" + encodeHex() + ")";
        }

        return length() + " bytes " + preview;
    }
}
