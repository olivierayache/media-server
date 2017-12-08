/*
 * Copyright (C) 2017 TeleStax, Inc..
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
package org.mobicents.media.controls.rest.core.factory;

import java.util.ArrayList;
import java.util.List;
import org.mobicents.media.controls.rest.api.EndpointType;
import org.mobicents.media.server.concurrent.ConcurrentCyclicFIFO;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointInstaller;

/**
 *
 * @author Ayache
 */
public class EndpointQueue {

    private ConcurrentCyclicFIFO<Endpoint>[] concurrentCyclicFIFOs;
    private final Object installerLock = new Object();
    private final List<Holder> list = new ArrayList<Holder>(3);
    private Holder[] holders;

    private EndpointQueue() {

    }

    private boolean match(String name1, String name2) {
        String[] tokens1 = name1.split("/");
        String[] tokens2 = name2.split("/");
        
        if (tokens1.length != tokens2.length) {
            return false;
        }
        
        for (int i = 0; i < tokens1.length; i++) {
            if (tokens1[i].equals("$") || tokens2[i].equals("$")) {
                continue;
            }
            
            if (!tokens1[i].equalsIgnoreCase(tokens2[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    public void setHolders(Holder[] holders) {
        this.holders = holders;
        concurrentCyclicFIFOs = new ConcurrentCyclicFIFO[holders.length];
        for (int i = 0; i < concurrentCyclicFIFOs.length; i++) {
            concurrentCyclicFIFOs[i] = new ConcurrentCyclicFIFO<Endpoint>();
        }
    }

    public void offer(String localname, Endpoint endpoint, EndpointInstaller installer) {
        for (int i = 0; i < holders.length; i++) {
            if (match(localname, holders[i].localName)) {
                holders[i].installer = installer == null ? holders[i].installer : installer;
                concurrentCyclicFIFOs[i].offer(endpoint);
            }
        }
    }

    public void offer(String localname, Endpoint endpoint) {
        offer(localname, endpoint, null);
    }

    public Endpoint poll(EndpointType type) {
        for (int i = 0; i < holders.length; i++) {
            if (type == holders[i].type) {
                Endpoint edp = concurrentCyclicFIFOs[i].poll();
                while (edp == null) {
                    synchronized (installerLock) {
                        holders[i].installer.newEndpoint();
                    }
                    edp = concurrentCyclicFIFOs[i].poll();
                }
                return edp;
            }
        }
        return null;// TODO: OAY throw exception would be better !!! 
    }

    void addHolder(String holder, EndpointType type) {
        final Holder holder1 = new Holder();
        holder1.localName = holder;
        holder1.type = type;
        list.add(holder1);
        holders = list.toArray(new Holder[0]);
        concurrentCyclicFIFOs = new ConcurrentCyclicFIFO[holders.length];
        for (int i = 0; i < concurrentCyclicFIFOs.length; i++) {
            concurrentCyclicFIFOs[i] = new ConcurrentCyclicFIFO<Endpoint>();
        }
    }

    public static class Holder {

        String localName;
        EndpointInstaller installer;
        EndpointType type;

        public void setLocalName(String localName) {
            this.localName = localName;
        }

        public void setInstaller(EndpointInstaller installer) {
            this.installer = installer;
        }
    }

    public static EndpointQueue getInstance() {
        return EndpointQueueHolder.INSTANCE;
    }

    private static class EndpointQueueHolder {

        private static final EndpointQueue INSTANCE = new EndpointQueue();
    }
}
