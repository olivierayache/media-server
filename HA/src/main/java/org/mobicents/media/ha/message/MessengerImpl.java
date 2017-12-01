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

package org.mobicents.media.ha.message;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gridgain.grid.GridException;
import org.gridgain.grid.lang.GridBiPredicate;
import org.gridgain.grid.messaging.GridMessaging;


public abstract class MessengerImpl<Subject extends Serializable, SendBody extends Serializable, ReceiveBody extends Serializable> implements Messenger<Subject, SendBody, ReceiveBody> {

    private final GridMessaging messaging;

    
    private static class MessageHolder<Subject extends Serializable, Body extends Serializable> implements Serializable{
        private final Subject s;
        private final Body b;

        public MessageHolder(Subject s, Body b) {
            this.s = s;
            this.b = b;
        }
        
    }

    public MessengerImpl(GridMessaging messaging) {
        this.messaging = messaging;
    }
    
    public void sendMessage(Subject sub, SendBody body) {
        try {
            messaging.send(sub, body);
        } catch (GridException ex) {
            Logger.getLogger(MessengerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onMessageReceived(Subject sub, ReceiveBody body) {
        //Do nothing
    }

    public void startReceiving(final Subject sub) {
        messaging.localListen(sub, new GridBiPredicate<UUID, ReceiveBody>(){

            @Override
            public boolean apply(UUID e1, ReceiveBody b) {
                onMessageReceived(sub, b);
                return true;
            }
            
        });
    }    
}
