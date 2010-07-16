package com.phatduckk.thriftpool.thriftpool.testhelper.testthrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import java.net.InetSocketAddress;

/**
 * User: arin
 * Date: Apr 28, 2010
 * Time: 11:02:08 PM
 */

public class HelperService {
    private TSimpleServer server;
    public static final int PORT = 2222;
    public static final String HOST_NAME = "0.0.0.0";

    public HelperService() throws TTransportException {
        TServerSocket tServerSocket = new TServerSocket(new InetSocketAddress(HOST_NAME, PORT));
        TProcessor processor = new TestService.Processor(new TestServiceHandler());
        server = new TSimpleServer(processor, tServerSocket);

        (new Thread(new ServerThread(server))).start();
    }

    public void stop() {
        server.stop();
    }
}

class ServerThread implements Runnable {
    private TSimpleServer server;

    ServerThread(TSimpleServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        server.serve();
    }
}