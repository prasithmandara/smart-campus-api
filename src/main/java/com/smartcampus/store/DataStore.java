package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {

    // Singleton pattern: only ONE instance of DataStore ever exists
    private static final DataStore INSTANCE = new DataStore();

    public static DataStore getInstance() {
        return INSTANCE;
    }

    // Private constructor so nobody can do "new DataStore()"
    private DataStore() {
        seedData(); // pre-load demo data when app starts
    }

    // The actual storage maps
    // ConcurrentHashMap is thread-safe (safe when multiple requests arrive at once)
    private final Map<String, Room>          rooms    = new ConcurrentHashMap<>();
    private final Map<String, Sensor>        sensors  = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    // Getters so resource classes can access the maps 
    public Map<String, Room> getRooms()       { return rooms; }
    public Map<String, Sensor> getSensors()   { return sensors; }

    // Get readings for a sensor (creates empty list if sensor is new)
    public List<SensorReading> getReadingsFor(String sensorId) {
        return readings.computeIfAbsent(
            sensorId,
            k -> Collections.synchronizedList(new ArrayList<>())
        );
    }

    // Demo data so the API has something to show immediately
    private void seedData() {

        // Create two rooms
        Room r1 = new Room("LIB-301", "Library Quiet Study", 40);
        Room r2 = new Room("LAB-101", "Computer Lab A",      30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        // Create sensors
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE",      21.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001",  "CO2",         "ACTIVE",     450.0, "LIB-301");
        Sensor s3 = new Sensor("OCC-001",  "Occupancy",   "MAINTENANCE",  0.0, "LAB-101");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);

        // Link sensors to rooms (both directions)
        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s2.getId());
        r2.getSensorIds().add(s3.getId());
    }
}