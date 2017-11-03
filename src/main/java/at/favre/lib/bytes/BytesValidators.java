package at.favre.lib.bytes;

/**
 * Util and easy access for {@link BytesValidators}
 */
public class BytesValidators {
    /**
     * Checks the length of a byte array
     *
     * @param value to check against
     * @return true if longer than given value
     */
    public static BytesValidator longerThan(int value) {
        return new BytesValidator.Length(value, BytesValidator.Length.Mode.GREATER_THAN);
    }

    public static BytesValidator shorterThan(int value) {
        return new BytesValidator.Length(value, BytesValidator.Length.Mode.SMALLER_THAN);
    }

    public static BytesValidator exactLength(int value) {
        return new BytesValidator.Length(value, BytesValidator.Length.Mode.EXACT);
    }

    public static BytesValidator onlyOf(byte value) {
        return new BytesValidator.IdenticalContent(value);
    }
}
