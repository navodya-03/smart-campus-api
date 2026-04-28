package com.smartcampus;

import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;
    private DataStore dataStore = DataStore.getInstance();


    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        List<SensorReading> readings = dataStore.getSensorReadings().getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = dataStore.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Sensor not found\"}").build();
        }


        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\":\"Sensor is in maintenance mode.\"}")
                    .build();
        }


        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());


        dataStore.getSensorReadings().putIfAbsent(sensorId, new ArrayList<>());
        dataStore.getSensorReadings().get(sensorId).add(reading);


        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
