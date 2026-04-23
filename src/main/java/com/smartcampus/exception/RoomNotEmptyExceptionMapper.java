package com.smartcampus.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

// @Provider tells JAX-RS to auto-register this mapper
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        return Response
                .status(409) // 409 Conflict
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                        "status",  409,
                        "error",   "Room Not Empty",
                        "message", ex.getMessage()
                ))
                .build();
    }
}