package at.favre.lib.primitives;

public interface BytesTransformer {

    Bytes transform(Bytes thisBytes);

    final class XorTransformer implements BytesTransformer {
        private final byte[] arrayToXorWith;

        public XorTransformer(byte[] arrayToXorWith) {
            this.arrayToXorWith = arrayToXorWith;
        }

        @Override
        public Bytes transform(Bytes first) {
            if (first.length() != arrayToXorWith.length) {
                throw new IllegalArgumentException("all byte array must be of same length when xoring");
            }
            byte[] xored = new byte[first.length()];

            for (int i = 0; i < first.length(); i++) {
                xored[i] = (byte) (first.array()[i] ^ arrayToXorWith[i]);
            }
            return Bytes.wrap(xored);
        }
    }

    final class BitWiseOperatorTransformer implements BytesTransformer {
        enum Mode {
            AND, OR, XOR
        }

        private final byte[] subject;
        private final Mode mode;

        public BitWiseOperatorTransformer(byte[] subject, Mode mode) {
            this.subject = subject;
            this.mode = mode;
        }

        @Override
        public Bytes transform(Bytes victim) {
            if (victim.length() != subject.length) {
                throw new IllegalArgumentException("all byte array must be of same length doing bit wise operation");
            }
            byte[] out = new byte[victim.length()];

            for (int i = 0; i < victim.length(); i++) {
                switch (mode) {
                    case OR:
                        out[i] = (byte) (victim.array()[i] | subject[i]);
                        break;
                    case AND:
                        out[i] = (byte) (victim.array()[i] & subject[i]);
                        break;
                    case XOR:
                        out[i] = (byte) (victim.array()[i] ^ subject[i]);
                        break;
                    default:
                        throw new IllegalArgumentException("unknown mode");
                }
            }
            return Bytes.wrap(out);
        }
    }
}
