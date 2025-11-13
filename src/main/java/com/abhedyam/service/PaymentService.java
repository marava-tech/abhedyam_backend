package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Payment;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.service.interfaces.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    
    private final PaymentRepository paymentRepository;
    
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public Payment getById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }
    
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getByOwnerId(UUID ownerId) {
        return paymentRepository.findByOwnerId(ownerId);
    }
    
    public List<Payment> getByCustomerId(UUID customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }
    
    @Transactional
    public Payment update(UUID id, Payment paymentDetails) {
        Payment payment = getById(id);
        if (paymentDetails.getAmount() != null) payment.setAmount(paymentDetails.getAmount());
        if (paymentDetails.getMedium() != null) payment.setMedium(paymentDetails.getMedium());
        if (paymentDetails.getReference() != null) payment.setReference(paymentDetails.getReference());
        if (paymentDetails.getStatus() != null) payment.setStatus(paymentDetails.getStatus());
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public void delete(UUID id) {
        Payment payment = getById(id);
        payment.setDeletedAt(Instant.now());
        payment.setIsActive(false);
        paymentRepository.save(payment);
    }
}

