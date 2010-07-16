package com.phatduckk.thriftpool;

/**
 * User: arin
 * Date: Apr 22, 2010
 * Time: 3:34:52 PM
 */


public class HostPort {
    private String host;
    private int port;

    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
