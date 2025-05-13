package org.dcis.re.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class REServer {
    static public void main (String [] args) throws Exception
    {
        Server server = ServerBuilder
                .forPort(8500)
                .build();
        server.start();
        server.awaitTermination();
    }
}
