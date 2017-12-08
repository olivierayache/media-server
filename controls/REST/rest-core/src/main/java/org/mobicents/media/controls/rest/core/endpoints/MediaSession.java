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
package org.mobicents.media.controls.rest.core.endpoints;

import java.util.HashMap;
import java.util.Map;
import org.mobicents.media.controls.rest.api.EndpointType;
import org.mobicents.media.controls.rest.core.exceptions.InitFailedException;
import org.mobicents.media.controls.rest.core.factory.EndpointQueue;
import org.mobicents.media.server.spi.ConnectionType;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author Ayache
 */
public class MediaSession {
    
    private final String id;
    private final Endpoint cnfEndpoint;
    private final Map<String, MediaSessionMember> mediaSessionMembers = new HashMap<String, MediaSessionMember>();
    
    public MediaSession(String id) {
        this.id = id;
        cnfEndpoint = EndpointQueue.getInstance().poll(EndpointType.CNF);
    }
    
    public Endpoint getCnfEndpoint() {
        return cnfEndpoint;
    }
    
    public void addMember(MediaSessionMember member) {
        try {
            member.setLocalCnfDestConnection(cnfEndpoint.createConnection(ConnectionType.LOCAL, Boolean.FALSE));
            mediaSessionMembers.put(id, member);
        } catch (ResourceUnavailableException ex) {
            throw new InitFailedException(ex);
        }
    }
    
    public void removeMember(String id) {
        MediaSessionMember remove = mediaSessionMembers.remove(id);
        if (remove != null) {
            remove.destroy();
        }
    }
    
    public MediaSessionMember getMember(String id) {
        return mediaSessionMembers.get(id);
    }
    
    public void destroy() {
        EndpointQueue.getInstance().offer(cnfEndpoint.getLocalName(), cnfEndpoint);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MediaSession other = (MediaSession) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
    
}
