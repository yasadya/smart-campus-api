package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

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

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.sensors.get(sensorId);

        if (sensor == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Sensor with ID " + sensorId + " does not exist");
            return Response.status(404).entity(error).build();
        }

        // 403 Forbidden if sensor is MAINTENANCE or OFFLINE - cannot accept readings
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) ||
            "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId);
        }

        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());
        store.readings.get(sensorId).add(reading);

        // Side effect: update parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Reading recorded successfully");
        response.put("reading", reading);
        response.put("updatedSensorValue", sensor.getCurrentValue());
        return Response.status(201).entity(response).build();
    }
}
