package com.smartcampus.exception.mapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Part 5.4 - Global catch-all mapper.
 * Catches unexpected errors and returns 500.
 * Lets JAX-RS WebApplicationExceptions (404, 405 etc) pass through normally.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        // If it's already a JAX-RS exception (404, 405 etc), let it pass through as-is
        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse();
        }

        // Only catch truly unexpected errors and return 500
        LOG.severe("Unexpected error: " + ex.getClass().getName() + " - " + ex.getMessage());

        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "INTERNAL_SERVER_ERROR");
        body.put("message", "An unexpected error occurred. Please contact the administrator.");
        return Response.status(500)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
