package com.abhedyam.service.interfaces;

import com.abhedyam.dto.CustomerCreateRequest;
import com.abhedyam.dto.CustomerResponse;
import com.abhedyam.dto.CustomerSearchRequest;
import com.abhedyam.dto.CustomerSearchResult;
import com.abhedyam.dto.CustomerUpdateRequest;
import com.abhedyam.dto.CustomerBasicSummaryResponse;
import com.abhedyam.dto.CustomerSalesSummaryResponse;
import com.abhedyam.dto.CustomerPaymentsSummaryResponse;
import com.abhedyam.dto.CustomerNotesSummaryResponse;
import com.abhedyam.dto.CustomerRemindersSummaryResponse;
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
    PageResponse<CustomerResponse> getOwnerCustomers(UUID ownerId, String searchText, Integer page, Integer size, String sortBy, String sortDirection);
    CustomerBasicSummaryResponse getCustomerBasicSummary(UUID ownerId, UUID customerId);
    CustomerSalesSummaryResponse getCustomerSalesSummary(UUID ownerId, UUID customerId);
    CustomerPaymentsSummaryResponse getCustomerPaymentsSummary(UUID ownerId, UUID customerId);
    CustomerNotesSummaryResponse getCustomerNotesSummary(UUID ownerId, UUID customerId);
    CustomerRemindersSummaryResponse getCustomerRemindersSummary(UUID ownerId, UUID customerId);
    Customer updateCustomer(CustomerUpdateRequest request);
    Customer updateCustomerForOwner(UUID ownerId, CustomerUpdateRequest request);
    NearestCustomerResponse findNearestCustomer(NearestCustomerRequest request);
    void invalidateCustomerSummaryCache(UUID ownerId, UUID customerId);
}

