package com.umg.edu.progra3_customer_service.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umg.edu.progra3_customer_service.entities.TicketEvent;
import com.umg.edu.progra3_model.entities.Customer;
import com.umg.edu.progra3_model.entities.History;
import com.umg.edu.progra3_model.entities.Service;
import com.umg.edu.progra3_model.entities.Ticket;
import com.umg.edu.progra3_model.enums.TicketStatus;
import com.umg.edu.progra3_customer_service.service.MyCustomerList;
import com.umg.edu.progra3_customer_service.service.TicketDataService;
import com.umg.edu.progra3_utilities.stack.MyStack;
import com.umg.edu.progra3_utilities.tree.MyBinaryTree;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.time.LocalDateTime;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class TicketConsumer {

    @Inject
    EntityManager em;

    @Inject
    TicketDataService ticketDataService;


    @Inject
    ObjectMapper mapper;

    @Incoming("ticket-events-listener")
    @Transactional
    public void handleMessage(JsonObject json) throws JsonProcessingException {        
        System.out.println("Mensaje recibido desde RabbitMQ: " + json.encodePrettily());
        TicketEvent event = mapper.readValue(json.encode(), TicketEvent.class);
        System.out.println("----- TicketConsumer.handleMessage");
        System.out.println("Received event: " + event.getAction());
        switch (event.getAction()) {
            case CREATE:
                Ticket ticket = event.getTicket();
                // Cargar customer y service desde la base de datos
                Customer customer = em.find(Customer.class, ticket.getCustomer().getId());
                Service service = em.find(Service.class, ticket.getService().getId());

                if (customer == null || service == null) {
                    System.out.println("Customer or Service not found");
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
                ticketDataService.tree.insertTicket(ticket);
                System.out.println("----- Tree");
                System.out.println(ticketDataService.tree.toString());
                break;

            case DELETE:
                boolean deleted = ticketDataService.tree.deleteTicketById(event.getTicketId());
                if (deleted) {
                    Ticket ticketD = em.find(Ticket.class, event.getTicketId());
                    if (ticketD != null) {
                        ticketD.setStatus(TicketStatus.CANCELLED);
                        em.merge(ticketD);

                        // Registrar en historial (opcional)
                        History hD = new History();
                        hD.setTicket(ticketD);
                        hD.setEventDate(LocalDateTime.now());
                        hD.setDescription("Ticket cancelado");
                        em.persist(hD);
                    }
                    System.out.println("----- Tree");
                    System.out.println(ticketDataService.tree.toString());
                    System.out.println("Ticket deleted");
                } else {
                    System.out.println("----- Tree");
                    System.out.println(ticketDataService.tree.toString());
                    System.out.println("Ticket not found");
                }
                break;
        } 
    }
        
}
