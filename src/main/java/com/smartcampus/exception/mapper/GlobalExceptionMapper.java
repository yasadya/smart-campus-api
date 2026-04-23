package com.smartcampus.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Part 5.4 - Global catch-all mapper.
 * Catches ANY unexpected Throwable and returns 500.
 * NEVER exposes stack traces to the client - prevents information leakage.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        // Log internally for debugging - but never send to client
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
