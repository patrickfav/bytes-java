package at.favre.lib.primitives;

import java.math.BigInteger;
import java.util.Objects;

public final class ByteToTextEncoding {
    public interface Encoder {
        String encode(byte[] array);
    }

    public interface Decoder {
        byte[] decode(String encoded);
    }

    public static class Hex implements Encoder, Decoder {
        private final boolean upperCase;

        public Hex() {
            this(true);
        }

        public Hex(boolean upperCase) {
            this.upperCase = upperCase;
        }

        @Override
        public String encode(byte[] byteArray) {
            StringBuilder sb = new StringBuilder(byteArray.length * 2);
            for (byte anArray : byteArray) {
                char first4Bit = Character.forDigit((anArray >> 4) & 0xF, 16);
                char last4Bit = Character.forDigit((anArray & 0xF), 16);
                if (upperCase) {
                    first4Bit = Character.toUpperCase(first4Bit);
                    last4Bit = Character.toUpperCase(last4Bit);
                }
                sb.append(first4Bit).append(last4Bit);
            }
            return sb.toString();
        }

        @Override
        public byte[] decode(String hexString) {
            Objects.requireNonNull(hexString, "hex input must not be null");
            if (hexString.length() % 2 != 0)
                throw new IllegalArgumentException("invalid hex string, must be mod 2 == 0");

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

    public static class Base64Encoding implements Encoder, Decoder {
        @Override
        public String encode(byte[] array) {
            return Base64.encode(array);
        }

        @Override
        public byte[] decode(String encoded) {
            return Base64.decode(encoded);
        }
    }

    public static class BaseRadixEncoder implements Encoder, Decoder {
        private final int radix;

        BaseRadixEncoder(int radix) {
            this.radix = radix;
        }

        @Override
        public String encode(byte[] array) {
            return new BigInteger(1, array).toString(radix);
        }

        @Override
        public byte[] decode(String encoded) {
            return new BigInteger(encoded, radix).toByteArray();
        }
    }
}
