package com.smartcampus.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

/**
 * Part 5.5 - Logging Filter
 * Implements BOTH ContainerRequestFilter and ContainerResponseFilter
 * to log every incoming request and every outgoing response.
 * Using a filter instead of per-method logging is cleaner (cross-cutting concern).
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOG.info("[REQUEST]  " +
                requestContext.getMethod() + " " +
                requestContext.getUriInfo().getRequestUri());
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        LOG.info("[RESPONSE] " +
                requestContext.getMethod() + " " +
                requestContext.getUriInfo().getRequestUri() +
                " -> HTTP " + responseContext.getStatus());
    }
}
