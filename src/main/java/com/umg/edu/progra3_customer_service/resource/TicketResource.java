package com.umg.edu.progra3_customer_service.resource;

import com.umg.edu.progra3_customer_service.service.MyCustomerList;
import com.umg.edu.progra3_customer_service.service.PersistenceService;
import com.umg.edu.progra3_utilities.stack.MyStack;
import com.umg.edu.progra3_utilities.tree.MyBinaryTree;
import jakarta.ws.rs.*;
import com.umg.edu.progra3_model.entities.Ticket;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TicketResource {

    @Inject
    EntityManager em;

    private final MyBinaryTree tree = new MyBinaryTree();
    private final MyStack operationStack = new MyStack();
    private final MyCustomerList customerList = new MyCustomerList();

    @POST
    public Response create(Ticket ticket) {
        tree.insertTicket(ticket);
        return Response.status(Response.Status.CREATED).entity(ticket).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = tree.deleteTicketById(id);
        if (deleted) {
            return Response.ok("Ticket deleted").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Ticket not found").build();
        }
    }

    @POST
    @Path("/next")
    public Response attendNext(@QueryParam("service") String serviceName) {
        Ticket next = tree.attendNext(serviceName);
        if (next == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No ticket available for this service").build();
        }
        next.setAttendedAt(java.time.LocalDateTime.now());
        operationStack.push(next);
        customerList.addTicket(next);
        return Response.ok(next).build();
    }

    @POST
    @Path("/persist")
    public Response persistAll() {
        PersistenceService service = new PersistenceService(em, tree, customerList);
        service.persistAll();
        return Response.ok("Persistencia completada con éxito").build();
    }

}