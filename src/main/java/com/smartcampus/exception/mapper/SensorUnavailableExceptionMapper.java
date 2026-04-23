package com.smartcampus.exception.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part 5.3 - Maps SensorUnavailableException to HTTP 403 Forbidden
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "SENSOR_UNAVAILABLE");
        body.put("message", ex.getMessage());
        body.put("sensorId", ex.getSensorId());
        body.put("hint", "Change the sensor status to ACTIVE before posting readings.");
        return Response.status(403)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
