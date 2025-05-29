package com.umg.edu.progra3_customer_service.resource;

import com.umg.edu.progra3_customer_service.service.TicketService;
import com.umg.edu.progra3_model.entities.Ticket;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/turno")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    @Inject
    TicketService ticketService;

    @POST
    public Response create(Ticket ticket) {
        ticketService.create(ticket);
        return Response.status(Response.Status.CREATED).entity(ticket).build();
    }

    @GET
    public List<Ticket> listAll() {
        return ticketService.listAll();
    }

    @GET
    @Path("/{id}")
    public Ticket findById(@PathParam("id") Long id) {
        return ticketService.findById(id);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        ticketService.delete(id);
        return Response.noContent().build();
    }
}
