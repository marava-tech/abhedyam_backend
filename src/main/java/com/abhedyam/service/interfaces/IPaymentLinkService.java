package com.abhedyam.service.interfaces;

import com.abhedyam.dto.UpiPaymentLinkRequest;
import com.abhedyam.dto.UpiPaymentLinkResponse;

public interface IPaymentLinkService {
    UpiPaymentLinkResponse generateUpiPaymentLink(UpiPaymentLinkRequest request);
}

