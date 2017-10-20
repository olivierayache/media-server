/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.mgcp.controller;

import java.io.IOException;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentType;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionType;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointState;
import org.mobicents.media.server.spi.MediaType;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.dsp.DspFactory;

import org.mobicents.media.server.scheduler.Scheduler;
import org.mobicents.media.server.scheduler.Clock;
import org.mobicents.media.server.scheduler.DefaultClock;
import org.mobicents.media.server.spi.BindingInformation;

/**
 *
 * @author yulian oifa
 */
public class ConfiguratorTest {
    
    private Configurator configurator;
    private Scheduler scheduler;
    private Clock clock;
    MyEndpoint endpoint;
    
    public ConfiguratorTest() {    	
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    	clock = new DefaultClock();
    	
    	scheduler = new Scheduler();
        scheduler.setClock(clock);
        scheduler.start();  
        
        configurator = new Configurator(getClass().getResourceAsStream("/mgcp-conf.xml"));
        endpoint = new MyEndpoint("mobicents/ivr/1",scheduler);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of activate method, of class Configurator.
     */
    @Test
    public void testActivate() throws Exception {      
    	MgcpEndpoint activity = configurator.activate(endpoint, null, "127.0.0.1", 9201);
        assertTrue(activity != null);    	    	
    }
    
    private class MyEndpoint implements Endpoint {

        private String name;
        private Scheduler scheduler;
        
        public MyEndpoint(String name,Scheduler scheduler) {
            this.name = name;
            this.scheduler=scheduler;
        }
        
        public String getLocalName() {
            return name;
        }

        public void setScheduler(Scheduler scheduler) {
        	throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public Scheduler getScheduler() {
            return scheduler;
        }
        
        public int getActiveConnectionsCount()
        {
        	return 0;
        }
        
        public EndpointState getState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Collection<MediaType> getMediaTypes() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void start() throws ResourceUnavailableException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Connection createConnection(ConnectionType type,Boolean isLocal) throws ResourceUnavailableException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void deleteConnection(Connection connection) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public void deleteConnection(Connection connection,ConnectionType type) {
            
        }

        public void deleteAllConnections() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void configure(boolean isALaw) {
        	throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public String describe(MediaType mediaType) throws ResourceUnavailableException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setDspFactory(DspFactory dspFactory) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void modeUpdated(ConnectionMode oldMode,ConnectionMode newMode) {    
        }
        
        public Component getResource(MediaType mediaType, ComponentType componentType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    
        public boolean hasResource(MediaType mediaType, ComponentType componentType) {
        	return false;
        }
        
        public void releaseResource(MediaType mediaType, ComponentType componentType) {    
        }

        public Connection createConnection(ConnectionType type, Boolean isLocal, BindingInformation... informations) throws ResourceUnavailableException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
