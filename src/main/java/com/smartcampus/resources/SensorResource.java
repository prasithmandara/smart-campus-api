package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.*;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    // GET /api/v1/sensors or GET /api/v1/sensors?type=CO2
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        Collection<Sensor> all = store.getSensors().values();
        if (type != null && !type.isBlank()) {
            // Filter by type if ?type= parameter is provided
            List<Sensor> filtered = all.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        return Response.ok(all).build();
    }

    // POST /api/v1/sensors - register a new sensor
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(400)
                    .entity(Map.of("error", "Sensor ID is required")).build();
        }
        // Check the roomId actually exists
        if (sensor.getRoomId() == null || !store.getRooms().containsKey(sensor.getRoomId())) {throw new LinkedResourceNotFoundException( "roomId '" + sensor.getRoomId() + "' does not exist.");
        }
        if (store.getSensors().containsKey(sensor.getId())) {
            return Response.status(409)
                    .entity(Map.of("error", "Sensor already exists")).build();
        }
        store.getSensors().put(sensor.getId(), sensor);
        // Link sensor to its room
        store.getRooms().get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        return Response.status(201)
                .entity(sensor)
                .header("Location", "/api/v1/sensors/" + sensor.getId())
                .build();
    }

    // GET /api/v1/sensors/{sensorId}
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null)
            return Response.status(404)
                    .entity(Map.of("error", "Sensor not found")).build();
        return Response.ok(sensor).build();
    }

    // DELETE /api/v1/sensors/{sensorId}
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null)
            return Response.status(404)
                    .entity(Map.of("error", "Sensor not found")).build();

        // Remove from room's sensor list
        if (sensor.getRoomId() != null && store.getRooms().containsKey(sensor.getRoomId())) {
            store.getRooms().get(sensor.getRoomId())
                    .getSensorIds().remove(sensorId);
        }
        store.getSensors().remove(sensorId);
        return Response.noContent().build();
    }

    // SUB-RESOURCE LOCATOR (Part 4) 
    // This method does NOT have @GET/@POST etc.
    // JAX-RS sees /sensors/{id}/readings and calls this method,
    // which returns a SensorReadingResource instance to handle the request.
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(
            @PathParam("sensorId") String sensorId) {
        if (!store.getSensors().containsKey(sensorId))
            throw new NotFoundException("Sensor not found: " + sensorId);
        return new SensorReadingResource(sensorId);
    }
}