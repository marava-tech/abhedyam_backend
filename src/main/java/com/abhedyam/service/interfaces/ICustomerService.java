package com.abhedyam.service.interfaces;

import com.abhedyam.model.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {
    Customer create(Customer customer);
    Customer getById(UUID id);
    List<Customer> getAll();
    List<Customer> getByOwnerId(UUID ownerId);
    Customer update(UUID id, Customer customerDetails);
    void delete(UUID id);
}

