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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import org.jboss.util.collection.Iterators;
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

    private static final int remoteConnection = 0;
    private static final int localCnfConnection = 1;
    private static final int localIvrConnection = 2;
    private static final int localCnfDestConnection = 3;
    private final Connection[] connections = new Connection[4];
    private final Component[] resources = new Component[ComponentType.values().length];
    private MediaSessionMemberDto.MemberType type;

    public void init(MediaSessionMemberDto memberDto) throws InitFailedException {
        try {
            Endpoint bridgeEndpoint = EndpointQueue.getInstance().poll(EndpointType.BRIDGE);
            try {
                connections[remoteConnection] = bridgeEndpoint.createConnection(ConnectionType.RTP, Boolean.FALSE);
                memberDto.setLocalSdp(connections[remoteConnection].getDescriptor());
            } catch (ResourceUnavailableException ex) {
                destroyConnection(remoteConnection, false);
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
                    connections[remoteConnection].setMode(ConnectionMode.RECV_ONLY);
                    break;
                case RECEIVER:
                    connections[remoteConnection].setMode(ConnectionMode.SEND_ONLY);
                    break;
                case BOTH:
                    connections[remoteConnection].setMode(ConnectionMode.SEND_RECV);
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
                resources[ComponentType.RECORDER.getType()] = (Recorder) resourceEndpoint.getResource(MediaType.AUDIO, ComponentType.RECORDER);
            }
            if (memberDto.isAnnouncementNeeded()) {
                resources[ComponentType.PLAYER.getType()] = (Player) resourceEndpoint.getResource(MediaType.AUDIO, ComponentType.PLAYER);
            }
            if (memberDto.isDtmfDetectorNeeded()) {
                resources[ComponentType.DTMF_DETECTOR.getType()] = (DtmfDetector) resourceEndpoint.getResource(MediaType.AUDIO, ComponentType.DTMF_DETECTOR);
            }
            try {
                connections[localIvrConnection] = bridgeEndpoint.createConnection(ConnectionType.LOCAL, Boolean.TRUE);
                connections[localIvrConnection].setOtherParty(resourceEndpoint.createConnection(ConnectionType.LOCAL, Boolean.TRUE));
            } catch (ResourceUnavailableException ex) {
                destroyConnection(localIvrConnection, false);
                throw new InitFailedException(ex);
            } catch (IOException ex) {
                destroyConnection(localIvrConnection, false);
                throw new InitFailedException(ex);
            }
            connections[localIvrConnection].setMode(ConnectionMode.SEND_RECV);
        }
    }

    public void setLocalCnfDestConnection(Connection localCnfDestConnection) {
        connections[this.localCnfDestConnection] = localCnfDestConnection;
    }

    public Connection getLocalCnfDestConnection() {
        return connections[localCnfDestConnection];
    }

    public void connectToRemote(byte[] remoteSdp) throws IOException {
        connections[remoteConnection].setOtherParty(remoteSdp);
    }

    public void disconnectFromRemote() throws IOException {
        destroyConnection(remoteConnection, false);
    }

    public void connectToPeer() throws IOException {
        Player get = (Player) resources[ComponentType.PLAYER.getType()];
        if (get != null && get.isStarted()) {
            get.deactivate();
        }
        try {
            connections[localCnfConnection] = connections[remoteConnection].getEndpoint().createConnection(ConnectionType.LOCAL, Boolean.TRUE);
            connections[localCnfConnection].setOtherParty(connections[localCnfDestConnection]);
            switch (type) {
                case EMITER:
                    connections[localCnfConnection].setMode(ConnectionMode.SEND_ONLY);
                    break;
                case RECEIVER:
                    connections[localCnfConnection].setMode(ConnectionMode.RECV_ONLY);
                    break;
                case BOTH:
                    connections[localCnfConnection].setMode(ConnectionMode.SEND_RECV);
                    break;
            }
        } catch (ModeNotSupportedException ex) {
            throw new IllegalStateException(ex);
        } catch (ResourceUnavailableException ex) {
            throw new IOException(ex);
        }
    }

    public void disconnectFromPeer() {
        destroyConnection(localCnfDestConnection, false);
        destroyConnection(localCnfConnection, false);
    }

    public Component getResource(ComponentType type) {
        return resources[type.getType()];
    }

    public void destroy() {
        for (Component c : resources) {
            if (c != null) {
                c.deactivate();
            }
        }
        destroyConnection(remoteConnection, true);
        destroyConnection(localCnfConnection, true);
        destroyConnection(localCnfDestConnection, false);
        destroyConnection(localIvrConnection, true);
    }

    private void destroyConnection(int connection, boolean releaseEndpoint) {
        if (connections[connection] != null) {
            Endpoint endpoint = connections[connection].getEndpoint();
            endpoint.deleteConnection(connections[connection]);
            connections[connection] = null;
            if (releaseEndpoint) {
                if (endpoint.getActiveConnectionsCount() == 0) {
                    EndpointQueue.getInstance().offer(endpoint.getLocalName(), endpoint);
                }
            }
        }
    }
}
