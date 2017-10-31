package at.favre.lib.primitives.bytes;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BytesMiscTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testToString() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
        byte[] defaultArray2 = new byte[]{(byte) 0xA1, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x14, (byte) 0x75, (byte) 0xE4, (byte) 0xA4, (byte) 0xFF};
        assertNotNull(Bytes.wrap(defaultArray).toString());
        assertNotNull(Bytes.wrap(defaultArray2).toString());
        System.out.println(Bytes.wrap(defaultArray).toString());
        System.out.println(Bytes.wrap(defaultArray2).toString());
    }
}