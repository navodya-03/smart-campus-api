package com.smartcampus;

import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class RoomResource {


    private DataStore dataStore = DataStore.getInstance();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(dataStore.getRooms().values());
        return Response.ok(roomList).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") String id) {
        Room room = dataStore.getRooms().get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}").build();
        }
        return Response.ok(room).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        if (newRoom.getId() == null || newRoom.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID and Name are required\"}").build();
        }


        dataStore.getRooms().put(newRoom.getId(), newRoom);
        return Response.status(Response.Status.CREATED).entity(newRoom).build();
    }


    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = dataStore.getRooms().get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}").build();
        }


        if (!room.getSensorIds().isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Cannot delete room. It still has active sensors.\"}")
                    .build();
        }


        dataStore.getRooms().remove(id);
        return Response.noContent().build(); // 204 Success
    }
}