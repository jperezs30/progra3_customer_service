
package com.umg.edu.progra3_customer_service.service;


import com.umg.edu.progra3_utilities.stack.MyStack;
import com.umg.edu.progra3_utilities.tree.MyBinaryTree;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketDataService {

    public final MyBinaryTree tree = new MyBinaryTree();
    public final MyCustomerList customerList = new MyCustomerList();
    public final MyStack history = new MyStack();

}