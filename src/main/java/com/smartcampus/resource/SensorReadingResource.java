package com.smartcampus.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Part 4 - Sub-Resource for sensor readings Handles
 * /api/v1/sensors/{sensorId}/readings This class is returned by SensorResource
 * as a sub-resource locator
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings Returns the full reading history
     * for a sensor
     */
    @GET
    public Response getReadings() {
        Sensor sensor = store.sensors.get(sensorId);
        if (sensor == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Sensor with ID " + sensorId + " does not exist");
            return Response.status(404).entity(error).build();
        }

        List<SensorReading> list = store.readings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(list).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings Appends a new reading for this
     * sensor. SIDE EFFECT: Also updates the sensor's currentValue field. Throws
     * SensorUnavailableException (403) if sensor status is MAINTENANCE.
     */
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.sensors.get(sensorId);

        if (sensor == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Sensor with ID " + sensorId + " does not exist");
            return Response.status(404).entity(error).build();
        }

        // State constraint: MAINTENANCE sensors cannot accept readings
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId);
        }

        // Auto-generate ID and timestamp
        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());

        // Save reading
        store.readings
                .computeIfAbsent(sensorId, k -> new ArrayList<>())
                .add(reading);

        // SIDE EFFECT: Update the parent sensor's currentValue for data consistency
        sensor.setCurrentValue(reading.getValue());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Reading recorded successfully");
        response.put("reading", reading);
        response.put("updatedSensorValue", sensor.getCurrentValue());
        return Response.status(201).entity(response).build();
    }
}
