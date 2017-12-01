/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.ha.data;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author Ayache
 */
public class DataPacket {

    private static final int DEFAULT_PACKET_SIZE = 4 * 8192;
    private final ByteBuffer data = ByteBuffer.allocateDirect(DEFAULT_PACKET_SIZE + 4 + 4);

    protected DataPacket(int index, int id) {
        data.putInt(id);
        data.putInt(index);
    }

    protected DataPacket() {
    }

    protected void write(byte[] b, int offset, int length) {
        data.put(b, offset, length);
        data.limit(data.position());
    }

    public ByteBuffer getDataBuffer() {
        data.rewind();
        return data;
    }

    public void recycle(int newIndex, int newId) {
        data.clear();
        data.putInt(newId);
        data.putInt(newIndex);
    }

    public void recycle() {
        data.clear();
    }

    @Override
    public DataPacket clone() {
        DataPacket dataPacket = new DataPacket();
        int position = data.position();
        data.rewind();
        dataPacket.getDataBuffer().put(data);
        data.position(position);
        dataPacket.getDataBuffer().rewind();
        return dataPacket;
    }

}
