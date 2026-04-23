package com.smartcampus;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {

    public static void main(String[] args) throws Exception {
        ResourceConfig config = new ResourceConfig().packages("com.smartcampus");
        HttpServer server = GrizzlyHttpServerFactory
                .createHttpServer(URI.create("http://localhost:8080/"), config);
        System.out.println("===========================================");
        System.out.println("Smart Campus API started!");
        System.out.println("URL: http://localhost:8080/api/v1");
        System.out.println("===========================================");
        System.out.println("Press ENTER to stop...");
        System.in.read();
        server.stop();
    }
}
