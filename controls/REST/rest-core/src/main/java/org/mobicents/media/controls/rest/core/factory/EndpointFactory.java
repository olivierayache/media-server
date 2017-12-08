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

import org.mobicents.media.controls.rest.api.EndpointType;

/**
 *
 * @author Ayache
 */
public class EndpointFactory {

    private static final EndpointQueue ENDPOINT_QUEUE = EndpointQueue.getInstance();

//    private final ConcurrentCyclicFIFO<FirstEndpoint> firstEndpoints;// TODO: OAY use pool of endpoints vs creation each time ???
//    private final ConcurrentCyclicFIFO<SecondEndpoint> secondEndpoints;// TODO: OAY use pool of endpoints vs creation each time ???
//
//    private HttpEndpointFactory() {
//        secondEndpoints = new ConcurrentCyclicFIFO<SecondEndpoint>();
//        firstEndpoints = new ConcurrentCyclicFIFO<FirstEndpoint>();
//
//    }

    public void setAnn(String ann) {
        ENDPOINT_QUEUE.addHolder(ann, EndpointType.ANN);
    }

    public void setBridge(String bridge) {
        ENDPOINT_QUEUE.addHolder(bridge, EndpointType.BRIDGE);
    }

    public void setIvr(String ivr) {
        ENDPOINT_QUEUE.addHolder(ivr, EndpointType.IVR);
    }

    public void setCnf(String cnf) {
        ENDPOINT_QUEUE.addHolder(cnf, EndpointType.CNF);
    }
    
    public void setPr(String pr) {
        ENDPOINT_QUEUE.addHolder(pr, EndpointType.PR);
    }

//    public FirstEndpoint createFirstEndpoint() {
//        return new FirstEndpoint(bridge, ivr, ENDPOINT_QUEUE);
//    }
//
//    public SecondEndpoint createSecondEndpoint() {
//        return new SecondEndpoint(ivr, cnf, ENDPOINT_QUEUE);
//    }

    public static EndpointFactory getInstance() {
        return EndpointFactoryHolder.INSTANCE;
    }

    private static class EndpointFactoryHolder {

        private static final EndpointFactory INSTANCE = new EndpointFactory();
    }
}
