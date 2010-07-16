package com.phatduckk.thriftpool;

/**
 * User: Arin Sarissian
 * Date: Apr 22, 2010
 * Time: 3:02:06 PM
 */

public class WrappedClient {
    private Object thrift;
    private boolean isFree = true;
    private ThriftClientPool pool;

    public WrappedClient(Object thrift) {
        this.thrift = thrift;
    }

    void use(){
        isFree = false;
    }

    void free() {
        isFree = true;
    }

    public void release() throws ThriftClientException {
        try {
            pool.returnObject(this);
        } catch (Exception e) {
            throw new ThriftClientException(e);
        }
    }

    public Object getThrift() {
        return thrift;
    }

    void setPool(ThriftClientPool pool) {
        this.pool = pool;
    }

    public boolean isFree() {
        return isFree;
    }
}
