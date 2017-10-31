package at.favre.lib.primitives.bytes;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class BytesConvertTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void toObjectArray() throws Exception {
        byte[] defaultArray = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
        assertArrayEquals(Bytes.wrap(defaultArray).toList().toArray(), Bytes.wrap(defaultArray).toObjectArray());
    }
}