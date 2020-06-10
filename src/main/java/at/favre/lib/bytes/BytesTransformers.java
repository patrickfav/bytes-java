package at.favre.lib.bytes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Collection of additional {@link BytesTransformer} for more specific use cases
 */
@SuppressWarnings("WeakerAccess")
public final class BytesTransformers {

    private BytesTransformers() {
    }

    /**
     * Create a {@link BytesTransformer} which shuffles the individual bytes in an array
     * with an {@link SecureRandom} instance.
     *
     * @return transformer
     */
    public static BytesTransformer shuffle() {
        return new ShuffleTransformer(new SecureRandom());
    }

    /**
     * Create a {@link BytesTransformer} which shuffles the individual bytes in an array
     *
     * @param random to use for entropy
     * @return transformer
     */
    public static BytesTransformer shuffle(Random random) {
        return new ShuffleTransformer(random);
    }

    /**
     * Create a {@link BytesTransformer} which sorts the internal byte array with it's natural ordering treating
     * each byte as signed byte (-128...127). Using inplace sorting, this can be reasonable fast.
     *
     * @return transformer
     */
    public static BytesTransformer sort() {
        return new SortTransformer();
    }

    /**
     * Create a {@link BytesTransformer} which sorts the internal byte array with it's natural ordering treating
     * each byte as unsigned byte (0...255). That is, the byte string {@code ff} sorts after {@code 00}.
     *
     * <strong>Note:</strong> this requires 2 copies of the internal array and a lot of unboxing due to
     * the fact that no primitives are not allowed as generic type arguments - so only use on small arrays.
     *
     * @return transformer
     */
    public static BytesTransformer sortUnsigned() {
        return new SortTransformer(new SortTransformer.UnsignedByteComparator());
    }

    /**
     * Create a {@link BytesTransformer} which sorts the internal byte array according to given comparator.
     *
     * <strong>Note:</strong> this requires 2 copies of the internal array and a lot of unboxing due to
     * the fact that no primitives are not allowed as generic type arguments - so only use on small arrays.
     *
     * @param comparator to sort the bytes
     * @return transformer
     */
    public static BytesTransformer sort(Comparator<Byte> comparator) {
        return new SortTransformer(comparator);
    }

    /**
     * Create a {@link BytesTransformer} which appends 4 byte Crc32 checksum to given bytes
     *
     * @return transformer
     */
    public static BytesTransformer checksumAppendCrc32() {
        return new ChecksumTransformer(new CRC32(), ChecksumTransformer.Mode.APPEND, 4);
    }

    /**
     * Create a {@link BytesTransformer} which transforms to 4 byte Crc32 checksum of given bytes
     *
     * @return transformer
     */
    public static BytesTransformer checksumCrc32() {
        return new ChecksumTransformer(new CRC32(), ChecksumTransformer.Mode.TRANSFORM, 4);
    }

    /**
     * Create a {@link BytesTransformer} which transforms to 4 byte Crc32 checksum of given bytes
     *
     * @param checksum           used algorithm
     * @param mode               mode (append or convert)
     * @param checksumLengthByte the byte length of the checksum; the {@link Checksum} class always returns 8 byte, but some
     *                           checksum algorithms (e.g. CRC32) only require smaller output. Must  be between 1 and 8 byte.
     * @return transformer
     */
    public static BytesTransformer checksum(Checksum checksum, ChecksumTransformer.Mode mode, int checksumLengthByte) {
        return new ChecksumTransformer(checksum, mode, checksumLengthByte);
    }

    /**
     * Create a {@link BytesTransformer} which gzip compresses the internal byte array
     *
     * @return transformer
     * @throws IllegalStateException if compression was not possible (i.e. wrapped {@link java.io.IOException})
     * @see <a href="https://en.wikipedia.org/wiki/Gzip">Gzip</a>
     */
    public static BytesTransformer compressGzip() {
        return new GzipCompressor(true);
    }

    /**
     * Create a {@link BytesTransformer} which gzip decompresses the internal byte array
     *
     * @return transformer
     * @throws IllegalStateException if compression was not possible (i.e. wrapped {@link java.io.IOException})
     * @see <a href="https://en.wikipedia.org/wiki/Gzip">Gzip</a>
     */
    public static BytesTransformer decompressGzip() {
        return new GzipCompressor(false);
    }

    /**
     * Create a {@link BytesTransformer} which returns the HMAC-SHA1 with given key, of the target byte array
     *
     * @param key to use for HMAC
     * @return hmac
     */
    public static BytesTransformer hmacSha1(byte[] key) {
        return new HmacTransformer(key, HmacTransformer.HMAC_SHA1);
    }

    /**
     * Create a {@link BytesTransformer} which returns the HMAC-SHA256 with given key, of the target byte array
     *
     * @param key to use for HMAC
     * @return hmac
     */
    public static BytesTransformer hmacSha256(byte[] key) {
        return new HmacTransformer(key, HmacTransformer.HMAC_SHA256);
    }

