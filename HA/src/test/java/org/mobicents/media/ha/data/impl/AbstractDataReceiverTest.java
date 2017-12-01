/*
 * Copyright (C) 2014 TeleStax, Inc..
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
package org.mobicents.media.ha.data.impl;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.gridgain.grid.GridException;
import org.mobicents.media.ha.App;
import org.mobicents.media.ha.cache.CacheManager;
import org.mobicents.media.ha.data.ReadableDataPacket;

/**
 *
 * @author Ayache
 */
public class AbstractDataReceiverTest extends TestCase {

    static{
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.setCacheConf(App.class.getResource("/example-cache.xml").getPath());
        cacheManager.setCacheName("replicated");
        try {
            cacheManager.start();
        } catch (GridException ex) {
            Logger.getLogger(AbstractDataReceiverTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public AbstractDataReceiverTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of onChannelRegistered method, of class AbstractDataReceiver.
     */
    public void testOnChannelRegistered() throws Exception {
        System.out.println("onChannelRegistered");
        int size = 0;
        int[] ports = null;
        AbstractDataReceiver instance = new AbstractDataReceiverImpl();
        instance.onChannelRegistered(size, ports);
    }

    public class AbstractDataReceiverImpl extends AbstractDataReceiver {

        public AbstractDataReceiverImpl() throws Exception {
            super(InetAddress.getByName("228.10.10.157"),"lo", true);
        }

        @Override
        public void onReceive(ReadableDataPacket dataPacket) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
