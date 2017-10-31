package at.favre.lib.primitives.bytes;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        System.out.println(Bytes.from(number).toLong());
        assertArrayEquals(Bytes.parseHex("000000283f72d790").array(), Bytes.from(number).array());
        assertEquals(number, Bytes.from(number).toLong());

        byte[] defaultArray = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};

        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.BIG_ENDIAN)).encodeHex());
        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.BIG_ENDIAN).getInt()).encodeHex());
        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.LITTLE_ENDIAN)).encodeHex());
        System.out.println(Bytes.from(ByteBuffer.wrap(defaultArray).order(ByteOrder.LITTLE_ENDIAN).getInt()).encodeHex());
    }
}