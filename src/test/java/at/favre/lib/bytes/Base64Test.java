package at.favre.lib.bytes;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test cases from rfc4648
 */
public class Base64Test {

    @Test
    public void decode() throws Exception {
        assertArrayEquals("".getBytes(), Base64.decode(""));
        assertArrayEquals("f".getBytes(), Base64.decode("Zg=="));
        assertArrayEquals("fo".getBytes(), Base64.decode("Zm8="));
        assertArrayEquals("foo".getBytes(), Base64.decode("Zm9v"));
        assertArrayEquals("foob".getBytes(), Base64.decode("Zm9vYg=="));
        assertArrayEquals("fooba".getBytes(), Base64.decode("Zm9vYmE="));
        assertArrayEquals("foobar".getBytes(), Base64.decode("Zm9vYmFy"));
    }

    @Test
    public void encode() throws Exception {
        assertEquals("", Base64.encode("".getBytes()));
        assertEquals("Zg==", Base64.encode("f".getBytes()));
        assertEquals("Zm8=", Base64.encode("fo".getBytes()));
        assertEquals("Zm9v", Base64.encode("foo".getBytes()));
        assertEquals("Zm9vYg==", Base64.encode("foob".getBytes()));
        assertEquals("Zm9vYmE=", Base64.encode("fooba".getBytes()));
        assertEquals("Zm9vYmFy", Base64.encode("foobar".getBytes()));
    }

}