package at.favre.lib.bytes;

/**
 * Interface for validating byte arrays
 */
public interface BytesValidator {

    /**
     * Validates given byte array
     *
     * @param byteArrayToValidate array, must not be altered, only read
     * @return true if validation is successful, false otherwise
     */
    boolean validate(byte[] byteArrayToValidate);

    /**
     * Validates for specific array length
     */
    final class Length implements BytesValidator {
        enum Mode {
            SMALLER_THAN, GREATER_THAN, EXACT
        }

        private final int refLength;
        private final Mode mode;

        public Length(int refLength, Mode mode) {
            this.refLength = refLength;
            this.mode = mode;
        }

        @Override
        public boolean validate(byte[] byteArrayToValidate) {
            switch (mode) {
                case GREATER_THAN:
                    return byteArrayToValidate.length > refLength;
                case SMALLER_THAN:
                    return byteArrayToValidate.length < refLength;
                default:
                case EXACT:
                    return byteArrayToValidate.length == refLength;
            }
        }
    }


    /**
     * Checks if a byte array contains only the same value
     */
    final class IdenticalContent implements BytesValidator {
        final byte refByte;


        IdenticalContent(byte refByte) {
            this.refByte = refByte;
        }

        @Override
        public boolean validate(byte[] byteArrayToValidate) {
            for (byte b : byteArrayToValidate) {
                if (b != refByte) {
                    return false;
                }
            }
            return true;
        }
    }
}