    /**
     * Create a {@link BytesTransformer} which returns the HMAC with given key, algorithm of the target byte array
     *
     * @param key           to use for HMAC
     * @param algorithmName e.g. 'HmacSHA256' - check if the algorithm is supported on your JVM/runtime
     * @return hmac (length depends on algorithm)
     */
    public static BytesTransformer hmac(byte[] key, String algorithmName) {
        return new HmacTransformer(key, algorithmName);
    }

    /**
     * Shuffles the internal byte array
     */
    public static final class ShuffleTransformer implements BytesTransformer {
        private final Random random;

        ShuffleTransformer(Random random) {
            Objects.requireNonNull(random, "passed random must not be null");
            this.random = random;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            byte[] out = inPlace ? currentArray : Bytes.from(currentArray).array();
            Util.Byte.shuffle(out, random);
            return out;
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return true;
        }

    }

    /**
     * Sorts the internal byte array with given {@link java.util.Comparator}
     */
    public static final class SortTransformer implements BytesTransformer {
        private final Comparator comparator;

        SortTransformer() {
            this(null);
        }

        SortTransformer(Comparator<Byte> comparator) {
            this.comparator = comparator;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            if (comparator == null) {
                byte[] out = inPlace ? currentArray : Bytes.from(currentArray).array();
                Arrays.sort(out);
                return out;
            } else {
                //no in-place implementation with comparator
                Byte[] boxedArray = Bytes.wrap(currentArray).toBoxedArray();
                Arrays.sort(boxedArray, comparator);
                return Bytes.from(boxedArray).array();
            }
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return comparator == null;
        }

        /**
         * Converting each byte into unsigned version and comparing it (0...255) vs (-128..127)
         */
        static final class UnsignedByteComparator implements Comparator<Byte> {
            @Override
            public int compare(Byte o1, Byte o2) {
                int byteA = o1 & 0xff;
                int byteB = o2 & 0xff;
                if (byteA == byteB) return 0;
                return byteA < byteB ? -1 : 1;
            }
        }

    }

    /**
     * Adds or converts to arbitrary checksum
     */
    public static final class ChecksumTransformer implements BytesTransformer {
        private final Checksum checksum;
        private final Mode mode;
        private final int checksumLengthByte;

        ChecksumTransformer(Checksum checksum, Mode mode, int checksumLengthByte) {
            if (checksumLengthByte <= 0 || checksumLengthByte > 8)
                throw new IllegalArgumentException("checksum length must be between 1 and 8 bytes");

            Objects.requireNonNull(checksum, "checksum instance must not be null");
            this.checksum = checksum;
            this.mode = mode;
            this.checksumLengthByte = checksumLengthByte;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            checksum.update(currentArray, 0, currentArray.length);
            byte[] checksumBytes = Bytes.from(checksum.getValue()).resize(checksumLengthByte).array();

            if (mode == Mode.TRANSFORM) {
                return checksumBytes;
            } else {
                return Bytes.from(currentArray, checksumBytes).array();
            }
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return false;
        }

        /**
         * Definitions of the mode
         */
        public enum Mode {
            /**
             * Appends checksum to given byte array
             */
            APPEND,
            /**
             * Transforms byte array and returns only checksum
             */
            TRANSFORM
        }
    }

    /**
     * Byte compression with gzip
     */
    public static final class GzipCompressor implements BytesTransformer {
        private final boolean compress;

        GzipCompressor(boolean compress) {
            this.compress = compress;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            return compress ? compress(currentArray) : decompress(currentArray);
        }

        private byte[] decompress(byte[] compressedContent) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(Math.max(32, compressedContent.length / 2));

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedContent))) {
                int len;
                byte[] buffer = new byte[4 * 1024];

                while ((len = gzipInputStream.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }

                return bos.toByteArray();
            } catch (Exception e) {
                throw new IllegalStateException("could not decompress gzip", e);
            }
        }

        private byte[] compress(byte[] content) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(content.length);

            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos)) {
                gzipOutputStream.write(content);
            } catch (Exception e) {
                throw new IllegalStateException("could not compress gzip", e);
            }

            return bos.toByteArray();
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return false;
        }
    }

    /**
     * HMAC transformer
     */
    public static final class HmacTransformer implements BytesTransformer {
        static final String HMAC_SHA1 = "HmacSHA1";
        static final String HMAC_SHA256 = "HmacSHA256";

        private final byte[] secretKey;
        private final String macAlgorithmName;

        /**
         * Create a new hmac transformer
         *
         * @param secretKey        to use as key
         * @param macAlgorithmName hash algorithm e.g. 'HmacSHA256'
         */
        HmacTransformer(byte[] secretKey, String macAlgorithmName) {
            this.macAlgorithmName = macAlgorithmName;
            this.secretKey = secretKey;
        }

        @Override
        public byte[] transform(byte[] currentArray, boolean inPlace) {
            try {
                Mac mac = Mac.getInstance(macAlgorithmName);
                mac.init(new SecretKeySpec(secretKey, macAlgorithmName));
                return mac.doFinal(currentArray);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public boolean supportInPlaceTransformation() {
            return false;
        }
    }
}
