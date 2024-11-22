package org.dcis.ccm.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CCMServer {
    static public void main (String [] args) throws Exception
    {
        Server server = ServerBuilder
                .forPort(8200)
                .build();
        server.start();
        server.awaitTermination();
    }
}
