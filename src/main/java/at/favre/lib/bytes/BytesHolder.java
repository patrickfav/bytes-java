package at.favre.lib.bytes;

import java.nio.ByteOrder;

public interface BytesHolder {

    BytesHolder set(byte[] newArray);

    byte[] get();

    ByteOrder getByteOrder();

    abstract class ABytesHolder implements BytesHolder {
        private final ByteOrder byteOrder;
        //private final BytesFactory factory;
        private transient int hashCodeCache;

        ABytesHolder(ByteOrder byteOrder) {
            this.byteOrder = byteOrder;
        }

        @Override
        public ByteOrder getByteOrder() {
            return byteOrder;
        }
    }

    final class MutableBytesHolder extends ABytesHolder {
        private byte[] byteArray;

        MutableBytesHolder(byte[] byteArray, ByteOrder byteOrder) {
            super(byteOrder);
            this.byteArray = byteArray;
        }

        @Override
        public BytesHolder set(byte[] newArray) {
            byteArray = newArray;
            return this;
        }

        @Override
        public byte[] get() {
            return byteArray;
        }
    }

    final class ImmutableBytesHolder extends ABytesHolder {
        private final byte[] byteArray;

        ImmutableBytesHolder(byte[] byteArray, ByteOrder byteOrder) {
            super(byteOrder);
            this.byteArray = byteArray;
        }

        @Override
        public BytesHolder set(byte[] newArray) {
            return new ImmutableBytesHolder(newArray, getByteOrder());
        }

        @Override
        public byte[] get() {
            return byteArray;
        }
    }
}
