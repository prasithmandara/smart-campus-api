package com.smartcampus.model;

import java.util.UUID;

public class SensorReading {
    private String id;
    private long timestamp;
    private double value;

    // Default constructor - REQUIRED by Jackson
    public SensorReading() {}

    // This constructor auto-generates ID and timestamp
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString(); // unique ID
        this.timestamp = System.currentTimeMillis(); // current time in ms
        this.value = value;
    }

    // Getters
    public String getId() { return id; }
    public long getTimestamp() { return timestamp; }
    public double getValue() { return value; }

    // Setters - REQUIRED by Jackson
    public void setId(String id) { this.id = id; }
    public void setTimestamp(long ts) { this.timestamp = ts; }
    public void setValue(double value) { this.value = value; }
}