package com.phatduckk.thriftpool;

import com.phatduckk.thriftpool.testhelper.testthrift.HelperService;
import com.phatduckk.thriftpool.testhelper.testthrift.TestService;
import com.phatduckk.thriftpool.testhelper.testthrift.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: arin
 * Date: Apr 28, 2010
 * Time: 10:47:58 PM
 */


public class ThriftClientPoolTest {
    private static HelperService helperService;

    @BeforeClass
    public static void setUp() throws Exception {
        helperService = new HelperService();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        helperService.stop();
    }

    @Test
    public void testBasic() throws Exception {
        // where's the thrift server at???
        HostPort hp = new HostPort(HelperService.HOST_PUBLIC, HelperService.PORT);

        // factory() is overloaded a few way - check it out for other options...
        ThriftClientOptions options = new ThriftClientOptions();
        options.setThriftClientClass(TestService.Client.class);
        options.addHostPort(hp);
        ThriftClientPool pool = ThriftClientPool.factory(options);

        // get a client from the pool (they're wrapped in a WrappedClient object
        WrappedClient wrappedClient = pool.borrowObject();
        // get the actual thrift client from the wrapped client
        TestService.Client thriftClient = (TestService.Client) wrappedClient.getThrift();

        User user = thriftClient.getByID(1);
        System.out.println(user);
        assertEquals(1, user.getUserID());

        // return the [wrapped] client back to the pool
        pool.returnObject(wrappedClient);
    }
}
