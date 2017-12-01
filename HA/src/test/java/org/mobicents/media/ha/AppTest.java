package org.mobicents.media.ha;

import java.io.Serializable;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.mobicents.media.ha.cache.Cache;
import org.mobicents.media.ha.cache.CacheManager;
import org.mobicents.media.ha.cache.Closure;
import org.mobicents.media.ha.message.SimpleMessenger;
import org.mobicents.media.ha.message.SimpleMessengerImpl;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    static class ClosTest implements Closure<TestClass, TestClass>, Serializable {

        public TestClass apply(TestClass e) {
            e.add("fffrgt");
            return e;
        }

    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws GridException {
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.setCacheConf(AppTest.class.getResource("/example-cache.xml").getPath());
        cacheManager.setCacheName("replicated");
        cacheManager.start();
        Cache<String, TestClass> c = cacheManager.getCache();

        c.addEntry("test", new TestClass("rfrrrh"));
        c.editEntry("test", new ClosTest());
        c.removeEntry("test");
        
        SimpleMessenger<String, String> messenger = new SimpleMessengerImpl<String, String>(GridGain.grid().message()) {

            @Override
            public void onMessageReceived(String id, String body) {
                assertEquals("coucou",id);
                assertEquals("grr", body);
            }
        };
        messenger.startReceiving("coucou");
        messenger.sendMessage("coucou", "grr");
        assertTrue(true);
    }
}
