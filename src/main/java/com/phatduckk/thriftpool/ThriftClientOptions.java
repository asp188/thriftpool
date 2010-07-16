package com.phatduckk.thriftpool;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: arin
 * Date: Jul 15, 2010
 * Time: 5:17:37 PM
 */


public class ThriftClientOptions {
    private int timeout;
    private boolean useFramed;
    private Collection<HostPort> hostPorts = new ArrayList<HostPort>();
    private Class thriftClientClass;

    public ThriftClientOptions() {
    }

    public ThriftClientOptions(int timeout, boolean useFramed, Collection<HostPort> hostPorts, Class thriftClientClass) {
        this.timeout = timeout;
        this.useFramed = useFramed;
        this.hostPorts = hostPorts;
        this.thriftClientClass = thriftClientClass;
    }

    public ThriftClientOptions(int timeout, boolean useFramed, HostPort hostPorts, Class thriftClientClass) {
        this.timeout = timeout;
        this.useFramed = useFramed;
        this.thriftClientClass = thriftClientClass;
        this.hostPorts.add(hostPorts);
    }

    public Class getThriftClientClass() {
        return thriftClientClass;
    }

    public void setThriftClientClass(Class thriftClientClass) {
        this.thriftClientClass = thriftClientClass;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isUseFramed() {
        return useFramed;
    }

    public void setUseFramed(boolean useFramed) {
        this.useFramed = useFramed;
    }

    public Collection<HostPort> getHostPorts() {
        return hostPorts;
    }

    public void setHostPorts(Collection<HostPort> hostPorts) {
        this.hostPorts = hostPorts;
    }

    public void addHostPort(HostPort hostPort) {
        this.hostPorts.add(hostPort);
    }
}
