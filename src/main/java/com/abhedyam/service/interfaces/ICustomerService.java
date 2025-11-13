package com.abhedyam.service.interfaces;

import com.abhedyam.dto.CustomerCreateRequest;
import com.abhedyam.dto.CustomerProfileSummary;
import com.abhedyam.dto.CustomerSearchRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {
    Customer create(CustomerCreateRequest request);
    Customer getById(UUID id);
    List<Customer> getAll();
    List<Customer> getByOwnerId(UUID ownerId);
    PageResponse<Customer> searchCustomers(CustomerSearchRequest request);
    CustomerProfileSummary getCustomerProfileSummary(UUID customerId);
    Customer update(UUID id, CustomerCreateRequest request);
    void delete(UUID id);
}

