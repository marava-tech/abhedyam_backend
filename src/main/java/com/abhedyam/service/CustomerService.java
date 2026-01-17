package com.abhedyam.service;

import com.abhedyam.dto.CustomerCreateRequest;
import com.abhedyam.dto.CustomerProfileSummary;
import com.abhedyam.dto.CustomerResponse;
import com.abhedyam.dto.CustomerSearchRequest;
import com.abhedyam.dto.CustomerSearchResult;
import com.abhedyam.dto.CustomerUpdateRequest;
import com.abhedyam.dto.NearestCustomerRequest;
import com.abhedyam.dto.NearestCustomerResponse;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Note;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.Reminder;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.model.enums.ReminderStatus;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.repository.NoteRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.ReminderRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.ICustomerService;
import com.abhedyam.util.PhoneUtil;
import com.abhedyam.util.SecurityUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements ICustomerService {
    
    private final CustomerRepository customerRepository;
    private final LocationDetailsRepository locationDetailsRepository;
    private final SaleItemRepository saleItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final NoteRepository noteRepository;
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String CUSTOMERS_CACHE_PREFIX = "customers:my:";
    private static final String CUSTOMER_SUMMARY_CACHE_PREFIX = "customers:summary:";
    private static final String CUSTOMER_MY_SUMMARY_CACHE_PREFIX = "customers:my-summary:";
    private static final int CACHE_TTL_MINUTES = 5;
    
    @Override
    @Transactional
    public Customer create(CustomerCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
        
        Optional<Customer> existingCustomerOpt = customerRepository.findByPhoneNormalized(normalizedPhone);
        
        if (existingCustomerOpt.isPresent()) {
            Customer existingCustomer = existingCustomerOpt.get();
            
            if (existingCustomer.getOwnerId() == null) {
                customerRepository.delete(existingCustomer);
                customerRepository.flush();
            } else {
                throw new BusinessException("CUSTOMER_EXISTS", 
                    "Customer with this phone number already exists and is associated with another owner");
            }
        }
        
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
        customer.setPhoneNormalized(normalizedPhone);
        customer.setType(UserType.CUSTOMER);
        customer.setOwnerId(ownerId);
        customer.setIsActive(true);
        
        Customer savedCustomer = customerRepository.save(customer);
        
            try {
                String pattern = CUSTOMERS_CACHE_PREFIX + ownerId + ":*";
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            } catch (Exception e) {
                log.warn("Error invalidating customer cache on create: {}", e.getMessage());
            }
        
        LocationDetails existingLocation = locationDetailsRepository.findById(savedCustomer.getId()).orElse(null);
        String villageName = (request.getVillage() != null && !request.getVillage().trim().isEmpty()) 
            ? request.getVillage().trim() 
            : "No Village";
        
        if (existingLocation != null) {
            existingLocation.setVillage(villageName);
            if (request.getLatitude() != null) {
                existingLocation.setLatitude(request.getLatitude());
            }
            if (request.getLongitude() != null) {
                existingLocation.setLongitude(request.getLongitude());
            }
            locationDetailsRepository.save(existingLocation);
        } else {
            LocationDetails locationDetails = new LocationDetails();
            locationDetails.setUserId(savedCustomer.getId());
            locationDetails.setVillage(villageName);
            locationDetails.setLatitude(request.getLatitude() != null ? request.getLatitude() : BigDecimal.ZERO);
            locationDetails.setLongitude(request.getLongitude() != null ? request.getLongitude() : BigDecimal.ZERO);
            locationDetailsRepository.save(locationDetails);
        }
        
        return savedCustomer;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Customer getById(UUID id) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Allow access if:
        // 1. Current user is the customer themselves
        // 2. Current user is the owner of this customer
        if (currentUserId.equals(id)) {
            return customer;
        }
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        if (currentUser.getType() == UserType.BUSINESS && 
            customer.getOwnerId() != null && 
            customer.getOwnerId().equals(currentUserId)) {
            return customer;
        }
        
        throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
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
    public PageResponse<CustomerResponse> getMyCustomersWithVillage(Integer page, Integer size) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        
        String cacheKey = CUSTOMERS_CACHE_PREFIX + ownerId + ":" + page + ":" + size;
        
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                PageResponse<CustomerResponse> cachedResponse = objectMapper.readValue(
                    cached, 
                    new TypeReference<PageResponse<CustomerResponse>>() {}
                );
                log.debug("Returning cached customers for owner {} page {} size {}", ownerId, page, size);
                return cachedResponse;
            }
        } catch (Exception e) {
            log.warn("Error reading from cache for key: {}", cacheKey, e);
        }
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Customer> customerPage = customerRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
        List<Customer> customers = customerPage.getContent();
        
        List<UUID> customerIds = customers.stream().map(Customer::getId).toList();
        List<LocationDetails> locations = locationDetailsRepository.findByUserIdIn(customerIds);
        java.util.Map<UUID, String> villageMap = locations.stream()
            .collect(java.util.stream.Collectors.toMap(
                LocationDetails::getUserId,
                LocationDetails::getVillage,
                (v1, v2) -> v1
            ));
        
        List<CustomerResponse> responses = customers.stream()
            .map(customer -> {
                String village = villageMap.get(customer.getId());
                return CustomerResponse.fromEntity(customer, village);
            })
            .toList();
        
        PageResponse<CustomerResponse> response = new PageResponse<>(
            responses,
            customerPage.getNumber(),
            customerPage.getSize(),
            customerPage.getTotalElements(),
            customerPage.getTotalPages(),
            customerPage.hasNext(),
            customerPage.hasPrevious()
        );
        
        try {
            String jsonResponse = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, jsonResponse, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached customers for owner {} page {} size {}", ownerId, page, size);
        } catch (Exception e) {
            log.warn("Error caching customers for key: {}", cacheKey, e);
        }
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> filterCustomers(String searchText) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        String normalizedSearchText = searchText != null && !searchText.trim().isEmpty() ? searchText.trim() : "";
        
        boolean isNumeric = false;
        
        if (!normalizedSearchText.isEmpty()) {
            try {
                Long.parseLong(normalizedSearchText);
                isNumeric = true;
            } catch (NumberFormatException e) {
                isNumeric = false;
            }
        }
        
        List<Customer> customers = customerRepository.filterCustomers(
            ownerId,
            normalizedSearchText.isEmpty() ? null : normalizedSearchText,
            isNumeric
        );
        
        List<Customer> limitedCustomers = customers.stream().limit(15).toList();
        List<UUID> customerIds = limitedCustomers.stream().map(Customer::getId).toList();
        List<LocationDetails> locations = locationDetailsRepository.findByUserIdIn(customerIds);
        java.util.Map<UUID, String> villageMap = locations.stream()
            .collect(java.util.stream.Collectors.toMap(
                LocationDetails::getUserId,
                LocationDetails::getVillage,
                (v1, v2) -> v1
            ));
        
        return limitedCustomers.stream()
            .map(customer -> {
                String village = villageMap.get(customer.getId());
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
        
        if (customers.isEmpty()) {
            return List.of();
        }
        
        List<UUID> customerIds = customers.stream().map(Customer::getId).toList();
        List<LocationDetails> locations = locationDetailsRepository.findByUserIdIn(customerIds);
        java.util.Map<UUID, String> villageMap = locations.stream()
            .collect(java.util.stream.Collectors.toMap(
                LocationDetails::getUserId,
                LocationDetails::getVillage,
                (v1, v2) -> v1
            ));
        
        return customers.stream()
            .map(customer -> {
                String village = villageMap.get(customer.getId());
                return new CustomerSearchResult(customer.getId(), customer.getName(), customer.getPhone(), village);
            })
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerProfileSummary getCustomerProfileSummary(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        String cacheKey = CUSTOMER_SUMMARY_CACHE_PREFIX + ownerId + ":" + customerId;
        
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                CustomerProfileSummary cachedSummary = objectMapper.readValue(
                    cached,
                    CustomerProfileSummary.class
                );
                log.debug("Returning cached customer summary for owner {} customer {}", ownerId, customerId);
                return cachedSummary;
            }
        } catch (Exception e) {
            log.warn("Error reading from cache for key: {}", cacheKey, e);
        }
        
        Customer customer = getById(customerId);
        
        List<SaleItem> saleItems = saleItemRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        List<Payment> payments = paymentRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        List<Note> notes = noteRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        List<Reminder> reminders = reminderRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        
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
        
        CustomerProfileSummary summary = new CustomerProfileSummary(
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
        
        try {
            String jsonResponse = objectMapper.writeValueAsString(summary);
            redisTemplate.opsForValue().set(cacheKey, jsonResponse, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached customer summary for owner {} customer {}", ownerId, customerId);
        } catch (Exception e) {
            log.warn("Error caching customer summary for key: {}", cacheKey, e);
        }
        
        return summary;
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerProfileSummary getMyCustomerSummary() {
        UUID customerId = SecurityUtil.getCurrentUserId();
        String cacheKey = CUSTOMER_MY_SUMMARY_CACHE_PREFIX + customerId;
        
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                CustomerProfileSummary cachedSummary = objectMapper.readValue(
                    cached,
                    CustomerProfileSummary.class
                );
                log.debug("Returning cached my customer summary for customer {}", customerId);
                return cachedSummary;
            }
        } catch (Exception e) {
            log.warn("Error reading from cache for key: {}", cacheKey, e);
        }
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        UUID ownerId = customer.getOwnerId();
        if (ownerId == null) {
            CustomerProfileSummary summary = new CustomerProfileSummary(
                    customerId,
                    customer.getName(),
                    customer.getPhone(),
                    customer.getImageUrl(),
                    0L,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0L,
                    0L,
                    0L
            );
            
            try {
                String jsonResponse = objectMapper.writeValueAsString(summary);
                redisTemplate.opsForValue().set(cacheKey, jsonResponse, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Error caching my customer summary for key: {}", cacheKey, e);
            }
            
            return summary;
        }
        
        List<SaleItem> saleItems = saleItemRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        List<Payment> payments = paymentRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        List<Note> notes = noteRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        List<Reminder> reminders = reminderRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        
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
        
        CustomerProfileSummary summary = new CustomerProfileSummary(
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
        
        try {
            String jsonResponse = objectMapper.writeValueAsString(summary);
            redisTemplate.opsForValue().set(cacheKey, jsonResponse, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached my customer summary for customer {}", customerId);
        } catch (Exception e) {
            log.warn("Error caching my customer summary for key: {}", cacheKey, e);
        }
        
        return summary;
    }
    
    @Override
    @Transactional
    public Customer updateCustomer(CustomerUpdateRequest request) {
        Customer customer = getById(request.getId());
        UUID ownerId = customer.getOwnerId();
        UUID customerId = customer.getId();
        
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            customer.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
            customer.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            customer.setPhoneNormalized(normalizedPhone);
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        
        try {
            if (ownerId != null) {
                redisTemplate.delete(CUSTOMER_SUMMARY_CACHE_PREFIX + ownerId + ":" + customerId);
                redisTemplate.delete(CUSTOMER_MY_SUMMARY_CACHE_PREFIX + customerId);
                
                String pattern = CUSTOMERS_CACHE_PREFIX + ownerId + ":*";
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            }
            log.debug("Invalidated customer cache for customer {}", customerId);
        } catch (Exception e) {
            log.warn("Error invalidating customer cache: {}", e.getMessage());
        }
        
        return updatedCustomer;
    }
    
    public void invalidateOwnerCaches(UUID ownerId) {
        try {
            String[] patterns = {
                CUSTOMERS_CACHE_PREFIX + ownerId + ":*",
                CUSTOMER_SUMMARY_CACHE_PREFIX + ownerId + ":*",
                CUSTOMER_MY_SUMMARY_CACHE_PREFIX + "*"
            };
            
            for (String pattern : patterns) {
                try {
                    var keys = redisTemplate.keys(pattern);
                    if (keys != null && !keys.isEmpty()) {
                        redisTemplate.delete(keys);
                        log.debug("Invalidated {} customer cache keys for pattern: {}", keys.size(), pattern);
                    }
                } catch (Exception e) {
                    log.warn("Error invalidating customer cache for pattern {}: {}", pattern, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Error during customer cache invalidation for owner {}: {}", ownerId, e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public NearestCustomerResponse findNearestCustomer(NearestCustomerRequest request) {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new BusinessException("MISSING_LOCATION", "Current location not captured. Please provide latitude and longitude.");
        }
        
        UUID ownerId = SecurityUtil.getCurrentUserId();
        BigDecimal ownerLat = request.getLatitude();
        BigDecimal ownerLng = request.getLongitude();
        
        List<LocationDetails> allLocationDetails = locationDetailsRepository.findCustomerLocationsByOwnerId(ownerId);
        
        if (allLocationDetails.isEmpty()) {
            throw new ResourceNotFoundException("No customers with location details found");
        }
        
        List<UUID> customerIdsWithLocation = allLocationDetails.stream()
            .map(LocationDetails::getUserId)
            .collect(Collectors.toList());
        
        List<SaleItem> saleItems = saleItemRepository.findByCustomerIdAndOwnerIdIn(customerIdsWithLocation, ownerId);
        List<Payment> payments = paymentRepository.findByCustomerIdInAndOwnerId(customerIdsWithLocation, ownerId);
        
        Map<UUID, BigDecimal> totalAmountByCustomer = saleItems.stream()
            .collect(Collectors.groupingBy(
                SaleItem::getCustomerId,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE),
                    BigDecimal::add
                )
            ));
        
        Map<UUID, BigDecimal> totalPaidByCustomer = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
            .collect(Collectors.groupingBy(
                Payment::getCustomerId,
                Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
            ));
        
        List<UUID> customersWithPendingAmounts = customerIdsWithLocation.stream()
            .filter(customerId -> {
                BigDecimal totalAmount = totalAmountByCustomer.getOrDefault(customerId, BigDecimal.ZERO);
                BigDecimal totalPaid = totalPaidByCustomer.getOrDefault(customerId, BigDecimal.ZERO);
                return totalAmount.subtract(totalPaid).compareTo(BigDecimal.ZERO) > 0;
            })
            .collect(Collectors.toList());
        
        if (customersWithPendingAmounts.isEmpty()) {
            throw new ResourceNotFoundException("No customers with location details and pending amount found");
        }
        
        List<LocationDetails> locationDetails = locationDetailsRepository.findCustomerLocationsByCustomerIds(ownerId, customersWithPendingAmounts);
        
        Optional<LocationDetailsWithDistance> nearestLocationOpt = locationDetails.stream()
            .map(loc -> {
                double distance = calculateDistance(
                    ownerLat.doubleValue(),
                    ownerLng.doubleValue(),
                    loc.getLatitude().doubleValue(),
                    loc.getLongitude().doubleValue()
                );
                return new LocationDetailsWithDistance(loc, distance);
            })
            .min(Comparator.comparingDouble(LocationDetailsWithDistance::getDistance));
        
        if (nearestLocationOpt.isEmpty()) {
            throw new ResourceNotFoundException("No customers with location details and pending amount found");
        }
        
        LocationDetailsWithDistance nearestLocationWithDistance = nearestLocationOpt.get();
        LocationDetails nearestLocation = nearestLocationWithDistance.getLocation();
        double distanceKm = nearestLocationWithDistance.getDistance();
        UUID nearestCustomerId = nearestLocation.getUserId();
        
        Customer nearestCustomer = customerRepository.findById(nearestCustomerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        List<SaleItem> customerSaleItems = saleItems.stream()
            .filter(item -> item.getCustomerId().equals(nearestCustomerId))
            .collect(Collectors.toList());
        List<Payment> customerPayments = payments.stream()
            .filter(p -> p.getCustomerId().equals(nearestCustomerId))
            .collect(Collectors.toList());
        
        BigDecimal totalAmount = totalAmountByCustomer.getOrDefault(nearestCustomerId, BigDecimal.ZERO);
        BigDecimal totalPaid = totalPaidByCustomer.getOrDefault(nearestCustomerId, BigDecimal.ZERO);
        BigDecimal pendingAmount = totalAmount.subtract(totalPaid);
        
        BigDecimal lastPaidAmount = customerPayments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
            .sorted(Comparator.comparing(Payment::getTimestamp).reversed())
            .findFirst()
            .map(Payment::getAmount)
            .orElse(BigDecimal.ZERO);
        
        String productName = null;
        if (!customerSaleItems.isEmpty()) {
            SaleItem mostRecentSaleItem = customerSaleItems.stream()
                .sorted(Comparator.comparing(SaleItem::getCreatedAt).reversed())
                .findFirst()
                .orElse(null);
            
            if (mostRecentSaleItem != null && mostRecentSaleItem.getProductId() != null) {
                Optional<Product> product = productRepository.findById(mostRecentSaleItem.getProductId());
                if (product.isPresent()) {
                    productName = product.get().getName();
                }
            }
        }
        
        return new NearestCustomerResponse(
            nearestCustomer.getId(),
            nearestCustomer.getName(),
            nearestCustomer.getPhone(),
            nearestLocation.getVillage(),
            productName,
            totalPaid,
            pendingAmount,
            lastPaidAmount,
            BigDecimal.valueOf(distanceKm).setScale(2, java.math.RoundingMode.HALF_UP)
        );
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    private static class LocationDetailsWithDistance {
        private final LocationDetails location;
        private final double distance;
        
        public LocationDetailsWithDistance(LocationDetails location, double distance) {
            this.location = location;
            this.distance = distance;
        }
        
        public LocationDetails getLocation() {
            return location;
        }
        
        public double getDistance() {
            return distance;
        }
    }
}

