/*
 * JBoss, Home of Professional Open Source
 * Copyright XXXX, Red Hat Middleware LLC, and individual contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.media.server.impl.resource.dtmf;

import org.mobicents.media.server.spi.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.dtmf.DtmfEvent;

/**
 * 
 * @author kulikov
 */
public class DtmfEventImpl implements DtmfEvent {

    private DtmfDetector source;
    private int volume;
    private String tone;
    
    public DtmfEventImpl(DtmfDetector source, String tone, int volume) {
        this.source = source;
        this.volume = volume;
        this.tone = tone;
    }
    
    public int getVolume() {
        return volume;
    }
    

    public DtmfDetector getSource() {
        return source;
    }

    public String getTone() {
        return tone;
    }
}