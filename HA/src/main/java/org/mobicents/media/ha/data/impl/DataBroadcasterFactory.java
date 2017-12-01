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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gridgain.grid.GridGain;
import org.mobicents.media.ha.data.DataBroadcaster;
import org.mobicents.media.ha.message.Messenger;
import org.mobicents.media.ha.message.MessengerImpl;
import org.mobicents.media.server.concurrent.ConcurrentCyclicFIFO;

/**
 *
 * @author Ayache
 */
public class DataBroadcasterFactory {

    private static final class DataBroadcasterFactoryHolder {

        private static final DataBroadcasterFactory INSTANCE = new DataBroadcasterFactory();
    }

    private static final class VersionedDataBroadcaster extends DataBroadcaster {

        final int version;

        public VersionedDataBroadcaster(int version, InetAddress groupAddress) throws IOException {
            super(groupAddress);
            this.version = version;
        }

        public VersionedDataBroadcaster(int version, InetAddress groupAddress, int port) throws IOException {
            super(groupAddress, port);
            this.version = version;
        }

    }

    private final Messenger<String, String, PortsHolder> messenger;
    private final ConcurrentCyclicFIFO<DataBroadcaster> broadcasters = new ConcurrentCyclicFIFO<DataBroadcaster>();
    private volatile int version = 0;
    static final String TOPIC = "BR_HA";

    private DataBroadcasterFactory() {
        messenger = new MessengerImpl<String, String, PortsHolder>(GridGain.grid().message()) {

            @Override
            public void onMessageReceived(String sub, PortsHolder body) {
                try {
                    fillFactory(body.ports, body.size);
                    Logger.getLogger(DataBroadcasterFactory.class.getName()).log(Level.INFO, "Message received for filling factory {0}", body);
                } catch (IOException ex) {
                    Logger.getLogger(DataBroadcasterFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        messenger.startReceiving(TOPIC);
    }

    public void requestForBroadcasters() {
        if (broadcasters.size() == 0) {
            messenger.sendMessage(AbstractDataReceiver.TOPIC, AbstractDataReceiver.INIT);
        }
    }

    private void fillFactory(int[] ports, int size) throws IOException {
        for (int i = 0; i < size; i++) {
            broadcasters.offer(new VersionedDataBroadcaster(version, InetAddress.getByName("228.10.10.157"), ports[i]));
        }
        version++;
    }

    public DataBroadcaster poll() {
        DataBroadcaster poll = broadcasters.poll();
        if (version != ((VersionedDataBroadcaster) poll).version) {
            poll();
        }
        return poll;
    }

    public void offer(DataBroadcaster broadcaster) {
        if (version == ((VersionedDataBroadcaster) broadcaster).version) {
            broadcasters.offer(broadcaster);
        }
    }

    public void clear() {
        broadcasters.clear();
    }

    public static DataBroadcasterFactory getInstance() {
        return DataBroadcasterFactoryHolder.INSTANCE;
    }
}
