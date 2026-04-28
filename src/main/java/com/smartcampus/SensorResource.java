package com.smartcampus;

import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {

    private DataStore dataStore = DataStore.getInstance();

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor newSensor) {

        if (newSensor.getRoomId() == null || !dataStore.getRooms().containsKey(newSensor.getRoomId())) {
            return Response.status(422)
                    .entity("{\"error\":\"Linked Room not found. Validation failed.\"}")
                    .build();
        }

        dataStore.getSensors().put(newSensor.getId(), newSensor);


        dataStore.getRooms().get(newSensor.getRoomId()).getSensorIds().add(newSensor.getId());

        return Response.status(Response.Status.CREATED).entity(newSensor).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(dataStore.getSensors().values());


        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }

        return Response.ok(sensorList).build();
    }


    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}