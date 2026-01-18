package com.abhedyam.service;

import com.abhedyam.dto.BillInfoResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Owner;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.model.enums.SaleItemStatus;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {
    
    private final SaleItemRepository saleItemRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final LocationDetailsRepository locationDetailsRepository;
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String BILL_PREFIX = "ABH-";
    
    @Transactional(readOnly = true)
    public List<BillInfoResponse> getBillsByCustomerId(UUID customerId) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer could not be found"));
        
        UUID ownerId = customer.getOwnerId();
        if (ownerId == null) {
            throw new ResourceNotFoundException("Customer owner not found");
        }
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        boolean hasAccess = false;
        if (currentUserId.equals(customerId)) {
            hasAccess = true;
        } else if (currentUser.getType() == UserType.BUSINESS && ownerId.equals(currentUserId)) {
            hasAccess = true;
        }
        
        if (!hasAccess) {
            throw new BusinessException("UNAUTHORIZED", "You don't have permission to access this customer's bills");
        }
        
        List<SaleItem> saleItems = saleItemRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        
        if (saleItems.isEmpty()) {
            return List.of();
        }
        
        Map<String, List<SaleItem>> itemsByTransaction = saleItems.stream()
                .filter(si -> si.getTransactionId() != null)
                .collect(Collectors.groupingBy(SaleItem::getTransactionId));
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        
        List<Product> products = productRepository.findByOwnerId(ownerId);
        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        
        List<Payment> allPayments = paymentRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        Map<UUID, List<Payment>> paymentsBySaleItem = allPayments.stream()
                .filter(p -> p.getSaleItemId() != null && p.getStatus() == PaymentStatus.SUCCESS)
                .collect(Collectors.groupingBy(Payment::getSaleItemId));
        
        LocationDetails location = locationDetailsRepository.findByUserId(customerId).orElse(null);
        
        List<BillInfoResponse> bills = new ArrayList<>();
        int billCounter = 1;
        
        List<Map.Entry<String, List<SaleItem>>> sortedTransactions = itemsByTransaction.entrySet().stream()
                .sorted((e1, e2) -> {
                    Instant t1 = e1.getValue().isEmpty() ? Instant.MIN : e1.getValue().get(0).getCreatedAt();
                    Instant t2 = e2.getValue().isEmpty() ? Instant.MIN : e2.getValue().get(0).getCreatedAt();
                    return t2.compareTo(t1);
                })
                .collect(Collectors.toList());
        
        for (Map.Entry<String, List<SaleItem>> entry : sortedTransactions) {
            String transactionId = entry.getKey();
            List<SaleItem> transactionItems = entry.getValue();
            
            if (transactionItems.isEmpty()) {
                continue;
            }
            
            SaleItem firstItem = transactionItems.get(0);
            Instant billCreatedAt = firstItem.getCreatedAt();
            Instant billUpdatedAt = transactionItems.stream()
                    .map(SaleItem::getUpdatedAt)
                    .max(Comparator.naturalOrder())
                    .orElse(billCreatedAt);
            
            BigDecimal subtotal = BigDecimal.ZERO;
            List<BillInfoResponse.BillItem> billItems = new ArrayList<>();
            
            for (SaleItem saleItem : transactionItems) {
                Product product = productMap.get(saleItem.getProductId());
                if (product == null) {
                    continue;
                }
                
                BigDecimal itemTotal = saleItem.getPrice().multiply(
                    saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
                );
                subtotal = subtotal.add(itemTotal);
                
                BillInfoResponse.BillItem billItem = new BillInfoResponse.BillItem();
                billItem.setProductId(saleItem.getProductId().toString());
                billItem.setName(product.getName());
                billItem.setQuantity(saleItem.getQuantity() != null 
                    ? saleItem.getQuantity().intValue() 
                    : 1);
                billItem.setUnitPrice(saleItem.getPrice());
                billItem.setTotalPrice(itemTotal);
                billItems.add(billItem);
            }
            
            BigDecimal totalPaid = BigDecimal.ZERO;
            List<BillInfoResponse.PaymentInfo> paymentInfos = new ArrayList<>();
            
            for (SaleItem saleItem : transactionItems) {
                List<Payment> itemPayments = paymentsBySaleItem.getOrDefault(saleItem.getId(), List.of());
                for (Payment payment : itemPayments) {
                    totalPaid = totalPaid.add(payment.getAmount());
                    
                    BillInfoResponse.PaymentInfo paymentInfo = new BillInfoResponse.PaymentInfo();
                    paymentInfo.setPaymentId(payment.getId().toString());
                    paymentInfo.setDate(formatDate(payment.getTimestamp()));
                    paymentInfo.setAmount(payment.getAmount());
                    paymentInfo.setMode(payment.getMedium() != null ? payment.getMedium().name() : "UNKNOWN");
                    paymentInfos.add(paymentInfo);
                }
            }
            
            paymentInfos.sort(Comparator.comparing(BillInfoResponse.PaymentInfo::getDate));
            
            BigDecimal total = subtotal;
            BigDecimal discount = BigDecimal.ZERO;
            BigDecimal pending = total.subtract(totalPaid);
            
            SaleItemStatus overallStatus = determineStatus(totalPaid, total);
            
            String billNumber = generateBillNumber(transactionId, billCounter++);
            
            BillInfoResponse.BillData billData = new BillInfoResponse.BillData();
            billData.setId("bill_" + transactionId.substring(0, 8));
            billData.setBillNumber(billNumber);
            billData.setBillDate(formatDate(billCreatedAt));
            billData.setStatus(overallStatus.name());
            
            BillInfoResponse.BusinessInfo businessInfo = new BillInfoResponse.BusinessInfo();
            businessInfo.setId("biz_" + owner.getId().toString().substring(0, 8));
            businessInfo.setName(owner.getBusinessName());
            businessInfo.setPhone(owner.getPhone() != null ? formatPhone(owner.getPhone()) : "");
            billData.setBusiness(businessInfo);
            
            BillInfoResponse.CustomerInfo customerInfo = new BillInfoResponse.CustomerInfo();
            customerInfo.setId("cust_" + customer.getId().toString().substring(0, 8));
            customerInfo.setName(customer.getName());
            customerInfo.setPhone(customer.getPhone() != null ? formatPhone(customer.getPhone()) : "");
            customerInfo.setVillage(location != null && location.getVillage() != null 
                ? location.getVillage() 
                : "");
            billData.setCustomer(customerInfo);
            
            billData.setItems(billItems);
            
            BillInfoResponse.AmountDetails amountDetails = new BillInfoResponse.AmountDetails();
            amountDetails.setSubtotal(subtotal);
            amountDetails.setDiscount(discount);
            amountDetails.setTotal(total);
            amountDetails.setPaid(totalPaid);
            amountDetails.setPending(pending);
            billData.setAmounts(amountDetails);
            
            billData.setPayments(paymentInfos);
            billData.setCreatedAt(billCreatedAt);
            billData.setUpdatedAt(billUpdatedAt);
            
            BillInfoResponse billResponse = new BillInfoResponse();
            billResponse.setBill(billData);
            bills.add(billResponse);
        }
        
        return bills;
    }
    
    private SaleItemStatus determineStatus(BigDecimal paid, BigDecimal total) {
        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            return SaleItemStatus.NOT_PAID;
        } else if (paid.compareTo(total) >= 0) {
            return SaleItemStatus.FULLY_PAID;
        } else {
            return SaleItemStatus.PARTIALLY_PAID;
        }
    }
    
    private String generateBillNumber(String transactionId, int totalBills) {
        String suffix = String.format("%06d", totalBills);
        return BILL_PREFIX + suffix;
    }
    
    private String formatDate(Instant instant) {
        if (instant == null) {
            return "";
        }
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return date.format(DATE_FORMATTER);
    }
    
    private String formatPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }
        if (phone.length() >= 10) {
            String last10 = phone.length() > 10 ? phone.substring(phone.length() - 10) : phone;
            return "+91-" + last10.substring(0, 2) + "XXXXXX";
        }
        return phone;
    }
}

