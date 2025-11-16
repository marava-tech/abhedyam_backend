package com.abhedyam.service;

import com.abhedyam.dto.CustomerCreateRequest;
import com.abhedyam.dto.CustomerProfileSummary;
import com.abhedyam.dto.CustomerResponse;
import com.abhedyam.dto.CustomerSearchRequest;
import com.abhedyam.dto.CustomerSearchResult;
import com.abhedyam.dto.CustomerUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Note;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Reminder;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.enums.ReminderStatus;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.repository.NoteRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ReminderRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.service.interfaces.ICustomerService;
import com.abhedyam.util.PhoneUtil;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {
    
    private final CustomerRepository customerRepository;
    private final LocationDetailsRepository locationDetailsRepository;
    private final SaleItemRepository saleItemRepository;
    private final PaymentRepository paymentRepository;
    private final NoteRepository noteRepository;
    private final ReminderRepository reminderRepository;
    
    @Override
    @Transactional
    public Customer create(CustomerCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
        
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
        customer.setPhoneNormalized(normalizedPhone);
        customer.setType(UserType.CUSTOMER);
        customer.setOwnerId(ownerId);
        customer.setIsActive(true);
        
        Customer savedCustomer = customerRepository.save(customer);
        
        if (request.getVillage() != null || request.getLatitude() != null || request.getLongitude() != null) {
            LocationDetails locationDetails = new LocationDetails();
            locationDetails.setUserId(savedCustomer.getId());
            locationDetails.setVillage(request.getVillage());
            locationDetails.setLatitude(request.getLatitude() != null ? request.getLatitude() : BigDecimal.ZERO);
            locationDetails.setLongitude(request.getLongitude() != null ? request.getLongitude() : BigDecimal.ZERO);
            locationDetailsRepository.save(locationDetails);
        }
        
        return savedCustomer;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Customer getById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        return customer;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Customer> getByOwnerId(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        UUID targetOwnerId = ownerId != null ? ownerId : currentOwnerId;
        if (!currentOwnerId.equals(targetOwnerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only view your own customers");
        }
        return customerRepository.findByOwnerId(targetOwnerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getMyCustomersWithVillage() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Customer> customers = customerRepository.findByOwnerId(ownerId);
        
        return customers.stream()
            .map(customer -> {
                String village = null;
                LocationDetails location = locationDetailsRepository.findByUserId(customer.getId()).orElse(null);
                if (location != null) {
                    village = location.getVillage();
                }
                return CustomerResponse.fromEntity(customer, village);
            })
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<Customer> searchCustomers(CustomerSearchRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(request.getSortDirection()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            request.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<Customer> page = customerRepository.searchCustomers(
            ownerId,
            request.getSearchTerm(),
            pageable
        );
        
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerSearchResult> searchByName(String name) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCaseAndOwnerId(name, ownerId);
        return customers.stream()
            .map(customer -> new CustomerSearchResult(customer.getId(), customer.getName()))
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerProfileSummary getCustomerProfileSummary(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Customer customer = getById(customerId);
        
        List<SaleItem> saleItems = saleItemRepository.findByCustomerId(customerId).stream()
            .filter(item -> item.getOwnerId().equals(ownerId))
            .toList();
        List<Payment> payments = paymentRepository.findByCustomerId(customerId).stream()
            .filter(p -> p.getOwnerId().equals(ownerId))
            .toList();
        List<Note> notes = noteRepository.findByCustomerId(customerId).stream()
            .filter(n -> n.getOwnerId().equals(ownerId))
            .toList();
        List<Reminder> reminders = reminderRepository.findByCustomerId(customerId).stream()
            .filter(r -> r.getOwnerId().equals(ownerId))
            .toList();
        
        long totalSales = saleItems.size();
        BigDecimal totalAmount = saleItems.stream()
            .map(item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPaid = payments.stream()
            .filter(p -> p.getStatus() == com.abhedyam.model.enums.PaymentStatus.SUCCESS)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDue = totalAmount.subtract(totalPaid);
        
        long totalReminders = reminders.size();
        long pendingReminders = reminders.stream()
            .filter(r -> r.getStatus() == ReminderStatus.PENDING)
            .count();
        
        return new CustomerProfileSummary(
            customerId,
            customer.getName(),
            customer.getPhone(),
            customer.getImageUrl(),
            totalSales,
            totalAmount,
            totalPaid,
            totalDue,
            (long) notes.size(),
            totalReminders,
            pendingReminders
        );
    }
    
    @Override
    @Transactional
    public Customer updateCustomer(CustomerUpdateRequest request) {
        Customer customer = getById(request.getId());
        
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            customer.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
            customer.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            customer.setPhoneNormalized(normalizedPhone);
        }
        return customerRepository.save(customer);
    }
}

