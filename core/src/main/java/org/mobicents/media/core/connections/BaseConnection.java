/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.media.core.connections;

import org.apache.log4j.Logger;
import org.mobicents.media.server.*;
import java.util.Collection;
import java.io.IOException;

import org.mobicents.media.CheckPoint;
import org.mobicents.media.server.utils.Text;

import org.mobicents.media.server.component.audio.AudioComponent;
import org.mobicents.media.server.component.oob.OOBComponent;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormat;
import org.mobicents.media.server.impl.rtp.sdp.RTPFormats;
import org.mobicents.media.server.impl.rtp.sdp.SessionDescription;
import org.mobicents.media.server.scheduler.Scheduler;
import org.mobicents.media.server.scheduler.Task;
import org.mobicents.media.server.spi.BindingInformation;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionEvent;
import org.mobicents.media.server.spi.ConnectionType;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.ConnectionFailureListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaType;
import org.mobicents.media.server.spi.ModeNotSupportedException;
import org.mobicents.media.server.spi.dsp.DspFactory;
import org.mobicents.media.server.spi.format.Format;
import org.mobicents.media.server.spi.format.Formats;
import org.mobicents.media.server.spi.listener.Listeners;
import org.mobicents.media.server.spi.listener.TooManyListenersException;
/**
 * Implements connection's FSM.
 *
 * @author Oifa Yulian
 */
public abstract class BaseConnection implements Connection {

	private int id;
	
    //Identifier of this connection
    private String textualId;

    //scheduler instance
    private Scheduler scheduler;

    /** FSM current state */
    private volatile ConnectionState state = ConnectionState.NULL;
    private final Object stateMonitor = new Integer(0);

    //connection event listeners
    private Listeners<ConnectionListener> listeners = new Listeners();

    //events
    private ConnectionEvent stateEvent;

    /** Remaining time to live in current state */
    private volatile long ttl;
    private HeartBeat heartBeat;

    private Endpoint activeEndpoint;
    
    private ConnectionMode connectionMode=ConnectionMode.INACTIVE;
    private static final Logger logger = Logger.getLogger(BaseConnection.class);
    
    /**
     * Creates basic connection implementation.
     *
     * @param id the unique identifier of this connection within endpoint.
     * @param endpoint the endpoint owner of this connection.
     */
    public BaseConnection(int id,Scheduler scheduler) {
        this.id = id;
        this.textualId=Integer.toHexString(id);
        
        this.scheduler = scheduler;
        
        heartBeat = new HeartBeat();

        //initialize event objects
        this.stateEvent = new ConnectionEventImpl(ConnectionEvent.STATE_CHANGE, this);
    }
        
    public abstract AudioComponent getAudioComponent();
    
