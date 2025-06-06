package com.umg.edu.progra3_customer_service.entities;

import com.umg.edu.progra3_model.entities.Ticket;

public class TicketEvent {

    public enum ActionType {
        CREATE,
        DELETE
    }

    private ActionType action;
    private Ticket ticket;
    private Long ticketId;

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
