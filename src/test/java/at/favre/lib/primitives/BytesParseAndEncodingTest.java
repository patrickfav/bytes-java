package at.favre.lib.primitives;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BytesParseAndEncodingTest {
    private final static byte[] example2 = new byte[]{0x4A, (byte) 0x94, (byte) 0xFD, (byte) 0xFF, 0x1E, (byte) 0xAF, (byte) 0xED};

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parseHex() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertArrayEquals(defaultArray, Bytes.parseHex("A0E1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex("a0e1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex("0xA0E1").array());
        assertArrayEquals(defaultArray, Bytes.parseHex(Bytes.parseHex("A0E1").encodeHex()).array());
    }

    @Test
    public void encodeHex() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("a0e1", Bytes.wrap(defaultArray).encodeHex());
        assertEquals("A0E1", Bytes.wrap(defaultArray).encodeHex(true));
        assertEquals(Bytes.wrap(defaultArray).encodeHex(), Bytes.wrap(defaultArray).encodeHex(false));
        assertEquals("4a94fdff1eafed", Bytes.wrap(example2).encodeHex());
    }

    @Test
    public void parseBase64() throws Exception {
        assertArrayEquals(example2, Bytes.parseBase64("SpT9/x6v7Q==").array());
    }

    @Test
    public void encodeBase64() throws Exception {
        assertEquals("SpT9/x6v7Q==", Bytes.wrap(example2).encodeBase64());
    }

    @Test
    public void encodeBinary() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("1010000011100001", Bytes.wrap(defaultArray).encodeBinary());
        assertEquals("1001010100101001111110111111111000111101010111111101101", Bytes.wrap(example2).encodeBinary());
    }

    @Test
    public void parseOctal() throws Exception {
        assertArrayEquals(example2, Bytes.parseOctal("1124517677707527755").array());
    }

    @Test
    public void encodeOctal() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("120341", Bytes.wrap(defaultArray).encodeOctal());
        assertEquals("1124517677707527755", Bytes.wrap(example2).encodeOctal());
    }

    @Test
    public void parseDec() throws Exception {
        assertArrayEquals(example2, Bytes.parseDec("20992966904426477").array());
    }

    @Test
    public void encodeDec() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1};
        assertEquals("41185", Bytes.wrap(defaultArray).encodeDec());
        assertEquals("20992966904426477", Bytes.wrap(example2).encodeDec());
    }

    @Test
    public void parseBase36() throws Exception {
        assertArrayEquals(example2, Bytes.parseBase36("5qpdvuwjvu5").array());
    }

    @Test
    public void encodeBase36() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0xA0, (byte) 0xE1, (byte) 0x13};
        assertEquals("69zbn", Bytes.wrap(defaultArray).encodeBase36());
        assertEquals("5qpdvuwjvu5", Bytes.wrap(example2).encodeBase36());
    }
}