package com.smartcampus.exception;

/**
 * Part 5.2 - Thrown when a sensor references a roomId that does not exist.
 * Mapped to HTTP 422 Unprocessable Entity.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    private final String resourceId;

    public LinkedResourceNotFoundException(String resourceId) {
        super("The referenced room with ID '" + resourceId + "' does not exist in the system.");
        this.resourceId = resourceId;
    }

    public String getResourceId() { return resourceId; }
}
