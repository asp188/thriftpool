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
import java.util.List;

/**
 * User: Arin Sarkissian
 * Date: Feb 26, 2010
 * Time: 3:28:41 PM
 */

public class ThriftClientPool extends GenericObjectPool {
    public static final boolean DEFAULT_TEST_ON_BORROW = false;
    private static final int DEFAULT_TIMEOUT = 250;

    public ThriftClientPool(PoolableObjectFactory objFactory) {
        super(objFactory);
        setPoolOptions();
    }

    public ThriftClientPool(PoolableObjectFactory factory, Config config) {
        super(factory, config);
    }

    public void setPoolOptions() {
        this.setMaxIdle(DEFAULT_MAX_IDLE);
        this.setMaxActive(DEFAULT_MAX_ACTIVE);
        this.setMinEvictableIdleTimeMillis(DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        this.setTestOnBorrow(DEFAULT_TEST_ON_BORROW);
        this.setMaxWait(DEFAULT_MAX_WAIT);
    }

    public void setPoolConfig(Config config) {
        this.setConfig(config);
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

    public static ThriftClientPool factory(ThriftClientOptions thriftClientOptions, Config poolConfig) {
        ThriftClientPoolFactory tpf = new ThriftClientPoolFactory(thriftClientOptions);
        return new ThriftClientPool(tpf, poolConfig);
    }

    public static ThriftClientPool factory(ThriftClientOptions thriftClientOptions) {
        ThriftClientPoolFactory tpf = new ThriftClientPoolFactory(thriftClientOptions);
        return new ThriftClientPool(tpf);
    }
}

class ThriftClientPoolFactory implements PoolableObjectFactory {
    protected Class cls;
    protected Collection<HostPort> hostPorts;
    protected boolean isFramedTransport = false;
    protected int timeout = 0;

    ThriftClientPoolFactory(Class cls, Collection<HostPort> hostPorts, int timeout) {
        this.cls = cls;
        this.hostPorts = hostPorts;
        this.timeout = timeout;
    }

    ThriftClientPoolFactory(Class cls, Collection<HostPort> hostPorts, boolean isFramedTransport, int timeout) {
        this.cls = cls;
        this.hostPorts = hostPorts;
        this.isFramedTransport = isFramedTransport;
        this.timeout = timeout;
    }

    ThriftClientPoolFactory(ThriftClientOptions thriftClientOptions) {
        this.cls = thriftClientOptions.getThriftClientClass();
        this.hostPorts = thriftClientOptions.getHostPorts();
        this.isFramedTransport = thriftClientOptions.isUseFramed();
        this.timeout = thriftClientOptions.getTimeout();
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
