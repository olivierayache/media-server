/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.ha.data;

/**
 *
 * @author Ayache
 */
public final class WriteableDataPacket extends DataPacket {

    public WriteableDataPacket(int index, int id) {
        super(index, id);
    }

    public void write(byte[] b, int offset, int length) {
        super.write(b, offset, length);
    }
}
