package com.smartcampus;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Sets the base path for all endpoints to /api/v1
    // This satisfies Part 1 - Project & Application Configuration
}
