package com.smartcampus;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

// Base path for all endpoints
@ApplicationPath("/api/v1")
public class App extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Root resource classes
        classes.add(com.smartcampus.resources.DiscoveryResource.class);
        classes.add(com.smartcampus.resources.RoomResource.class);
        classes.add(com.smartcampus.resources.SensorResource.class);


        // Exception mappers
        classes.add(com.smartcampus.exception.RoomNotEmptyExceptionMapper.class);
        classes.add(com.smartcampus.exception.LinkedResourceNotFoundExceptionMapper.class);
        classes.add(com.smartcampus.exception.SensorUnavailableExceptionMapper.class);
        classes.add(com.smartcampus.exception.GlobalExceptionMapper.class);

        // Filters
        classes.add(com.smartcampus.filter.LoggingFilter.class);

        return classes;
    }
}