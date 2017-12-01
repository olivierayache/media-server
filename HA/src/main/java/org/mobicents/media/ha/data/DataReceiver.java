/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.ha.data;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ayache
 */
public abstract class DataReceiver {

    private final int lowestPort;
    private final int highestPort;
    private final Selector selector;
    private final InetAddress groupAddress;
    protected final int[] ports;
    protected int size;
    private final NetworkInterface nic;

    public DataReceiver(int lowestPort, int highestPort, InetAddress gAddress, String nic, boolean autoBind) throws IOException {
        this.lowestPort = lowestPort;
        this.highestPort = highestPort;
        this.nic = NetworkInterface.getByName(nic);
        selector = Selector.open();
        groupAddress = gAddress;
        ports = new int[highestPort - lowestPort];
        if (autoBind) {
            for (int i = lowestPort; i < highestPort; i++) {
                try {
                    registerNewChannel(i, groupAddress, this.nic);
                    ports[size] = i;
                    size++;
                } catch (IOException e) {
                    Logger.getLogger(DataReceiver.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            onChannelRegistered(size, ports);
        }
    }

    public DataReceiver(InetAddress gAddress, String nic, boolean autoBind) throws IOException {
        this.lowestPort = 64000;
        this.highestPort = 65535;
        this.nic = NetworkInterface.getByName(nic);
        selector = Selector.open();
        groupAddress = gAddress;
        ports = new int[highestPort - lowestPort];
        if (autoBind) {
            for (int i = lowestPort; i < highestPort; i++) {
                try {
                    registerNewChannel(i, groupAddress, this.nic);
                    ports[size] = i;
                    size++;
                } catch (IOException e) {
                    Logger.getLogger(DataReceiver.class.getName()).log(Level.SEVERE, "unable to bind to port "+i, e);
                }
            }
            onChannelRegistered(size, ports);
        }
    }

    private void registerNewChannel(int i, InetAddress groupAddress, NetworkInterface nic) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(i));
        channel.join(groupAddress,nic);
        channel.register(selector, SelectionKey.OP_READ, new ReadableDataPacket());
    }

    public void onMessageReceived(String id, Integer body) {
        try {
            registerNewChannel(body, groupAddress, nic);
        } catch (IOException ex) {
            Logger.getLogger(DataReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public abstract void onReceive(ReadableDataPacket dataPacket);

    protected abstract void onChannelRegistered(int size, int... ports);

    public void start() {
        new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        selector.select();
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = keys.iterator();
                        while (iterator.hasNext()) {
                            final SelectionKey next = iterator.next();
                            if (next.isReadable()) {
                                DatagramChannel channel = (DatagramChannel) next.channel();
                                ReadableDataPacket packet = (ReadableDataPacket) next.attachment();

                                if (!channel.isConnected()) {
                                    SocketAddress receive = channel.receive(packet.getDataBuffer());
                                    if (receive != null) {
                                        channel.connect(receive);
                                        onReceive(packet);
                                    }
                                }
                                int read = channel.read(packet.getDataBuffer());
                                if (read > 0) {
                                    onReceive(packet);
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DataReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        ).start();
    }
}
