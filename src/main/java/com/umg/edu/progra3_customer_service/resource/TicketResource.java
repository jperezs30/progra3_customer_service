package com.umg.edu.progra3_customer_service.resource;

import com.umg.edu.progra3_customer_service.entities.TicketEvent;
import com.umg.edu.progra3_customer_service.service.MyCustomerList;
import com.umg.edu.progra3_customer_service.service.TicketDataService;
import com.umg.edu.progra3_model.entities.History;
import com.umg.edu.progra3_model.entities.Ticket;
import com.umg.edu.progra3_model.enums.TicketStatus;
import com.umg.edu.progra3_utilities.stack.MyStack;
import com.umg.edu.progra3_utilities.tree.MyBinaryTree;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import java.time.LocalDateTime;

@Path("/tickets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TicketResource {

    @Inject
    TicketDataService ticketDataService;

    @Inject
    EntityManager em;

    @Inject
    @Channel("ticket-events")
    Emitter<TicketEvent> emitter;

    @POST
    public Response create(Ticket ticket) {
        TicketEvent event = new TicketEvent();
        event.setAction(TicketEvent.ActionType.CREATE);
        event.setTicket(ticket);

        OutgoingRabbitMQMetadata metadata = OutgoingRabbitMQMetadata.builder()
            .withRoutingKey("turno.create") // personaliza esta routing key
            .build();

        Message<TicketEvent> message = Message.of(event).addMetadata(metadata);
        emitter.send(message);

        return Response.accepted().entity("Ticket enviado a la cola.").build();
    }


    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        TicketEvent event = new TicketEvent();
        event.setAction(TicketEvent.ActionType.DELETE);        
        event.setTicketId(id);
         OutgoingRabbitMQMetadata metadata = OutgoingRabbitMQMetadata.builder()
        .withRoutingKey("turno.delete")
        .build();
    
        emitter.send(Message.of(event).addMetadata(metadata));
        return Response.ok("Solicitud de eliminación enviada a cola").build();
    }

    @POST
    @Path("/next")
    @Transactional
    public Response attendNext(@QueryParam("service") String serviceName) {
        System.out.println("----- TicketResource.attendNext") ;
        System.out.println("Service: " + serviceName);
        Ticket ticket = ticketDataService.tree.attendNext(serviceName);
        if (ticket == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No hay turnos pendientes").build();
        }
        ticket.setAttendedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.ATTENDED);
        em.merge(ticket);
        ticketDataService.history.push(ticket);
        ticketDataService.customerList.addTicket(ticket);

        History h = new History();
        h.setTicket(ticket);
        h.setEventDate(LocalDateTime.now());
        h.setDescription("Ticket atendido");
        em.persist(h);

        return Response.ok(ticket).build();
    }
}