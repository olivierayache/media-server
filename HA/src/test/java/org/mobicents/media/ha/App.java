package org.mobicents.media.ha;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.messaging.GridMessaging;
import org.mobicents.media.ha.cache.CacheManager;
import org.mobicents.media.ha.data.impl.DataBroadcasterFactory;
import org.mobicents.media.ha.data.impl.PortsHolder;
import org.mobicents.media.ha.message.Messenger;
import org.mobicents.media.ha.message.MessengerImpl;
import org.mobicents.media.ha.message.SimpleMessenger;
import org.mobicents.media.ha.message.SimpleMessengerImpl;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws GridException {
        System.out.println("Hello World!");
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.setCacheConf(App.class.getResource("/example-cache.xml").getPath());
        cacheManager.setCacheName("replicated");
        cacheManager.start();
        final GridMessaging message = GridGain.grid().message();

        Messenger<String, String, PortsHolder> messenger = new MessengerImpl<String, String, PortsHolder>(message) {

            public void onMessageReceived(String id, PortsHolder body) {
                System.out.println(body);
            }
        };

        messenger.startReceiving("BR_HA");
    }
}
