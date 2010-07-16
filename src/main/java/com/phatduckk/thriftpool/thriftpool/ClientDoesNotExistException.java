package com.phatduckk.thriftpool.thriftpool;

/**
 * User: Arin Sarkissian
 * Date: Apr 22, 2010
 * Time: 3:05:37 PM
 */

public class ClientDoesNotExistException extends Exception {
    public ClientDoesNotExistException(String s, Exception e) {
        super(s, e);
    }
}
