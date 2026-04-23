package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part 1 - Discovery Endpoint
 * GET /api/v1
 * Returns API metadata including version, contact, and resource links (HATEOAS)
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", "Smart Campus Sensor & Room Management API");
        response.put("version", "1.0");
        response.put("contact", "admin@smartcampus.ac.uk");
        response.put("description", "RESTful API for managing campus rooms and IoT sensors");

        // HATEOAS links - tells clients where to find resources
        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("resources", links);

        return Response.ok(response).build();
    }
}
