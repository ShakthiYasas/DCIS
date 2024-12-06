package org.dcis.cam.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CAMServer {
    static public void main (String [] args) throws Exception
    {
        Server server = ServerBuilder
                .forPort(8300)
                .build();
        server.start();
        server.awaitTermination();
    }
}