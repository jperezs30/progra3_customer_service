package com.umg.edu.progra3_customer_service.service;

import com.umg.edu.progra3_model.entities.Ticket;
import com.umg.edu.progra3_utilities.list.MyLinkedList;

public class MyCustomerList {

    public static class Node {
        Long customerId;
        MyLinkedList tickets;
        Node next;

        Node(Long customerId) {
            this.customerId = customerId;
            this.tickets = new MyLinkedList();
        }
    }

    private Node head;

    public void addTicket(Ticket ticket) {
        Long customerId = ticket.getCustomer().getId();
        Node current = head;

        while (current != null) {
            if (current.customerId.equals(customerId)) {
                current.tickets.add(ticket);
                return;
            }
            current = current.next;
        }

        // Si no existe, crear nuevo nodo
        Node newNode = new Node(customerId);
        newNode.tickets.add(ticket);
        newNode.next = head;
        head = newNode;
    }

    public MyLinkedList getTicketsForCustomer(Long customerId) {
        Node current = head;
        while (current != null) {
            if (current.customerId.equals(customerId)) {
                return current.tickets;
            }
            current = current.next;
        }
        return new MyLinkedList(); // vacío si no se encuentra
    }
    
    public Node getHead() {
        return head;
    }

}

