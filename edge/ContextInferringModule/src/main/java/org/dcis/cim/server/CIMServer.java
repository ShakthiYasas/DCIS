package org.dcis.cim.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CIMServer {
    static public void main (String [] args) throws Exception
    {
        Server server = ServerBuilder
                .forPort(8583)
                .build();
        server.start();
        server.awaitTermination();
    }
}
