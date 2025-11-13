package com.abhedyam.service.interfaces;

import com.abhedyam.dto.PaymentStatusUpdateRequest;
import com.abhedyam.model.Payment;

import java.util.List;
import java.util.UUID;

public interface IPaymentService {
    Payment create(Payment payment);
    Payment getById(UUID id);
    List<Payment> getAll();
    List<Payment> getByOwnerId(UUID ownerId);
    List<Payment> getByCustomerId(UUID customerId);
    Payment updateStatus(UUID id, PaymentStatusUpdateRequest request);
    Payment update(UUID id, Payment paymentDetails);
    void delete(UUID id);
}

