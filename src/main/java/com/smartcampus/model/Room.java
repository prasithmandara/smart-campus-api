package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>();

    // Default constructor - REQUIRED by Jackson to convert JSON to Java object
    public Room() {}

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    // Getters (read values)
    public String getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public List<String> getSensorIds() { return sensorIds; }

    // Setters (write values) - REQUIRED by Jackson
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setSensorIds(List<String> s) { this.sensorIds = s; }
}