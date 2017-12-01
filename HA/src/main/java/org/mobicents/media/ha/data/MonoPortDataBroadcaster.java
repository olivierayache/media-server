///*
// * Copyright (C) 2014 TeleStax, Inc..
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 2.1 of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
// * MA 02110-1301  USA
// */
//package org.mobicents.media.ha.data;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//
//public class MonoPortDataBroadcaster extends DataBroadcaster {
//
//    private static final int PORT = 9002;
//    private final BlockingQueue<DataPacket> dataPackets = new ArrayBlockingQueue<DataPacket>(2000);
//
//    public MonoPortDataBroadcaster(InetAddress groupAddress) throws IOException {
//        super(groupAddress);
////        new Thread(new Runnable() {
////
////            public void run() {
////                try {
////                    while (true) {
////                        DataPacket packet = dataPackets.take();
////                        send(packet);
////                    }
////                } catch (InterruptedException ex) {
////                    Logger.getLogger(MonoPortDataBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
////                } catch (IOException ex) {
////                    Logger.getLogger(MonoPortDataBroadcaster.class.getName()).log(Level.SEVERE, null, ex);
////                }
////            }
////        }, "data-packet-consumer-worker").start();
//    }
//
////    public boolean enqueuePacket(DataPacket packet){
////       return dataPackets.offer(packet);
////    }
//    
//    @Override
//    protected SocketAddress initBroadcastAddress() {
//        return new InetSocketAddress(broadcastAdress, PORT);
//    }
//
//}
