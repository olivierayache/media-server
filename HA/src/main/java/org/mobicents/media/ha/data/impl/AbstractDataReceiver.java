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
package org.mobicents.media.ha.data.impl;

import java.io.IOException;
import java.net.InetAddress;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.messaging.GridMessaging;
import org.mobicents.media.ha.data.DataReceiver;
import org.mobicents.media.ha.message.Messenger;
import org.mobicents.media.ha.message.MessengerImpl;

/**
 *
 * @author Ayache
 */
public abstract class AbstractDataReceiver extends DataReceiver {

    static final String INIT = "INIT";
    static final String TOPIC = "TOPIC";

    private class DataReceiverMessenger extends MessengerImpl<String, PortsHolder, String> {

        public DataReceiverMessenger(GridMessaging messaging) {
            super(messaging);
        }

        @Override
        public void onMessageReceived(String sub, String body) {
            if (INIT.equals(body)) {
                sendMessage(DataBroadcasterFactory.TOPIC, new PortsHolder(ports, size));
            }
        }

    }
    
    private Messenger<String, PortsHolder, String> messenger;

    public AbstractDataReceiver(InetAddress gAddress, String nic, boolean autoBind) throws IOException {
        super(gAddress, nic, autoBind);
    }

    @Override
    protected void onChannelRegistered(int size, int... ports) {
        messenger = new DataReceiverMessenger(GridGain.grid().message());
        messenger.startReceiving(TOPIC);
        messenger.sendMessage(DataBroadcasterFactory.TOPIC, new PortsHolder(ports, size));
    }
}
