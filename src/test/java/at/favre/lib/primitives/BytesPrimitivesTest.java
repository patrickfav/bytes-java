package at.favre.lib.primitives;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BytesPrimitivesTest {
    private final static byte[] example2 = new byte[]{0x4A, (byte) 0x94, (byte) 0xFD, (byte) 0xFF, 0x1E, (byte) 0xAF, (byte) 0xED};

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void fromLong() throws Exception {
        long number = 172863182736L;
        System.out.println(Bytes.from(number).encodeHex());
        assertArrayEquals(Bytes.parseHex("000000283f72d790").array(), Bytes.from(number).array());
        assertEquals(number, Bytes.from(number).toLong());
    }
}