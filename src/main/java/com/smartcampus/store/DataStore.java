package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton in-memory data store.
 * ConcurrentHashMap is used instead of HashMap to prevent race conditions
 * when multiple requests access data simultaneously.
 * This satisfies the Part 1 report question about lifecycle and synchronization.
 */
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    public final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {}

    public static DataStore getInstance() {
        return INSTANCE;
    }
}