    public abstract OOBComponent getOOBComponent();
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#getId()
     */
    public int getId() {
        return id;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#getTextualId()
     */
    public String getTextualId() {
        return textualId;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#getState()
     */
    public ConnectionState getState() {
        synchronized(stateMonitor) {
            return state;
        }
    }

    /**
     * Modifies state of the connection.
     *
     * @param state the new value for the state.
     */
    private void setState(ConnectionState state) {
        //change state
        this.state = state;
        this.ttl = state.getTimeout()*10 + 1;
        
        switch (state) {
            case HALF_OPEN:
            	//heartBeat.setDeadLine(scheduler.getClock().getTime() + 1000000000L);
                scheduler.submitHeatbeat(heartBeat);
                break;
            case NULL:
                heartBeat.cancel();
                break;
        }

        //notify listeners
        try {
            listeners.dispatch(stateEvent);
        } catch (Exception e) {
        	logger.error(e);
        }
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#getDescriptor()
     */
    public String getDescriptor() {
        return null;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#getEndpoint() 
     */
    public void setEndpoint(Endpoint endpoint) {
        this.activeEndpoint=endpoint;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#getEndpoint() 
     */
    public Endpoint getEndpoint() {
        return this.activeEndpoint;
    }    

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#addListener(org.mobicents.media.server.spi.ConnectionListener)
     */
    public void addListener(ConnectionListener listener) {
        try {
            listeners.add(listener);
        } catch (TooManyListenersException e) {
        	logger.error(e);
        }
    }


    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Connection#removeListener(org.mobicents.media.server.spi.ConnectionListener)
     */
    public void removeListener(ConnectionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Initiates transition from NULL to HALF_OPEN state.
     */
    public void bind(BindingInformation... informations) throws Exception {
        synchronized (stateMonitor) {
            //check current state
            if (this.state != ConnectionState.NULL) {
                throw new IllegalStateException("Connection already bound");
            }

            //execute call back
            if (informations != null && informations.length != 0){
                this.onCreated(informations[0]);
            }else{
                this.onCreated(null);
            }

            //update state
            setState(ConnectionState.HALF_OPEN);
        }
    }

    /**
     * Initiates transition from HALF_OPEN to OPEN state.
     */
    public void join() throws Exception {
        synchronized (stateMonitor) {
            if (this.state == ConnectionState.NULL) {
                throw new IllegalStateException("Connection not bound yet");
            }

            if (this.state == ConnectionState.OPEN) {
                throw new IllegalStateException("Connection opened already");
            }

            //execute callback
            this.onOpened();

            //update state
            setState(ConnectionState.OPEN);
        }
    }

    /**
     * Initiates transition from any state to state NULL.
     */
    public void close() {
        synchronized (stateMonitor) {
            if (this.state != ConnectionState.NULL) {
                this.onClosed();
                setState(ConnectionState.NULL);
            }
        }
    }

    private void fail() {
        synchronized (stateMonitor) {
            if (this.state != ConnectionState.NULL) {
                this.onFailed();
                setState(ConnectionState.NULL);
            }
        }
    }
    
    /**
     * Gets the current mode of this connection.
     *
     * @return integer constant indicating mode.
     */
    public ConnectionMode getMode()
    {
    	return connectionMode;
    }

    /**
     * Modify mode of this connection for all known media types.
     * 
     * @param mode the new mode of the connection.
     */
    public void setMode(ConnectionMode mode) throws ModeNotSupportedException
    {
    	if(this.activeEndpoint!=null)
    		this.activeEndpoint.modeUpdated(connectionMode,mode);
    	
    	this.connectionMode=mode;
    }
    
    /**
     * Sets connection failure listener.
     * 
     *
     */
    public abstract void setConnectionFailureListener(ConnectionFailureListener connectionFailureListener);
    
    /**
     * Called when connection created.
     */
    protected abstract void onCreated() throws Exception;
    
    /**
     * Called when connection created.
     * @param information
     * @throws Exception 
     */
    protected abstract void onCreated(BindingInformation information) throws Exception;

    /**
     * Called when connected moved to OPEN state.
     * @throws Exception
     */
    protected abstract void onOpened() throws Exception;

    /**
     * Called when connection is moving from OPEN state to NULL state.
     */
    protected abstract void onClosed();

    /**
     * Called if failure has bean detected during transition.
     */
    protected abstract void onFailed();    
    
    /**
     * Joins endpoint wich executes this connection with other party.
     *
     * @param other the connection executed by other party endpoint.
     * @throws IOException
     */
    public abstract void setOtherParty(Connection other) throws IOException;

    /**
     * Joins endpoint which executes this connection with other party.
     *
     * @param descriptor the SDP descriptor of the other party.
     * @throws IOException
     */
    public abstract void setOtherParty(byte[] descriptor) throws IOException;

    /**
     * Joins endpoint which executes this connection with other party.
     *
     * @param descriptor the SDP descriptor of the other party.
     * @throws IOException
     */
    public abstract void setOtherParty(Text descriptor) throws IOException;
    
    /**
     * Gets supported formats for the specified media type.
     *
     * @param mt the media type
     * @return the list of formats supported by endpoint.
     */
    private Collection<Format> getFormats(MediaType mt) {
        return null;
    }    

    /**
     * Gets whether connection should be bound to local or remote interface , supported only for rtp connections.
     *
     * @return boolean value
     */
    public boolean getIsLocal()
    {
    	return false;
    }
    
    /**
     * Gets whether connection should be bound to local or remote interface , supported only for rtp connections.
     *
     * @return boolean value
     */
    public void setIsLocal(boolean isLocal)
    {
    	//do nothing
    }
    
    private class HeartBeat extends Task {

        public HeartBeat() {
            super();
        }        

        public int getQueueNumber()
        {
        	return scheduler.HEARTBEAT_QUEUE;
        }   
        
        @Override
        public long perform() {
                synchronized(stateMonitor) {
                	ttl--;
                    if (ttl == 0) {
                        //setState(ConnectionState.NULL);
                        fail();
                    } else {
                        //setDeadLine(scheduler.getClock().getTime() +  1000000000L);
                        scheduler.submitHeatbeat(this);
                    }
                }            
            return 0;
        }
    }  
    
    protected void releaseConnection(ConnectionType connectionType)
    {
    	if(this.activeEndpoint!=null)
    		this.activeEndpoint.deleteConnection(this,connectionType);
    	
    	this.activeEndpoint=null;
    }
}
