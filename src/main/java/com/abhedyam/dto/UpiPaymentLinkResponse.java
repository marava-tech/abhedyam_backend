package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpiPaymentLinkResponse {
    private String paymentLink;
    private String paymentId;
    private String orderId;
}

