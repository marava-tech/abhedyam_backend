package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.service.interfaces.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {
    
    private final CustomerRepository customerRepository;
    
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }
    
    public Customer getById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
    
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }
    
    public List<Customer> getByOwnerId(UUID ownerId) {
        return customerRepository.findByOwnerId(ownerId);
    }
    
    @Transactional
    public Customer update(UUID id, Customer customerDetails) {
        Customer customer = getById(id);
        if (customerDetails.getOwnerId() != null) customer.setOwnerId(customerDetails.getOwnerId());
        if (customerDetails.getLocationDetailsId() != null) customer.setLocationDetailsId(customerDetails.getLocationDetailsId());
        return customerRepository.save(customer);
    }
    
    @Transactional
    public void delete(UUID id) {
        Customer customer = getById(id);
        customer.setDeletedAt(Instant.now());
        customer.setIsActive(false);
        customerRepository.save(customer);
    }
}

