package com.smartcampus.exception;

/**
 * Part 5.1 - Thrown when a room with sensors is attempted to be deleted.
 * Mapped to HTTP 409 Conflict.
 */
public class RoomNotEmptyException extends RuntimeException {
    private final String roomId;

    public RoomNotEmptyException(String roomId) {
        super("Room " + roomId + " cannot be deleted because it still has sensors assigned to it.");
        this.roomId = roomId;
    }

    public String getRoomId() { return roomId; }
}
