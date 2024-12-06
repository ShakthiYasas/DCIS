package org.dcis.csm.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CSMServer {
    static public void main (String [] args) throws Exception
    {
        Server server = ServerBuilder
                .forPort(8400)
                .build();
        server.start();
        server.awaitTermination();
    }
}
