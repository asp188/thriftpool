package com.phatduckk.thriftpool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Arin Sarkissian
 * Date: Feb 26, 2010
 * Time: 3:28:41 PM
 */

public class ThriftClientPool extends GenericObjectPool {
    public static final boolean DEFAULT_TEST_ON_BORROW = false;

    public ThriftClientPool(PoolableObjectFactory objFactory) {
        super(objFactory);
        setupClientOptions();
    }

    public ThriftClientPool(PoolableObjectFactory factory, Config config) {
        super(factory, config);
    }

    protected void setupClientOptions() {
        this.setMaxIdle(DEFAULT_MAX_IDLE);
        this.setMaxActive(DEFAULT_MAX_ACTIVE);
        this.setMinEvictableIdleTimeMillis(DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        this.setTestOnBorrow(DEFAULT_TEST_ON_BORROW);
        this.setMaxWait(DEFAULT_MAX_WAIT);
    }

    public WrappedClient borrowObject() throws Exception {
        WrappedClient wrappedClient = (WrappedClient) super.borrowObject();
        wrappedClient.setPool(this);

        return wrappedClient;
    }

    public void returnObject(Object obj) throws Exception {
        WrappedClient wrapped = (WrappedClient) obj;
        Object thriftClient = wrapped.getThrift();

        Method getInputProtocol = thriftClient.getClass().getMethod("getInputProtocol");
        getInputProtocol.invoke(thriftClient);
        TProtocol inputProtocol = (TProtocol) getInputProtocol.invoke(thriftClient);
        inputProtocol.getTransport().close();

        ((WrappedClient) obj).free();
        super.returnObject(obj);
    }

    public static ThriftClientPool factory(Class cls, Collection<HostPort> hostPorts, boolean useFramed) {
        ThriftClientPoolFactory tpf = new ThriftClientPoolFactory(cls, hostPorts, useFramed);
        return new ThriftClientPool(tpf);
    }

    public static ThriftClientPool factory(Class cls, Collection<HostPort> hostPorts) {
        return factory(cls, hostPorts, true);
    }

    public static ThriftClientPool factory(Class cls, HostPort hostPort, boolean useFramed) {
        ArrayList list = new ArrayList();
        list.add(hostPort);
        return factory(cls, list, useFramed);
    }

    public static ThriftClientPool factory(Class cls, HostPort hostPort) {
        return factory(cls, hostPort, false);
    }

}

class ThriftClientPoolFactory implements PoolableObjectFactory {
    protected Class cls;
    protected Collection<HostPort> hostPorts;
    protected boolean isFramedTransport = false;

    ThriftClientPoolFactory(Class cls, Collection<HostPort> hostPorts) {
        this.cls = cls;
        this.hostPorts = hostPorts;
    }

    ThriftClientPoolFactory(Class cls, Collection<HostPort> hostPorts, boolean isFramedTransport) {
        this.cls = cls;
        this.hostPorts = hostPorts;
        this.isFramedTransport = isFramedTransport;
    }

    protected Object createClient(Class cls, String host, int port) throws ClientDoesNotExistException {
        try {
            Class params[] = {TProtocol.class};

            TTransport transport;
            if (isFramedTransport) {
                // TODO: set a timeout
                transport = new TFramedTransport(new TSocket(host, port));
            } else {
                transport = new TSocket(host, port);
            }

            Constructor constructor = cls.getConstructor(params);
            TBinaryProtocol thriftProtocol = new TBinaryProtocol(transport);
            return constructor.newInstance(thriftProtocol);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object makeObject() throws Exception {
        // TODO: round robin thru the hosts
        HostPort hostPort = hostPorts.iterator().next();

        Object client = createClient(cls, hostPort.getHost(), hostPort.getPort());
        Method getInputProtocol = cls.getMethod("getInputProtocol");
        getInputProtocol.invoke(client);

        TProtocol inputProtocol = (TProtocol) getInputProtocol.invoke(client);
        inputProtocol.getTransport().open();

        return new WrappedClient(client);
    }

    public void destroyObject(Object o) throws Exception {
    }

    public boolean validateObject(Object o) {
        return ((WrappedClient) o).isFree();
    }

    public void activateObject(Object o) throws Exception {
        ((WrappedClient) o).use();
    }

    public void passivateObject(Object client) throws Exception {
        Method getInputProtocol = cls.getMethod("getInputProtocol");
        getInputProtocol.invoke(client);
        TProtocol inputProtocol = (TProtocol) getInputProtocol.invoke(client);
        inputProtocol.getTransport().close();
    }
}
