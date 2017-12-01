package org.mobicents.media.ha.data;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ayache
 */
public class DataBroadcaster implements IBrodcastPortListener {

    private final DatagramChannel channel;
    protected final InetAddress broadcastAdress;
    protected SocketAddress broadcastSocketAdress;
    private final List<DataPacket> localBackup = new ArrayList<DataPacket>();

    public DataBroadcaster(InetAddress groupAddress) throws IOException {
        broadcastAdress = groupAddress;
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
    }
    
    public DataBroadcaster(InetAddress groupAddress, int port) throws IOException {
        broadcastAdress = groupAddress;
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        broadcastSocketAdress = new InetSocketAddress(broadcastAdress, port);
        channel.connect(broadcastSocketAdress);
    }

    public void send(DataPacket dataPacket) throws IOException {
        if (broadcastSocketAdress != null) {
            if (!localBackup.isEmpty()) {
                for (DataPacket p : localBackup) {
                    channel.send(p.getDataBuffer(), broadcastSocketAdress);
                }
                localBackup.clear();
            }
            channel.send(dataPacket.getDataBuffer(), broadcastSocketAdress);
        } else {
            localBackup.add(dataPacket.clone());
        }
    }

    @Override
    public void onPortSet(int port) {
        broadcastSocketAdress = new InetSocketAddress(broadcastAdress, port);
    }
}
