package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    // GET /api/v1 → returns API info and links to all resources
    @GET
    public Response discover() {

        Map<String, Object> info = new HashMap<>();
        info.put("api", "Smart Campus Sensor & Room Management API");
        info.put("version", "1.0.0");
        info.put("status", "running");

        Map<String, String> contact = new HashMap<>();
        contact.put("name", "Campus Infrastructure Team");
        contact.put("email", "infra@smartcampus.ac.uk");
        info.put("contact", contact);

        // These are HATEOAS links - clients discover endpoints from this response
        Map<String, String> links = new HashMap<>();
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        info.put("resources", links);

        return Response.ok(info).build();
    }
}