package at.favre.lib.bytes;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class UtilTest {
    @Test(expected = IllegalStateException.class)
    public void readFromStream() throws Exception {
        Util.readFromStream(null);
    }

    @Test
    public void concatVararg() throws Exception {
        assertArrayEquals(new byte[]{1}, Util.concatVararg((byte) 1, null));
    }
}