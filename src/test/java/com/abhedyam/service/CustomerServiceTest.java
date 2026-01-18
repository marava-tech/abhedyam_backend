package com.abhedyam.service;

import com.abhedyam.dto.CustomerPaymentsSummaryResponse;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.CustomerResponse;
import com.abhedyam.model.Customer;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Payment;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.repository.NoteRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.ReminderRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private LocationDetailsRepository locationDetailsRepository;
    @Mock
    private SaleItemRepository saleItemRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private ReminderRepository reminderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private CustomerService customerService;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(
            customerRepository,
            locationDetailsRepository,
            saleItemRepository,
            paymentRepository,
            productRepository,
            noteRepository,
            reminderRepository,
            userRepository,
            redisTemplate,
            new ObjectMapper()
        );
        ownerId = UUID.randomUUID();
        UserPrincipal principal = new UserPrincipal(ownerId, "8888888888");
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getOwnerCustomersMapsVillage() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Kiran");
        customer.setOwnerId(ownerId);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());

        Page<Customer> page = new PageImpl<>(List.of(customer));
        when(customerRepository.searchCustomersWithVillage(eq(ownerId), eq("Kiran"), eq(false), any(Pageable.class)))
            .thenReturn(page);

        LocationDetails location = new LocationDetails();
        location.setUserId(customer.getId());
        location.setVillage("Madhapur");
        when(locationDetailsRepository.findByUserIdIn(List.of(customer.getId())))
            .thenReturn(List.of(location));

        PageResponse<CustomerResponse> response = customerService.getOwnerCustomers(
            ownerId, "Kiran", 0, 20, "createdAt", "DESC");

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getVillage()).isEqualTo("Madhapur");
    }

    @Test
    void getCustomerPaymentsSummaryCountsSuccessOnly() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setOwnerId(ownerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Payment successPayment = new Payment();
        successPayment.setStatus(PaymentStatus.SUCCESS);
        successPayment.setAmount(new BigDecimal("100.00"));
        Payment pendingPayment = new Payment();
        pendingPayment.setStatus(PaymentStatus.PENDING);
        pendingPayment.setAmount(new BigDecimal("50.00"));
        when(paymentRepository.findByCustomerIdAndOwnerId(customerId, ownerId))
            .thenReturn(List.of(successPayment, pendingPayment));

        CustomerPaymentsSummaryResponse response = customerService.getCustomerPaymentsSummary(ownerId, customerId);

        assertThat(response.getTotalPayments()).isEqualTo(1L);
        assertThat(response.getTotalPaid()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void getOwnerCustomersRejectsMismatchedOwner() {
        UUID otherOwnerId = UUID.randomUUID();
        assertThatThrownBy(() -> customerService.getOwnerCustomers(
            otherOwnerId, null, 0, 20, "createdAt", "DESC"))
            .isInstanceOf(RuntimeException.class);
    }
}


