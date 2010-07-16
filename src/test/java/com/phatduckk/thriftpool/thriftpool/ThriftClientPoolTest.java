package com.phatduckk.thriftpool.thriftpool;

import static org.junit.Assert.*;

import com.phatduckk.thriftpool.thriftpool.testhelper.testthrift.HelperService;
import com.phatduckk.thriftpool.thriftpool.testhelper.testthrift.TestService;
import com.phatduckk.thriftpool.thriftpool.testhelper.testthrift.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
        // get a pool ready
        HostPort hp = new HostPort("localhost", HelperService.PORT);
        // factory() is overloaded a few way - check it out for other options...
        ThriftClientPool pool = ThriftClientPool.factory(TestService.Client.class, hp);

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
