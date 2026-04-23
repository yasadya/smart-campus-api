package com.smartcampus.exception.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part 5.1 - Maps RoomNotEmptyException to HTTP 409 Conflict
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "ROOM_NOT_EMPTY");
        body.put("message", ex.getMessage());
        body.put("roomId", ex.getRoomId());
        body.put("hint", "Remove or reassign all sensors from this room before deleting it.");
        return Response.status(409)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
