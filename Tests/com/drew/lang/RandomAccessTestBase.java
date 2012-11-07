package com.drew.lang;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Base class for testing implementations of {@link RandomAccessReader}.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public abstract class RandomAccessTestBase
{
    protected abstract RandomAccessReader createReader(byte[] bytes);

    @Test
    public void testDefaultEndianness()
    {
        Assert.assertEquals(true, createReader(new byte[1]).isMotorolaByteOrder());
    }

    @Test
    public void testGetInt8() throws BufferBoundsException
    {
        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals((byte)0, reader.getInt8(0));
        Assert.assertEquals((byte)1, reader.getInt8(1));
        Assert.assertEquals((byte)127, reader.getInt8(2));
        Assert.assertEquals((byte)255, reader.getInt8(3));
    }

    @Test
    public void testGetUInt8() throws BufferBoundsException
    {
        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals(0, reader.getUInt8(0));
        Assert.assertEquals(1, reader.getUInt8(1));
        Assert.assertEquals(127, reader.getUInt8(2));
        Assert.assertEquals(255, reader.getUInt8(3));
    }

    @Test
    public void testGetUInt8_OutOfBounds()
    {
        try {
            RandomAccessReader reader = createReader(new byte[2]);
            reader.getUInt8(2);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read from beyond end of underlying data source (requested index: 2, requested count: 1, max index: 1)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt16() throws BufferBoundsException
    {
        Assert.assertEquals(-1, createReader(new byte[]{(byte)0xff,(byte)0xff}).getInt16(0));

        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals((short)0x0001, reader.getInt16(0));
        Assert.assertEquals((short)0x017F, reader.getInt16(1));
        Assert.assertEquals((short)0x7FFF, reader.getInt16(2));

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals((short)0x0100, reader.getInt16(0));
        Assert.assertEquals((short)0x7F01, reader.getInt16(1));
        Assert.assertEquals((short)0xFF7F, reader.getInt16(2));
    }

    @Test
    public void testGetUInt16() throws BufferBoundsException
    {
        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals(0x0001, reader.getUInt16(0));
        Assert.assertEquals(0x017F, reader.getUInt16(1));
        Assert.assertEquals(0x7FFF, reader.getUInt16(2));

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(0x0100, reader.getUInt16(0));
        Assert.assertEquals(0x7F01, reader.getUInt16(1));
        Assert.assertEquals(0xFF7F, reader.getUInt16(2));
    }

    @Test
    public void testGetUInt16_OutOfBounds()
    {
        try {
            RandomAccessReader reader = createReader(new byte[2]);
            reader.getUInt16(1);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read from beyond end of underlying data source (requested index: 1, requested count: 2, max index: 1)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt32() throws BufferBoundsException
    {
        Assert.assertEquals(-1, createReader(new byte[]{(byte)0xff,(byte)0xff, (byte)0xff,(byte)0xff}).getInt32(0));

        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF, 0x02, 0x03, 0x04 };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals(0x00017FFF, reader.getInt32(0));
        Assert.assertEquals(0x017FFF02, reader.getInt32(1));
        Assert.assertEquals(0x7FFF0203, reader.getInt32(2));
        Assert.assertEquals(0xFF020304, reader.getInt32(3)); // equiv
        Assert.assertEquals(-16645372, reader.getInt32(3));  //

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(0xFF7F0100, reader.getInt32(0));
        Assert.assertEquals(0x02FF7F01, reader.getInt32(1));
        Assert.assertEquals(0x0302FF7F, reader.getInt32(2));
    }

    @Test
    public void testGetUInt32() throws BufferBoundsException
    {
        Assert.assertEquals(4294967295L, createReader(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff}).getUInt32(0));

        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF, 0x02, 0x03, 0x04 };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals(0x00017FFF, reader.getUInt32(0));
        Assert.assertEquals(0x017FFF02, reader.getUInt32(1));
        Assert.assertEquals(0x7FFF0203, reader.getUInt32(2));
        Assert.assertEquals(4278321924L, reader.getUInt32(3)); // equiv

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(4286513408L, reader.getUInt32(0));
        Assert.assertEquals(0x02FF7F01, reader.getUInt32(1));
        Assert.assertEquals(0x0302FF7F, reader.getUInt32(2));
    }

    @Test
    public void testGetInt32_OutOfBounds()
    {
        try {
            RandomAccessReader reader = createReader(new byte[3]);
            reader.getInt32(0);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read from beyond end of underlying data source (requested index: 0, requested count: 4, max index: 2)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt64() throws BufferBoundsException
    {
        byte[] buffer = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, (byte)0xFF };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals(0x0001020304050607L, reader.getInt64(0));
        Assert.assertEquals(0x01020304050607FFL, reader.getInt64(1));

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(0x0706050403020100L, reader.getInt64(0));
        Assert.assertEquals(0xFF07060504030201L, reader.getInt64(1));
    }

    @Test
    public void testGetInt64_OutOfBounds()
    {
        try {
            RandomAccessReader reader = createReader(new byte[7]);
            reader.getInt64(0);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read from beyond end of underlying data source (requested index: 0, requested count: 8, max index: 6)", ex.getMessage());
        }
        try {
            RandomAccessReader reader = createReader(new byte[7]);
            reader.getInt64(-1);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read from buffer using a negative index (-1)", ex.getMessage());
        }
    }

    @Test
    public void testGetFloat32() throws BufferBoundsException
    {
        final int nanBits = 0x7fc00000;
        Assert.assertEquals(Float.NaN, Float.intBitsToFloat(nanBits));

        byte[] buffer = new byte[] { 0x7f, (byte)0xc0, 0x00, 0x00 };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals(Float.NaN, reader.getFloat32(0));
    }

    @Test
    public void testGetFloat64() throws BufferBoundsException
    {
        final long nanBits = 0xfff0000000000001L;
        Assert.assertEquals(Double.NaN, Double.longBitsToDouble(nanBits));

        byte[] buffer = new byte[] { (byte)0xff, (byte)0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
        RandomAccessReader reader = createReader(buffer);

        Assert.assertEquals(Double.NaN, reader.getDouble64(0));
    }

    @Test
    public void testGetNullTerminatedString() throws BufferBoundsException
    {
        byte[] bytes = new byte[]{ 0x41, 0x42, 0x43, 0x44, 0x00, 0x45, 0x46, 0x47 };
        RandomAccessReader reader = createReader(bytes);

        Assert.assertEquals("", reader.getNullTerminatedString(0, 0));
        Assert.assertEquals("A", reader.getNullTerminatedString(0, 1));
        Assert.assertEquals("AB", reader.getNullTerminatedString(0, 2));
        Assert.assertEquals("ABC", reader.getNullTerminatedString(0, 3));
        Assert.assertEquals("ABCD", reader.getNullTerminatedString(0, 4));
        Assert.assertEquals("ABCD", reader.getNullTerminatedString(0, 5));
        Assert.assertEquals("ABCD", reader.getNullTerminatedString(0, 6));

        Assert.assertEquals("BCD", reader.getNullTerminatedString(1, 3));
        Assert.assertEquals("BCD", reader.getNullTerminatedString(1, 4));
        Assert.assertEquals("BCD", reader.getNullTerminatedString(1, 5));

        Assert.assertEquals("", reader.getNullTerminatedString(4, 3));
    }

    @Test
    public void testGetString() throws BufferBoundsException
    {
        byte[] bytes = new byte[]{ 0x41, 0x42, 0x43, 0x44, 0x00, 0x45, 0x46, 0x47 };
        RandomAccessReader reader = createReader(bytes);

        Assert.assertEquals("", reader.getString(0, 0));
        Assert.assertEquals("A", reader.getString(0, 1));
        Assert.assertEquals("AB", reader.getString(0, 2));
        Assert.assertEquals("ABC", reader.getString(0, 3));
        Assert.assertEquals("ABCD", reader.getString(0, 4));
        Assert.assertEquals("ABCD\0", reader.getString(0, 5));
        Assert.assertEquals("ABCD\0E", reader.getString(0, 6));

        Assert.assertEquals("BCD", reader.getString(1, 3));
        Assert.assertEquals("BCD\0", reader.getString(1, 4));
        Assert.assertEquals("BCD\0E", reader.getString(1, 5));

        Assert.assertEquals("\0EF", reader.getString(4, 3));
    }

    @Test
    public void testIndexPlusCountExceedsIntMaxValue()
    {
        RandomAccessReader reader = createReader(new byte[10]);

        try {
            reader.getBytes(0x6FFFFFFF, 0x6FFFFFFF);
        } catch (BufferBoundsException e) {
            Assert.assertEquals("Number of requested bytes summed with starting index exceed maximum range of signed 32 bit integers (requested index: 1879048191, requested count: 1879048191)", e.getMessage());
        }
    }

    @Test
    public void testOverflowBoundsCalculation()
    {
        RandomAccessReader reader = createReader(new byte[10]);

        try {
            reader.getBytes(5, 10);
        } catch (BufferBoundsException e) {
            Assert.assertEquals("Attempt to read from beyond end of underlying data source (requested index: 5, requested count: 10, max index: 9)", e.getMessage());
        }
    }
}
