package at.favre.lib.primitives.bytes;

import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Mutable version of {@link Bytes}. If possible, all transformations are done in place, without creating a copy.
 * <p>
 * Adds additional mutator, which may change the internal array in-place, like {@link #wipe()}
 */
public final class MutableBytes extends Bytes {

    MutableBytes(byte[] byteArray, ByteOrder byteOrder) {
        super(byteArray, byteOrder, true);
    }

    public void wipe() {
        fill((byte) 0);
    }

    public void fill(byte fillByte) {
        Arrays.fill(array(), fillByte);
    }

    public void secureWipe() {
        secureWipe(new SecureRandom());
    }

    public void secureWipe(SecureRandom random) {
        random.nextBytes(array());
    }
}
