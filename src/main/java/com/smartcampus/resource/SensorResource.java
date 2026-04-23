package com.smartcampus.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Part 3 - Sensor Operations Handles all /api/v1/sensors endpoints
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    /**
     * GET /api/v1/sensors GET /api/v1/sensors?type=CO2 (optional filter by
     * type) Returns all sensors, optionally filtered by type
     */
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(store.sensors.values());

        // Filter by type if query param is provided
        if (type != null && !type.isEmpty()) {
            result.removeIf(s -> !s.getType().equalsIgnoreCase(type));
        }

        return Response.ok(result).build();
    }

    /**
     * GET /api/v1/sensors/{id} Returns a single sensor by ID
     */
    @GET
    @Path("/{id}")
    public Response getSensor(@PathParam("id") String id) {
        Sensor sensor = store.sensors.get(id);

        if (sensor == null) {
            return Response.status(404)
                    .entity(Map.of("error", "NOT_FOUND", "message", "Sensor not found"))
                    .build();
        }

        return Response.ok(sensor).build();
    }

    /**
     * POST /api/v1/sensors Registers a new sensor. Validates that the
     * referenced roomId actually exists - throws 422 if not.
     */
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "INVALID_INPUT");
            error.put("message", "Sensor ID is required");
            return Response.status(400).entity(error).build();
        }

        // Validate that the roomId exists - Part 3 integrity check
        if (!store.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(sensor.getRoomId());
        }

        store.sensors.put(sensor.getId(), sensor);

        // Add sensor ID to the room's sensorIds list
        store.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        // Initialize an empty readings list for this sensor
        store.readings.put(sensor.getId(), new ArrayList<>());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Sensor registered successfully");
        response.put("sensor", sensor);
        return Response.status(201).entity(response).build();
    }

    /**
     * Sub-resource locator - Part 4 Delegates
     * /api/v1/sensors/{sensorId}/readings to SensorReadingResource
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
