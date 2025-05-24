package com.umg.edu.progra3_customer_service.service;

import com.umg.edu.progra3_model.entities.Ticket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class TicketService {

    @Inject
    EntityManager em;

    @Transactional
    public void create(Ticket ticket) {
        em.persist(ticket);
    }

    public Ticket findById(Long id) {
        return em.find(Ticket.class, id);
    }

    public List<Ticket> listAll() {
        return em.createQuery("FROM Ticket", Ticket.class).getResultList();
    }

    @Transactional
    public void delete(Long id) {
        Ticket ticket = em.find(Ticket.class, id);
        if (ticket != null) {
            em.remove(ticket);
        }
    }
}
