package com.umg.edu.progra3_customer_service.resource;

import com.umg.edu.progra3_customer_service.service.MyCustomerList;
import com.umg.edu.progra3_utilities.stack.MyStack;
import com.umg.edu.progra3_utilities.tree.MyBinaryTree;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class DataStructuresProducer {

    @Produces
    @ApplicationScoped
    public MyCustomerList myCustomerList() {
        return new MyCustomerList();
    }

    @Produces
    @ApplicationScoped
    public MyBinaryTree myBinaryTree() {
        return new MyBinaryTree();
    }

    @Produces
    @ApplicationScoped
    public MyStack myStack() {
        return new MyStack();
    }
}
