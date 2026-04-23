package com.smartcampus.exception.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part 5.2 - Maps LinkedResourceNotFoundException to HTTP 422 Unprocessable Entity
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "UNPROCESSABLE_ENTITY");
        body.put("message", ex.getMessage());
        body.put("referencedResourceId", ex.getResourceId());
        body.put("hint", "Ensure the roomId exists before registering a sensor.");
        return Response.status(422)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
