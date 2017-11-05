package at.favre.lib.bytes;

import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class BytesTransformers {

    private BytesTransformers() {
    }

    public static BytesTransformer appendCrc32() {
        return new ChecksumTransformer(new CRC32(), ChecksumTransformer.Mode.APPEND, 4);
    }

    /**
     * Adds or converts to arbitrary checksum
     */
    final static class ChecksumTransformer implements BytesTransformer {
        enum Mode {
            /**
             * Appends checksum to given byte array
             */
            APPEND,
            /**
             * Transforms byte array and returns only checksum
             */
            TRANSFORM
        }

        private final Checksum checksum;
        private final Mode mode;
        private final int checksumLengthByte;

        public ChecksumTransformer(Checksum checksum, Mode mode, int checksumLengthByte) {
            if (checksumLengthByte < 0 || checksumLengthByte > 8)
                throw new IllegalArgumentException("checksumlength must be between 1 and 8 bytes");

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
    }
}
