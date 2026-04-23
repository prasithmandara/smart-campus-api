package com.smartcampus.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// Catches ANY exception not caught by the specific mappers above
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        // Log the FULL error on the server (for developer debugging)
        LOG.log(Level.SEVERE, "Unhandled exception", ex);

        // Return a SAFE generic message to the client (no stack trace leaked)
        return Response
                .status(500)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                        "status",  500,
                        "error",   "Internal Server Error",
                        "message", "An unexpected error occurred. Contact the administrator."
                ))
                .build();
    }
}