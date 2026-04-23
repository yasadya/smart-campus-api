package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        ResourceConfig config = new ResourceConfig()
            .packages("com.smartcampus")
            .register(JacksonFeature.class);

        // Set /api/v1 directly in the base URI for Grizzly
        HttpServer server = GrizzlyHttpServerFactory
            .createHttpServer(URI.create("http://localhost:8080/api/v1/"), config);

        System.out.println("===========================================");
        System.out.println("Smart Campus API started!");
        System.out.println("URL: http://localhost:8080/api/v1/");
        System.out.println("===========================================");
        System.out.println("Press ENTER to stop...");
        System.in.read();
        server.stop();
    }
}
