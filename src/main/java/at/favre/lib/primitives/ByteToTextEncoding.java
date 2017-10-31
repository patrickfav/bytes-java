package at.favre.lib.primitives;

public final class ByteToTextEncoding {
    public interface Encoder {
        String encode(byte[] array);
    }

    public interface Decoder {
        byte[] decode(String encoded);
    }

    public static class Hex implements Encoder, Decoder {
        private final boolean lowerCase;

        public Hex() {
            this(true);
        }

        public Hex(boolean lowerCase) {
            this.lowerCase = lowerCase;
        }

        @Override
        public String encode(byte[] byteArray) {
            StringBuilder sb = new StringBuilder(byteArray.length * 2);
            for (byte anArray : byteArray) {
                sb.append(Character.forDigit((anArray >> 4) & 0xF, 16));
                sb.append(Character.forDigit((anArray & 0xF), 16));
            }
            String out = sb.toString();
            return lowerCase ? out.toLowerCase() : out;
        }

        @Override
        public byte[] decode(String hexString) {
            if (hexString.startsWith("0x")) {
                hexString = hexString.substring(2, hexString.length());
            }

            int len = hexString.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte)
                        ((Character.digit(hexString.charAt(i), 16) << 4)
                                + Character.digit(hexString.charAt(i + 1), 16));
            }
            return data;
        }
    }
}
