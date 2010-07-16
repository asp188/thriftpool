package com.phatduckk.thriftpool;

/**
 * User: arin
 * Date: Apr 22, 2010
 * Time: 3:46:58 PM
 */


public class ThriftClientException extends Exception {
    public ThriftClientException() {
    }

    public ThriftClientException(String message) {
        super(message);
    }

    public ThriftClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThriftClientException(Throwable cause) {
        super(cause);
    }
}
