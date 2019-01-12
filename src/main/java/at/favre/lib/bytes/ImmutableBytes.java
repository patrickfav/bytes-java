package at.favre.lib.bytes;

import java.nio.ByteOrder;

public class ImmutableBytes extends Bytes {

    ImmutableBytes(byte[] byteArray, ByteOrder byteOrder) {
        super(byteArray, byteOrder, new Factory());
    }

    @Override
    public byte[] array() {
        return copy().internalArray();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("immutable instance cannot close/wipe the internal array");
    }

    private static class Factory implements BytesFactory {
        @Override
        public Bytes wrap(byte[] array, ByteOrder byteOrder) {
            return new ImmutableBytes(array, byteOrder);
        }

        @Override
        public Bytes wrap(Bytes other, byte[] array) {
            return wrap(array, other.byteOrder());
        }

        @Override
        public Bytes wrap(Bytes other) {
            return wrap(other.isMutable() ? other.copy().array() : other.array(), other.byteOrder());
        }
    }
}
