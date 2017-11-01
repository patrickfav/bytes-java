package at.favre.lib.bytes;

import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BinaryToTextEncodingTest {
    @Test
    public void encodeHex() throws Exception {
        assertEquals("010203", new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
        assertEquals("030201", new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.Hex(false).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }

    @Test
    public void encodeBaseRadix() throws Exception {
        assertEquals("100211", new BinaryToTextEncoding.BaseRadix(16).encode(new byte[]{16, 2, 17}, ByteOrder.BIG_ENDIAN));
        assertEquals("110210", new BinaryToTextEncoding.BaseRadix(16).encode(new byte[]{16, 2, 17}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.BaseRadix(2).encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.BaseRadix(2).encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));

    }

    @Test
    public void encodeBase64() throws Exception {
        assertEquals("EAIR", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{16, 2, 17}, ByteOrder.BIG_ENDIAN));
        assertEquals("EQIQ", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{17, 2, 16}, ByteOrder.BIG_ENDIAN));
        assertEquals("EQIQ", new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{16, 2, 17}, ByteOrder.LITTLE_ENDIAN));
        assertNotEquals(new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{1, 2, 3}, ByteOrder.LITTLE_ENDIAN), new BinaryToTextEncoding.Base64Encoding().encode(new byte[]{1, 2, 3}, ByteOrder.BIG_ENDIAN));
    }
}