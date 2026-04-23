package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

// FIXED: 'javax.ws.rs' - 'jakarta.ws.rs'
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.*;

// NOTE: No @Path here - this is a sub-resource, not a root resource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings - full history
    @GET
    public Response getReadings() {
        List<SensorReading> history = store.getReadingsFor(sensorId);
        return Response.ok(history).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings - add new reading
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensors().get(sensorId);

        if (reading == null) {
            return Response.status(400)
                    .entity(Map.of("error", "Reading body required")).build();
        }

        // Block readings for sensors under MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException( "Sensor '" + sensorId + "' is MAINTENANCE. Cannot post readings." );
        }

        // Auto-generate ID and timestamp
        SensorReading saved = new SensorReading(reading.getValue());
        store.getReadingsFor(sensorId).add(saved);

        // SIDE EFFECT: update parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201).entity(saved).build();
    }
}