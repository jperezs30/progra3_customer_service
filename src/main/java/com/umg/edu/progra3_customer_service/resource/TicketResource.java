package com.umg.edu.progra3_customer_service.resource;

import java.time.LocalDateTime;

import com.umg.edu.progra3_customer_service.service.MyCustomerList;
import com.umg.edu.progra3_utilities.stack.MyStack;
import com.umg.edu.progra3_utilities.tree.MyBinaryTree;
import jakarta.ws.rs.*;

import com.umg.edu.progra3_model.entities.Customer;
import com.umg.edu.progra3_model.entities.History;
import com.umg.edu.progra3_model.entities.Service;
import com.umg.edu.progra3_model.entities.Ticket;
import com.umg.edu.progra3_model.enums.TicketStatus;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

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
    @Transactional
    public Response create(Ticket ticket) {
        // Cargar customer y service desde la base de datos
        Customer customer = em.find(Customer.class, ticket.getCustomer().getId());
        Service service = em.find(Service.class, ticket.getService().getId());

        if (customer == null || service == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Customer or Service not found").build();
        }

        ticket.setCustomer(customer);
        ticket.setService(service);
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setCreatedAt(LocalDateTime.now());
        em.persist(ticket);

        History h = new History();
        h.setTicket(ticket);
        h.setEventDate(LocalDateTime.now());
        h.setDescription("Ticket creado");
        em.persist(h);

        // Insertar en estructuras en memoria
        tree.insertTicket(ticket);
        System.out.println("----- Tree");
        System.out.println(tree.toString());
        return Response.status(Response.Status.CREATED).entity(ticket).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = tree.deleteTicketById(id);
        if (deleted) {
            Ticket ticket = em.find(Ticket.class, id);
            if (ticket != null) {
                ticket.setStatus(TicketStatus.CANCELLED);
                em.merge(ticket);

                // Registrar en historial (opcional)
                History h = new History();
                h.setTicket(ticket);
                h.setEventDate(LocalDateTime.now());
                h.setDescription("Ticket cancelado");
                em.persist(h);
            }
            System.out.println("----- Tree");
            System.out.println(tree.toString());
            return Response.ok("Ticket deleted").build();
        } else {
            System.out.println("----- Tree");
            System.out.println(tree.toString());
            return Response.status(Response.Status.NOT_FOUND).entity("Ticket not found").build();
        }
    }

    @POST
    @Path("/next")
    @Transactional
    public Response attendNext(@QueryParam("service") String serviceName) {
        Ticket next = tree.attendNext(serviceName);
        if (next == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No ticket available for this service").build();
        }        
        operationStack.push(next);
        customerList.addTicket(next);

        next.setAttendedAt(java.time.LocalDateTime.now());
        next.setStatus(TicketStatus.ATTENDED);
        em.merge(next);

        System.out.println("----- Tree");
        System.out.println(tree.toString());

        return Response.ok(next).build();
    }

}