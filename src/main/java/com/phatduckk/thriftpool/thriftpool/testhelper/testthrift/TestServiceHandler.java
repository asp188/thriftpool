package com.phatduckk.thriftpool.thriftpool.testhelper.testthrift;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: arin
 * Date: Apr 28, 2010
 * Time: 11:06:28 PM
 */


public class TestServiceHandler implements TestService.Iface {
    @Override
    public User getByID(int userID) throws TException {
        return new User(userID, "phatduckk");
    }

    @Override
    public List<User> getAll() throws TException {
        List users = new ArrayList();
        users.add(new User(666, "phatduckk"));
        users.add(new User(777, "bob"));
        return users;
    }

    @Override
    public boolean put(User user) throws TException {
        return true;
    }
}
