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
package org.mobicents.media.controls.rest.core.controller;

import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.mobicents.media.controls.rest.core.factory.EndpointQueue;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointInstaller;
import org.mobicents.media.server.spi.ServerManager;
import org.mobicents.media.controls.rest.core.jaxrs.MediaControllerService;
import org.mobicents.media.server.spi.MediaServer;

/**
 *
 * @author Ayache
 */
public class Controller implements ServerManager {

    private final UndertowJaxrsServer jaxrsServer;
    private final Undertow.Builder undertow;
    private MediaServer server;

    public Controller(String jaxrsHost, String jaxrsPort) {
        ResteasyDeployment rd = new ResteasyDeployment();
        rd.getActualResourceClasses().add(MediaControllerService.class);
        jaxrsServer = new UndertowJaxrsServer();
        jaxrsServer.deploy(jaxrsServer.undertowDeployment(rd)
                .setDeploymentName("media-server-control")
                .setContextPath("/")
                .setClassLoader(Controller.class.getClassLoader()));
        undertow = Undertow.builder().addHttpListener(Integer.valueOf(jaxrsPort), jaxrsHost);

    }

    public void start() {
        jaxrsServer.start(undertow);
        server.addManager(this);
    }

    public void stop() {
        jaxrsServer.stop();
        server.removeManager(this);
    }

    public void onStarted(Endpoint endpoint, EndpointInstaller installer) {
        EndpointQueue.getInstance().offer(endpoint.getLocalName(), endpoint, installer);
    }

    public void onStopped(Endpoint endpoint) {

    }

}
