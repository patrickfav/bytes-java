package at.favre.lib.bytes;

import org.junit.Test;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class UtilConverterTest extends AUtilTest {

    @Test
    public void testToArray() {
        List<Byte> none = Collections.emptyList();
        assertArrayEquals(EMPTY, Util.Converter.toArray(none));

        List<Byte> one = Collections.singletonList((byte) 1);
        assertArrayEquals(ARRAY1, Util.Converter.toArray(one));

        byte[] array = {(byte) 0, (byte) 1, (byte) 0x55};

        List<Byte> three = Arrays.asList((byte) 0, (byte) 1, (byte) 0x55);
        assertArrayEquals(array, Util.Converter.toArray(three));

        assertArrayEquals(array, Util.Converter.toArray(Util.Converter.toList(array)));
    }

    @Test
    public void testToArray_withNull() {
        List<Byte> list = Arrays.asList((byte) 0, (byte) 1, null);
        try {
            Util.Converter.toArray(list);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testToArray_withConversion() {
        byte[] array = {(byte) 0, (byte) 1, (byte) 2};

        List<Byte> bytes = Arrays.asList((byte) 0, (byte) 1, (byte) 2);
        assertArrayEquals(array, Util.Converter.toArray(bytes));
    }

    @Test
    public void testAsList_toArray_roundTrip() {
        byte[] array = {(byte) 0, (byte) 1, (byte) 2};
        List<Byte> list = Util.Converter.toList(array);
        byte[] newArray = Util.Converter.toArray(list);

        // Make sure it returned a copy
        list.set(0, (byte) 4);
        assertArrayEquals(
                new byte[]{(byte) 0, (byte) 1, (byte) 2}, newArray);
        newArray[1] = (byte) 5;
        assertEquals((byte) 1, (byte) list.get(1));
    }

    @Test
    // This test stems from a real bug found by andrewk
    public void testAsList_subList_toArray_roundTrip() {
        byte[] array = {(byte) 0, (byte) 1, (byte) 2, (byte) 3};
        List<Byte> list = Util.Converter.toList(array);
        assertArrayEquals(new byte[]{(byte) 1, (byte) 2},
                Util.Converter.toArray(list.subList(1, 3)));
        assertArrayEquals(new byte[]{},
                Util.Converter.toArray(list.subList(2, 2)));
    }

    @Test
    public void testCharToByteArray() {
        Charset[] charsets = new Charset[]{StandardCharsets.UTF_8, StandardCharsets.US_ASCII, StandardCharsets.UTF_16};
        for (Charset charset : charsets) {
            checkCharArrayToByteArray("".toCharArray(), charset);
            checkCharArrayToByteArray("A".toCharArray(), charset);
            checkCharArrayToByteArray("12".toCharArray(), charset);
            checkCharArrayToByteArray("XYZ".toCharArray(), charset);
            checkCharArrayToByteArray("abcdefg".toCharArray(), charset);
            checkCharArrayToByteArray("71oh872gdl2dhp81g".toCharArray(), charset);

        }

        checkCharArrayToByteArray("யe2ாமறிந்தиют убSîne klâwenasd1".toCharArray(), StandardCharsets.UTF_8);
    }

    private void checkCharArrayToByteArray(char[] subject, Charset charset) {
        for (int lenI = 1; lenI < subject.length + 1; lenI++) {
            for (int offsetI = 0; offsetI < subject.length; offsetI++) {
                if (offsetI + lenI > subject.length) break;
                byte[] bytes = Util.Converter.charToByteArray(subject, charset, offsetI, lenI);
                assertEquals(Bytes.wrap(bytes), Bytes.wrap(new String(subject).substring(offsetI, offsetI + lenI).getBytes(charset)));
            }
        }
        compareArrayToByteArrayWithoutOffset(subject, charset);
    }

    private void compareArrayToByteArrayWithoutOffset(char[] subject, Charset charset) {
        assertArrayEquals(Util.Converter.charToByteArray(subject, charset, 0, subject.length), new String(subject).getBytes(charset));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCharToByteArrayIllegalOffset() {
        Util.Converter.charToByteArray("abcdef".toCharArray(), StandardCharsets.UTF_8, -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCharToByteArrayIllegalLength() {
        Util.Converter.charToByteArray("abcdef".toCharArray(), StandardCharsets.UTF_8, 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCharToByteArrayIllegalOffsetPlusLength() {
        Util.Converter.charToByteArray("abcdef".toCharArray(), StandardCharsets.UTF_8, 4, 3);
    }

    @Test
    public void testToIntArray() {
        assertArrayEquals(new int[]{1}, Util.Converter.toIntArray(new byte[]{0, 0, 0, 1}, ByteOrder.BIG_ENDIAN));
        assertArrayEquals(new int[]{257}, Util.Converter.toIntArray(new byte[]{0, 0, 1, 1}, ByteOrder.BIG_ENDIAN));

        assertArrayEquals(new int[]{1}, Util.Converter.toIntArray(new byte[]{1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN));
        assertArrayEquals(new int[]{257}, Util.Converter.toIntArray(new byte[]{1, 1, 0, 0}, ByteOrder.LITTLE_ENDIAN));

        assertArrayEquals(new int[]{16_843_009}, Util.Converter.toIntArray(new byte[]{1, 1, 1, 1}, ByteOrder.BIG_ENDIAN));
        assertArrayEquals(new int[]{571_211_845}, Util.Converter.toIntArray(new byte[]{34, 12, 0, 69}, ByteOrder.BIG_ENDIAN));
        assertArrayEquals(new int[]{1_290_429_439}, Util.Converter.toIntArray(new byte[]{76, (byte) 234, 99, (byte) 255}, ByteOrder.BIG_ENDIAN));
        assertArrayEquals(new int[]{257, 65_793, 1}, Util.Converter.toIntArray(new byte[]{0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1}, ByteOrder.BIG_ENDIAN));

        assertArrayEquals(new int[]{1_290_429_439}, Util.Converter.toIntArray(new byte[]{(byte) 255, 99, (byte) 234, 76}, ByteOrder.LITTLE_ENDIAN));
        assertArrayEquals(new int[]{257, 65_793, 1}, Util.Converter.toIntArray(new byte[]{1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN));

        assertArrayEquals(new int[0], Util.Converter.toIntArray(new byte[0], ByteOrder.LITTLE_ENDIAN));
        assertArrayEquals(new int[0], Util.Converter.toIntArray(new byte[0], ByteOrder.BIG_ENDIAN));
    }

    @Test
    public void testToLongArray() {
        assertArrayEquals(new long[]{1}, Util.Converter.toLongArray(new byte[]{0, 0, 0, 0, 0, 0, 0, 1}, ByteOrder.BIG_ENDIAN));
        assertArrayEquals(new long[]{257}, Util.Converter.toLongArray(new byte[]{0, 0, 0, 0, 0, 0, 1, 1}, ByteOrder.BIG_ENDIAN));

        assertArrayEquals(new long[]{1}, Util.Converter.toLongArray(new byte[]{1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN));
        assertArrayEquals(new long[]{257}, Util.Converter.toLongArray(new byte[]{1, 1, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN));

        assertArrayEquals(new long[]{-7124130559744646145L}, Util.Converter.toLongArray(new byte[]{(byte) 157, 34, 1, 0, 76, (byte) 234, 99, (byte) 255}, ByteOrder.BIG_ENDIAN));
        assertArrayEquals(new long[]{-7124130559744646145L}, Util.Converter.toLongArray(new byte[]{(byte) 255, 99, (byte) 234, 76, 0, 1, 34, (byte) 157}, ByteOrder.LITTLE_ENDIAN));

        assertArrayEquals(new long[]{257, 1}, Util.Converter.toLongArray(new byte[]{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1}, ByteOrder.BIG_ENDIAN));
        assertArrayEquals(new long[]{257, 1}, Util.Converter.toLongArray(new byte[]{1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN));

        assertArrayEquals(new long[0], Util.Converter.toLongArray(new byte[0], ByteOrder.LITTLE_ENDIAN));
        assertArrayEquals(new long[0], Util.Converter.toLongArray(new byte[0], ByteOrder.BIG_ENDIAN));
    }

    @Test
    public void testToFloatArray() {
        assertArrayEquals(new float[]{1.4E-45f}, Util.Converter.toFloatArray(new byte[]{0, 0, 0, 1}, ByteOrder.BIG_ENDIAN), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f}, Util.Converter.toFloatArray(new byte[]{0, 0, 1, 1}, ByteOrder.BIG_ENDIAN), 0.01f);

        assertArrayEquals(new float[]{1.4E-45f}, Util.Converter.toFloatArray(new byte[]{1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f}, Util.Converter.toFloatArray(new byte[]{1, 1, 0, 0}, ByteOrder.LITTLE_ENDIAN), 0.01f);

        assertArrayEquals(new float[]{2.3694278E-38f}, Util.Converter.toFloatArray(new byte[]{1, 1, 1, 1}, ByteOrder.BIG_ENDIAN), 0.01f);
        assertArrayEquals(new float[]{1.897368E-18f}, Util.Converter.toFloatArray(new byte[]{34, 12, 0, 69}, ByteOrder.BIG_ENDIAN), 0.01f);
        assertArrayEquals(new float[]{1.22888184E8f}, Util.Converter.toFloatArray(new byte[]{76, (byte) 234, 99, (byte) 255}, ByteOrder.BIG_ENDIAN), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f, 9.2196E-41f, 1.4E-45f}, Util.Converter.toFloatArray(new byte[]{0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1}, ByteOrder.BIG_ENDIAN), 0.01f);

        assertArrayEquals(new float[]{1.22888184E8f}, Util.Converter.toFloatArray(new byte[]{(byte) 255, 99, (byte) 234, 76}, ByteOrder.LITTLE_ENDIAN), 0.01f);
        assertArrayEquals(new float[]{3.6E-43f, 9.2196E-41f, 1.4E-45f}, Util.Converter.toFloatArray(new byte[]{1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN), 0.01f);

        assertArrayEquals(new float[0], Util.Converter.toFloatArray(new byte[0], ByteOrder.LITTLE_ENDIAN), 0.01f);
        assertArrayEquals(new float[0], Util.Converter.toFloatArray(new byte[0], ByteOrder.BIG_ENDIAN), 0.01f);
    }

    @Test
    public void testToDoubleArray() {
        assertArrayEquals(new double[]{1.4E-45}, Util.Converter.toDoubleArray(new byte[]{0, 0, 0, 0, 0, 0, 0, 1}, ByteOrder.BIG_ENDIAN), 0.01);
        assertArrayEquals(new double[]{3.6E-43}, Util.Converter.toDoubleArray(new byte[]{0, 0, 0, 0, 0, 0, 1, 1}, ByteOrder.BIG_ENDIAN), 0.01);

        assertArrayEquals(new double[]{1.4E-45}, Util.Converter.toDoubleArray(new byte[]{1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN), 0.01);
        assertArrayEquals(new double[]{3.6E-43}, Util.Converter.toDoubleArray(new byte[]{1, 1, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN), 0.01);

        assertArrayEquals(new double[]{-2.385279556059394E-168}, Util.Converter.toDoubleArray(new byte[]{(byte) 157, 34, 1, 0, 76, (byte) 234, 99, (byte) 255}, ByteOrder.BIG_ENDIAN), 0.01);
        assertArrayEquals(new double[]{-2.385279556059394E-168}, Util.Converter.toDoubleArray(new byte[]{(byte) 255, 99, (byte) 234, 76, 0, 1, 34, (byte) 157}, ByteOrder.LITTLE_ENDIAN), 0.01);

        assertArrayEquals(new double[]{1.27E-321, 4.9E-324}, Util.Converter.toDoubleArray(new byte[]{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1}, ByteOrder.BIG_ENDIAN), 0.01);
        assertArrayEquals(new double[]{1.27E-321, 4.9E-324}, Util.Converter.toDoubleArray(new byte[]{1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, ByteOrder.LITTLE_ENDIAN), 0.01);

        assertArrayEquals(new double[0], Util.Converter.toDoubleArray(new byte[0], ByteOrder.LITTLE_ENDIAN), 0.01);
        assertArrayEquals(new double[0], Util.Converter.toDoubleArray(new byte[0], ByteOrder.BIG_ENDIAN), 0.01);
    }
}
