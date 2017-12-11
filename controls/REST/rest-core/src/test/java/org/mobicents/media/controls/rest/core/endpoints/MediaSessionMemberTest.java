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

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.ComponentType;
import org.mobicents.media.controls.rest.api.EndpointType;
import org.mobicents.media.controls.rest.api.dto.MediaSessionMemberDto;
import org.mobicents.media.controls.rest.core.factory.EndpointFactory;
import org.mobicents.media.controls.rest.core.factory.EndpointQueue;
import org.mobicents.media.core.ResourcesPool;
import org.mobicents.media.core.Server;
import org.mobicents.media.core.endpoints.VirtualEndpointInstaller;
import org.mobicents.media.core.endpoints.impl.BridgeEndpoint;
import org.mobicents.media.core.endpoints.impl.ConferenceEndpoint;
import org.mobicents.media.core.endpoints.impl.IvrEndpoint;
import org.mobicents.media.server.component.DspFactoryImpl;
import org.mobicents.media.server.impl.rtp.ChannelsManager;
import org.mobicents.media.server.io.network.UdpManager;
import org.mobicents.media.server.scheduler.DefaultClock;
import org.mobicents.media.server.scheduler.Scheduler;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionType;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointInstaller;
import org.mobicents.media.server.spi.ServerManager;
import org.mobicents.media.server.spi.recorder.Recorder;

/**
 *
 * @author Ayache
 */
public class MediaSessionMemberTest {

    public MediaSessionMemberTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class MediaSessionMember.
     */
    @Test
    public void testInit() throws Exception, Throwable {
        System.setProperty("org.apache.logging.log4j.level", "DEBUG");
        System.out.println("init");

        Server server = new Server();
        ServerManager manager = new ServerManager() {
            public void onStarted(Endpoint endpoint, EndpointInstaller installer) {
                EndpointQueue.getInstance().offer(endpoint.getLocalName(), endpoint, installer);
                System.out.println(endpoint);
            }

            public void onStopped(Endpoint endpoint) {
            }
        };

        EndpointFactory.getInstance().setBridge("/mobicents/bridge/$");
        EndpointFactory.getInstance().setIvr("/mobicents/ivr/$");
        EndpointFactory.getInstance().setCnf("/mobicents/cnf/$");

        server.addManager(manager);
        Scheduler scheduler = new Scheduler();
        DefaultClock defaultClock = new DefaultClock();
        scheduler.setClock(defaultClock);
        server.setClock(defaultClock);
        server.setScheduler(scheduler);
        ChannelsManager channelsManager = new ChannelsManager(new UdpManager(scheduler));
        channelsManager.setScheduler(scheduler);
        server.setResourcesPool(new ResourcesPool(scheduler, channelsManager, new DspFactoryImpl()));

        VirtualEndpointInstaller virtualEndpointInstaller = new VirtualEndpointInstaller();
        virtualEndpointInstaller.setServer(server);
        virtualEndpointInstaller.setEndpointClass(BridgeEndpoint.class.getName());
        virtualEndpointInstaller.setNamePattern("/mobicents/bridge/");
        virtualEndpointInstaller.setInitialSize(5);
        virtualEndpointInstaller.install();

        virtualEndpointInstaller = new VirtualEndpointInstaller();
        virtualEndpointInstaller.setServer(server);
        virtualEndpointInstaller.setEndpointClass(IvrEndpoint.class.getName());
        virtualEndpointInstaller.setNamePattern("/mobicents/ivr/");
        virtualEndpointInstaller.setInitialSize(5);
        virtualEndpointInstaller.install();

        virtualEndpointInstaller = new VirtualEndpointInstaller();
        virtualEndpointInstaller.setServer(server);
        virtualEndpointInstaller.setEndpointClass(ConferenceEndpoint.class.getName());
        virtualEndpointInstaller.setNamePattern("/mobicents/cnf/");
        virtualEndpointInstaller.setInitialSize(5);
        virtualEndpointInstaller.install();

        MediaSessionMemberDto memberDto = new MediaSessionMemberDto("Test", true, true, true, null, MediaSessionMemberDto.MemberType.RECEIVER);
        MediaSessionMember instance = new MediaSessionMember();
        instance.init(memberDto);
        Endpoint poll = EndpointQueue.getInstance().poll(EndpointType.CNF);
        System.out.println(poll);
        Connection createConnection = poll.createConnection(ConnectionType.LOCAL, Boolean.TRUE);
        instance.setLocalCnfDestConnection(createConnection);
        instance.connectToPeer();
        instance.disconnectFromPeer();
        System.out.println(poll.getActiveConnectionsCount());
        Connection createConnection2 = poll.createConnection(ConnectionType.LOCAL, Boolean.TRUE);
        instance.setLocalCnfDestConnection(createConnection2);
        instance.connectToPeer();
        Recorder get = (Recorder) instance.getResource(ComponentType.RECORDER);
        String toURI = File.createTempFile("audio",".wav").getCanonicalPath();
        get.setRecordDir(Files.createTempDirectory("mms").toString());
        get.setRecordFile("audio.wav", true);
        get.activate();
//        Thread.sleep(10000);
        
        instance.disconnectFromPeer();
        System.out.println(poll.getActiveConnectionsCount());
        
        instance.destroy();

    }

}
