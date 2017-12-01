package org.mobicents.media.ha;

import org.gridgain.grid.GridException;
import org.mobicents.media.ha.cache.CacheManager;

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
    }
}
