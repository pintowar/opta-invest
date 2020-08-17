package com.github.invest.quarkus.controller;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/api")
public class HomeController {

    @Context
    private HttpServletRequest httpRequest;

    @GET
    @Path("/session-id")
    @Produces(MediaType.TEXT_PLAIN)
    public String sessionId() {
        return httpRequest.getSession().getId();
    }
}
