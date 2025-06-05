package com.umg.edu.progra3_customer_service.service;

import com.umg.edu.progra3_model.entities.History;
import com.umg.edu.progra3_model.entities.Ticket;
import com.umg.edu.progra3_utilities.list.MyLinkedList;
import com.umg.edu.progra3_utilities.list.MyNode;
import com.umg.edu.progra3_utilities.tree.MyBinaryTree;
import com.umg.edu.progra3_utilities.tree.TreeNode;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

public class PersistenceService {

    private final EntityManager em;
    private final MyBinaryTree tree;
    private final MyCustomerList customerList;

    public PersistenceService(EntityManager em, MyBinaryTree tree, MyCustomerList customerList) {
        this.em = em;
        this.tree = tree;
        this.customerList = customerList;
    }

    @Transactional
    public void persistAll() {
        persistPendings(tree.getRoot());

        MyCustomerList.Node current = customerList.getHead();
        while (current != null) {
            MyLinkedList list = current.tickets;
            for (int i = 0; i < list.size(); i++) {
                Ticket t = list.get(i);
                em.persist(t);

                History h = new History();
                h.setTicket(t);
                h.setEventDate(LocalDateTime.now());
                h.setDescription("Ticket atendido");
                em.persist(h);
            }
            current = current.next;
        }
    }

    private void persistPendings(TreeNode node) {
        if (node == null) return;

        MyNode current = node.queue.getFront();
        while (current != null) {
            em.persist(current.data);
            current = current.next;
        }

        persistPendings(node.left);
        persistPendings(node.right);
    }
}