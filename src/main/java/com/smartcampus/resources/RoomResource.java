package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.*;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    // GET /api/v1/rooms - return all rooms
    @GET
    public Response getAllRooms() {
        return Response.ok(store.getRooms().values()).build();
    }

    // POST /api/v1/rooms - create a new room
    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(400)
                    .entity(Map.of("error", "Room ID is required"))
                    .build();
        }
        if (store.getRooms().containsKey(room.getId())) {
            return Response.status(409)
                    .entity(Map.of("error", "Room with this ID already exists"))
                    .build();
        }
        store.getRooms().put(room.getId(), room);
        return Response.status(201)
                .entity(room)
                .header("Location", "/api/v1/rooms/" + room.getId())
                .build();
    }

    // GET /api/v1/rooms/{roomId} - get one specific room
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(404)
                    .entity(Map.of("error", "Room not found: " + roomId))
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} - delete room (only if no sensors)
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(404)
                    .entity(Map.of("error", "Room not found: " + roomId))
                    .build();
        }

        // BUSINESS RULE: Cannot delete room if it has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException( "Room '" + roomId + "' cannot be deleted - it still has " + room.getSensorIds().size() + " sensor(s). Remove sensors first.");
        }

        store.getRooms().remove(roomId);
        return Response.noContent().build(); // 204 No Content = success
    }
}