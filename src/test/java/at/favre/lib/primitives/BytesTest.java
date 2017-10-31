package at.favre.lib.primitives;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class BytesTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parseBase16() throws Exception {
    }

    @Test
    public void toHex() throws Exception {
        assertArrayEquals(new byte[]{(byte) 0xA0, (byte) 0xE1}, Bytes.parseHex("A0E1").array());
        assertArrayEquals(new byte[]{(byte) 0xA0, (byte) 0xE1}, Bytes.parseHex("a0e1").array());
        assertArrayEquals(new byte[]{(byte) 0xA0, (byte) 0xE1}, Bytes.parseHex("0xA0E1").array());
    }

}