package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(store.rooms.values());
        return Response.ok(roomList).build();
    }

    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "INVALID_INPUT");
            error.put("message", "Room ID is required");
            return Response.status(400).entity(error).build();
        }

        if (store.rooms.containsKey(room.getId())) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "ROOM_ALREADY_EXISTS");
            error.put("message", "A room with ID " + room.getId() + " already exists");
            return Response.status(409).entity(error).build();
        }

        store.rooms.put(room.getId(), room);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Room created successfully");
        response.put("room", room);

        // Location header for 201 Created - shows client where to find the new resource
        return Response.status(201)
                .header("Location", "/api/v1/rooms/" + room.getId())
                .entity(response)
                .build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String id) {
        Room room = store.rooms.get(id);
        if (room == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Room with ID " + id + " does not exist");
            return Response.status(404).entity(error).build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String id) {
        Room room = store.rooms.get(id);

        if (room == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Room with ID " + id + " does not exist");
            return Response.status(404).entity(error).build();
        }

        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(id);
        }

        store.rooms.remove(id);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Room " + id + " deleted successfully");
        return Response.ok(response).build();
    }
}
