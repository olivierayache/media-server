/*
 * Copyright (C) 2014 TeleStax, Inc..
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.mobicents.media.ha.data;

import java.nio.ByteBuffer;
import junit.framework.TestCase;

/**
 *
 * @author Ayache
 */
public class DataPacketTest extends TestCase {

    public DataPacketTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of write method, of class DataPacket.
     */
    public void testWrite() {
        System.out.println("write");
        byte[] b = new byte[2000];
        int offset = 0;
        int length = 2000;
        DataPacket instance = new DataPacket();
        instance.write(b, offset, length);
        assertEquals(instance.getDataBuffer(), ByteBuffer.wrap(b));
    }

    /**
     * Test of getDataBuffer method, of class DataPacket.
     */
    public void testGetDataBuffer() {
        System.out.println("getDataBuffer");
        DataPacket instance = new DataPacket();
        ByteBuffer result = instance.getDataBuffer();
        int index = 28;
        int id = 5000;
        instance = new DataPacket(index, id);
        result = instance.getDataBuffer();
        assertEquals(id, result.getInt());
        assertEquals(index, result.getInt());
    }

    /**
     * Test of clone method, of class DataPacket.
     */
    public void testClone() {
        System.out.println("clone");
        DataPacket instance = new DataPacket();
        DataPacket expResult = instance;
        DataPacket result = instance.clone();
        assertEquals(expResult.getDataBuffer(), result.getDataBuffer());
        
        instance = new DataPacket(25, 5000);
        expResult = instance;
        result = instance.clone();
        assertEquals(expResult.getDataBuffer(), result.getDataBuffer());
    }

    /**
     * Test of recycle method, of class DataPacket.
     */
    public void testRecycle() {
        System.out.println("recycle");
        int newIndex = 0;
        int newId = 0;
        byte[] b = new byte[2000];
        int offset = 0;
        int length = 2000;
        DataPacket instance = new DataPacket();
        instance.write(b, offset, length);
        assertEquals(instance.getDataBuffer(), ByteBuffer.wrap(b));
        instance.recycle();
        instance.write(b, offset, length);
        assertEquals(instance.getDataBuffer(), ByteBuffer.wrap(b));
    }

}
