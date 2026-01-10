package com.abhedyam.service.interfaces;

import com.abhedyam.dto.CustomerCreateRequest;
import com.abhedyam.dto.CustomerProfileSummary;
import com.abhedyam.dto.CustomerResponse;
import com.abhedyam.dto.CustomerSearchRequest;
import com.abhedyam.dto.CustomerSearchResult;
import com.abhedyam.dto.CustomerUpdateRequest;
import com.abhedyam.dto.NearestCustomerRequest;
import com.abhedyam.dto.NearestCustomerResponse;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {
    Customer create(CustomerCreateRequest request);
    Customer getById(UUID id);
    List<Customer> getByOwnerId(UUID ownerId);
    PageResponse<CustomerResponse> getMyCustomersWithVillage(Integer page, Integer size);
    List<CustomerResponse> filterCustomers(String searchText);
    PageResponse<Customer> searchCustomers(CustomerSearchRequest request);
    List<CustomerSearchResult> searchByName(String name);
    CustomerProfileSummary getCustomerProfileSummary(UUID customerId);
    CustomerProfileSummary getMyCustomerSummary();
    Customer updateCustomer(CustomerUpdateRequest request);
    NearestCustomerResponse findNearestCustomer(NearestCustomerRequest request);
}

