/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.ha.data;

import java.nio.ByteBuffer;

/**
 *
 * @author Ayache
 */
public final class ReadableDataPacket extends DataPacket {

    private final byte[] dst;

    public static class DataHolder {

        public final int size;
        public final byte[] data;

        public DataHolder(int size, byte[] data) {
            this.size = size;
            this.data = data;
        }

    }

    public ReadableDataPacket() {
        super();
        dst = new byte[getDataBuffer().capacity()];
    }

    public int getId() {
        return getDataBuffer().getInt(0);
    }

    public int getIndex() {
        return getDataBuffer().getInt(4);
    }

    public DataHolder getData() {
        ByteBuffer dataBuffer = getDataBuffer();
        dataBuffer.get(dst, 0, dataBuffer.limit());
        return new DataHolder(dataBuffer.limit(), dst);
    }

}
