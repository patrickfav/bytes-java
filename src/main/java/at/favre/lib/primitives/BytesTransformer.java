package at.favre.lib.primitives;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Interface for transforming {@link Bytes}
 */
public interface BytesTransformer {

    /**
     * Transform a copy of given byte array and return the copy. The state of victim will not be changed.
     *
     * @param victim to preform the transformation on
     * @return new instance / copy
     */
    Bytes transform(Bytes victim);

    /**
     * Transform given victim in place, overwriting its internal byte array
     *
     * @param victim to preform the transformation on
     */
    void transformInPlace(Bytes victim);

    /**
     * Simple transformer for bitwise operations on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bitwise_operators">Bitwise operation</a>
     */
    final class BitWiseOperatorTransformer implements BytesTransformer {

        enum Mode {
            AND, OR, XOR
        }

        private final byte[] secondArray;
        private final Mode mode;

        public BitWiseOperatorTransformer(byte[] secondArray, Mode mode) {
            Objects.requireNonNull(secondArray, "the second byte array must not be null");
            Objects.requireNonNull(mode, "passed bitwise mode must not be null");
            this.secondArray = secondArray;
            this.mode = mode;
        }

        @Override
        public Bytes transform(Bytes victim) {
            return transform(victim, false);
        }

        @Override
        public void transformInPlace(Bytes victim) {
            transform(victim, true);
        }

        private Bytes transform(Bytes victim, boolean inPlace) {
            if (victim.length() != secondArray.length) {
                throw new IllegalArgumentException("all byte array must be of same length doing bit wise operation");
            }

            byte[] out;

            if (inPlace) {
                out = victim.array();
            } else {
                out = new byte[victim.length()];
            }

            for (int i = 0; i < victim.length(); i++) {
                switch (mode) {
                    case OR:
                        out[i] = (byte) (victim.array()[i] | secondArray[i]);
                        break;
                    case AND:
                        out[i] = (byte) (victim.array()[i] & secondArray[i]);
                        break;
                    case XOR:
                        out[i] = (byte) (victim.array()[i] ^ secondArray[i]);
                        break;
                    default:
                        throw new IllegalArgumentException("unknown bitwise transform mode " + mode);
                }
            }
            return Bytes.wrap(out);
        }
    }

    /**
     * Simple transformer for bitwise unary negation on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#NOT">Bitwise operators: NOT</a>
     */
    final class NegateTransformer implements BytesTransformer {
        @Override
        public Bytes transform(Bytes victim) {
            return transform(victim, false);
        }

        @Override
        public void transformInPlace(Bytes victim) {
            transform(victim, true);
        }

        private Bytes transform(Bytes victim, boolean inPlace) {
            byte[] out;

            if (inPlace) {
                out = victim.array();
            } else {
                out = new byte[victim.length()];
            }

            for (int i = 0; i < victim.length(); i++) {
                out[i] = (byte) ~victim.array()[i];
            }
            return Bytes.wrap(out);
        }
    }

    /**
     * Simple transformer for bit shifting {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bit_shifts">Bit shifts</a>
     */
    final class ShiftTransformer implements BytesTransformer {
        enum Type {
            LEFT_SHIFT, RIGHT_SHIFT
        }

        private final int shiftCount;
        private final Type type;

        public ShiftTransformer(int shiftCount, Type type) {
            Objects.requireNonNull(type, "passed shift type must not be null");

            this.shiftCount = shiftCount;
            this.type = type;
        }

        @Override
        public Bytes transform(Bytes victim) {
            return transform(victim, false);
        }

        @Override
        public void transformInPlace(Bytes victim) {
            transform(victim, true);
        }

        private Bytes transform(Bytes victim, boolean inPlace) {
            BigInteger bigInt;

            if (inPlace) {
                bigInt = new BigInteger(victim.array());
            } else {
                bigInt = new BigInteger(victim.copy().array());
            }

            switch (type) {
                case LEFT_SHIFT:
                    return Bytes.wrap(bigInt.shiftLeft(shiftCount).toByteArray());
                case RIGHT_SHIFT:
                    return Bytes.wrap(bigInt.shiftRight(shiftCount).toByteArray());
                default:
                    throw new IllegalArgumentException("unknown shift type " + type);
            }
        }
    }

    /**
     * Simple transformer for bitwise operations on {@link Bytes} instances
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bitwise_operation#Bitwise_operators">Bitwise operation</a>
     */
    final class ConcatTransformer implements BytesTransformer {
        private final byte[] secondArray;

        public ConcatTransformer(byte[] secondArrays) {
            Objects.requireNonNull(secondArrays, "the second byte array must not be null");
            this.secondArray = secondArrays;
        }

        @Override
        public Bytes transform(Bytes victim) {
            return Bytes.wrap(Bytes.Util.concat(victim.array(), secondArray));
        }

        @Override
        public void transformInPlace(Bytes victim) {
            transform(victim);
        }
    }
}
