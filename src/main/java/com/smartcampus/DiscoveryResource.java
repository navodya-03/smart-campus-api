package com.smartcampus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("version", "v1.0");
        info.put("admin", "admin@smartcampus.edu");
        info.put("rooms_url", "/api/v1/rooms");
        info.put("sensors_url", "/api/v1/sensors");

        return Response.ok(info).build();
    }
}