package at.favre.lib.primitives.bytes;

import org.junit.Test;

import static org.junit.Assert.*;

public class BytesMiscTest extends ABytesTest {

    @Test
    public void testToString() throws Exception {
        testToString(Bytes.wrap(new byte[0]));
        testToString(Bytes.wrap(new byte[2]));
        testToString(Bytes.wrap(example_bytes_seven));
        testToString(Bytes.wrap(example2_bytes_seven));
        testToString(Bytes.wrap(example3_bytes_eight));
        testToString(Bytes.wrap(example4_bytes_sixteen));
    }

    private void testToString(Bytes bytes) {
        assertNotNull(bytes.toString());
        System.out.println(bytes.toString());
    }

    @Test
    public void testHashcode() throws Exception {
        assertEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.from(example_bytes_seven).hashCode());
        assertEquals(Bytes.wrap(example2_bytes_seven).hashCode(), Bytes.from(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example_bytes_seven).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(Bytes.wrap(example3_bytes_eight).hashCode(), Bytes.wrap(example2_bytes_seven).hashCode());
        assertNotEquals(0, Bytes.wrap(example2_bytes_seven).hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(Bytes.wrap(new byte[0]).equals(Bytes.wrap(new byte[0])));
        assertTrue(Bytes.wrap(new byte[16]).equals(Bytes.wrap(new byte[16])));
        assertTrue(Bytes.wrap(example_bytes_seven).equals(Bytes.from(example_bytes_seven)));
        assertTrue(Bytes.wrap(example2_bytes_seven).equals(Bytes.from(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example_bytes_seven).equals(Bytes.wrap(example2_bytes_seven)));
        assertFalse(Bytes.wrap(example3_bytes_eight).equals(Bytes.wrap(example2_bytes_seven)));
    }
}