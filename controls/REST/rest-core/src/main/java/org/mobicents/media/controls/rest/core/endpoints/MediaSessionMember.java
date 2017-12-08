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

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentType;
import org.mobicents.media.controls.rest.api.EndpointType;
import org.mobicents.media.controls.rest.api.dto.MediaSessionMemberDto;
import org.mobicents.media.controls.rest.core.exceptions.InitFailedException;
import org.mobicents.media.controls.rest.core.factory.EndpointQueue;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionType;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaType;
import org.mobicents.media.server.spi.ModeNotSupportedException;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.player.Player;
import org.mobicents.media.server.spi.recorder.Recorder;

/**
 *
 * @author Ayache
 */
public class MediaSessionMember {

    private Connection remoteConnection;
    private Connection localCnfConnection;
    private Connection localIvrConnection;
    private Connection localCnfDestConnection;
    private MediaSessionMemberDto.MemberType type;
    private final Map<ComponentType, Component> resources = new EnumMap<ComponentType, Component>(ComponentType.class);

    public void init(MediaSessionMemberDto memberDto) throws InitFailedException {
        try {
            Endpoint bridgeEndpoint = EndpointQueue.getInstance().poll(EndpointType.BRIDGE);
            try {
                remoteConnection = bridgeEndpoint.createConnection(ConnectionType.RTP, Boolean.FALSE);
                memberDto.setLocalSdp(remoteConnection.getDescriptor());
            } catch (ResourceUnavailableException ex) {
                destroyConnection(remoteConnection, ConnectionType.RTP, false);
                throw new InitFailedException(ex);
            }

//        if (memberDto.getType() == MediaSessionMemberDto.MemberType.EMITER || memberDto.getType() == MediaSessionMemberDto.MemberType.BOTH) {
//            Endpoint cnfEndpoint = EndpointQueue.getInstance().poll(EndpointType.CNF);
//            localCnfConnection.setOtherParty(cnfEndpoint.createConnection(ConnectionType.LOCAL, Boolean.TRUE));
//        }
            initResources(memberDto, bridgeEndpoint);

            type = memberDto.getType();
            switch (memberDto.getType()) {
                case EMITER:
                    remoteConnection.setMode(ConnectionMode.RECV_ONLY);
                    break;
                case RECEIVER:
                    remoteConnection.setMode(ConnectionMode.SEND_ONLY);
                    break;
                case BOTH:
                    remoteConnection.setMode(ConnectionMode.SEND_RECV);
                    break;
            }
        } catch (ModeNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }

    }

    private void initResources(MediaSessionMemberDto memberDto, Endpoint bridgeEndpoint) throws InitFailedException, ModeNotSupportedException {
        Endpoint resourceEndpoint = null;

        if (memberDto.isRecorderNeeded() || (memberDto.isDtmfDetectorNeeded() && memberDto.isAnnouncementNeeded())) {
            resourceEndpoint = EndpointQueue.getInstance().poll(EndpointType.IVR);
        } else if (memberDto.isAnnouncementNeeded()) {
            resourceEndpoint = EndpointQueue.getInstance().poll(EndpointType.ANN);
        }

        if (resourceEndpoint != null) {
            if (memberDto.isRecorderNeeded()) {
                resources.put(ComponentType.RECORDER, (Recorder) resourceEndpoint.getResource(MediaType.AUDIO, ComponentType.RECORDER));
            }
            if (memberDto.isAnnouncementNeeded()) {
                resources.put(ComponentType.PLAYER, (Player) resourceEndpoint.getResource(MediaType.AUDIO, ComponentType.PLAYER));
            }
            if (memberDto.isDtmfDetectorNeeded()) {
                resources.put(ComponentType.DTMF_DETECTOR, (DtmfDetector) resourceEndpoint.getResource(MediaType.AUDIO, ComponentType.DTMF_DETECTOR));
            }
            try {
                localIvrConnection = bridgeEndpoint.createConnection(ConnectionType.LOCAL, Boolean.TRUE);
                localIvrConnection.setOtherParty(resourceEndpoint.createConnection(ConnectionType.LOCAL, Boolean.TRUE));
            } catch (ResourceUnavailableException ex) {
                destroyConnection(localIvrConnection, ConnectionType.LOCAL, false);
                throw new InitFailedException(ex);
            } catch (IOException ex) {
                destroyConnection(localIvrConnection, ConnectionType.LOCAL, false);
                throw new InitFailedException(ex);
            }
            localIvrConnection.setMode(ConnectionMode.SEND_RECV);
        }
    }

    public void setLocalCnfDestConnection(Connection localCnfDestConnection) {
        this.localCnfDestConnection = localCnfDestConnection;
    }

    public Connection getLocalCnfDestConnection() {
        return localCnfDestConnection;
    }

    public void connectToRemote(byte[] remoteSdp) throws IOException {
        remoteConnection.setOtherParty(remoteSdp);
    }

    public void disconnectFromRemote() throws IOException {
        destroyConnection(remoteConnection, ConnectionType.RTP, false);
    }

    public void connectToPeer() throws IOException {
        Component get = resources.get(ComponentType.PLAYER);
        if (get != null){
            get.deactivate();
        }
        try {
            localCnfConnection = remoteConnection.getEndpoint().createConnection(ConnectionType.LOCAL, Boolean.TRUE);
            localCnfConnection.setOtherParty(localCnfDestConnection);
            switch (type) {
                case EMITER:
                    localCnfConnection.setMode(ConnectionMode.SEND_ONLY);
                    break;
                case RECEIVER:
                    localCnfConnection.setMode(ConnectionMode.RECV_ONLY);
                    break;
                case BOTH:
                    localCnfConnection.setMode(ConnectionMode.SEND_RECV);
                    break;
            }
        } catch (ModeNotSupportedException ex) {
            throw new IllegalStateException(ex);
        } catch (ResourceUnavailableException ex) {
            System.out.println(remoteConnection.getEndpoint());
            throw new IOException(ex);
        }
    }

    public void disconnectFromPeer() {
        destroyConnection(localCnfDestConnection, ConnectionType.LOCAL, false);
        destroyConnection(localCnfConnection, ConnectionType.LOCAL, false);
    }

    public Map<ComponentType, Component> getResources() {
        return resources;
    }

    public void destroy() {
        destroyConnection(remoteConnection, ConnectionType.RTP, true);
        destroyConnection(localCnfConnection, ConnectionType.LOCAL, true);
        destroyConnection(localCnfDestConnection, ConnectionType.LOCAL, false);
        destroyConnection(localIvrConnection, ConnectionType.LOCAL, true);
    }

    private void destroyConnection(Connection connection, ConnectionType type, boolean releaseEndpoint) {
        if (connection != null) {
            Endpoint endpoint = connection.getEndpoint();
            endpoint.deleteConnection(connection);
            if (releaseEndpoint) {
                if (endpoint.getActiveConnectionsCount() == 0) {
                    EndpointQueue.getInstance().offer(endpoint.getLocalName(), endpoint);
                }
            }
        }
    }
}
