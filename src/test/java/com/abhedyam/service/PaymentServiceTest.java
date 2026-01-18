package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.PaymentResponse;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.enums.PaymentMedium;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.security.UserPrincipal;
import com.abhedyam.service.interfaces.IAuditService;
import com.abhedyam.service.interfaces.ICustomerService;
import com.abhedyam.service.interfaces.IFcmService;
import com.abhedyam.service.interfaces.INotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SaleItemRepository saleItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IAuditService auditService;
    @Mock
    private INotificationService notificationService;
    @Mock
    private IFcmService fcmService;
    @Mock
    private ICustomerService customerService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private PaymentService paymentService;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
            paymentRepository,
            customerRepository,
            productRepository,
            saleItemRepository,
            userRepository,
            auditService,
            notificationService,
            fcmService,
            customerService,
            redisTemplate
        );
        ownerId = UUID.randomUUID();
        UserPrincipal principal = new UserPrincipal(ownerId, "9999999999");
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getOwnerPaymentsWithoutExpandReturnsCoreFields() {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setOwnerId(ownerId);
        payment.setCustomerId(UUID.randomUUID());
        payment.setSaleItemId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setMedium(PaymentMedium.CASH);
        payment.setTimestamp(Instant.now());
        payment.setStatus(PaymentStatus.SUCCESS);

        Page<Payment> page = new PageImpl<>(List.of(payment));
        when(paymentRepository.findByOwnerId(eq(ownerId), any(Pageable.class))).thenReturn(page);

        PageResponse<PaymentResponse> response = paymentService.getOwnerPayments(
            ownerId, null, 0, 20, "createdAt", "DESC", false);

        assertThat(response.getContent()).hasSize(1);
        PaymentResponse paymentResponse = response.getContent().get(0);
        assertThat(paymentResponse.getCustomerName()).isNull();
        assertThat(paymentResponse.getProductName()).isNull();
    }

    @Test
    void getOwnerPaymentsWithExpandReturnsNames() {
        UUID customerId = UUID.randomUUID();
        UUID saleItemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setOwnerId(ownerId);
        payment.setCustomerId(customerId);
        payment.setSaleItemId(saleItemId);
        payment.setAmount(new BigDecimal("200.00"));
        payment.setMedium(PaymentMedium.UPI);
        payment.setTimestamp(Instant.now());
        payment.setStatus(PaymentStatus.SUCCESS);

        Page<Payment> page = new PageImpl<>(List.of(payment));
        when(paymentRepository.findByOwnerId(eq(ownerId), any(Pageable.class))).thenReturn(page);

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Ravi");
        when(customerRepository.findByIdIn(List.of(customerId))).thenReturn(List.of(customer));

        SaleItem saleItem = new SaleItem();
        saleItem.setId(saleItemId);
        saleItem.setProductId(productId);
        when(saleItemRepository.findByIdIn(List.of(saleItemId))).thenReturn(List.of(saleItem));

        Product product = new Product();
        product.setId(productId);
        product.setName("Rice");
        when(productRepository.findByIdIn(List.of(productId))).thenReturn(List.of(product));

        PageResponse<PaymentResponse> response = paymentService.getOwnerPayments(
            ownerId, null, 0, 20, "createdAt", "DESC", true);

        assertThat(response.getContent()).hasSize(1);
        PaymentResponse paymentResponse = response.getContent().get(0);
        assertThat(paymentResponse.getCustomerName()).isEqualTo("Ravi");
        assertThat(paymentResponse.getProductName()).isEqualTo("Rice");
    }

    @Test
    void getOwnerPaymentsRejectsMismatchedOwner() {
        UUID otherOwnerId = UUID.randomUUID();
        assertThatThrownBy(() -> paymentService.getOwnerPayments(
            otherOwnerId, null, 0, 20, "createdAt", "DESC", false))
            .isInstanceOf(RuntimeException.class);
    }
}


