package com.travel_agent.services;

import com.travel_agent.repositories.TransactionInfoRepository;
import com.travel_agent.repositories.UserRepository;
import com.travel_agent.repositories.booking.BookingHotelRepository;
import com.travel_agent.repositories.booking.BookingShipRepository;
import com.travel_agent.services.email.EmailService;
import com.travel_agent.services.vnpay.VNPayService;
import com.travel_agent.models.entity.UserEntity;
import com.travel_agent.models.entity.TransactionInfo;
import com.travel_agent.models.entity.booking.BookingHotelEntity;
import com.travel_agent.models.entity.booking.BookingShipEntity;
import com.travel_agent.configs.VNPayConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("VNPay Service Unit Tests")
class VNPayServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private TransactionInfoRepository transactionInfoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingHotelRepository bookingHotelRepository;

    @Mock
    private BookingShipRepository bookingShipRepository;

    @InjectMocks
    private VNPayService vnPayService;

    private UserEntity testUser;
    private BookingHotelEntity testHotelBooking;
    private BookingShipEntity testShipBooking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        testUser = new UserEntity();
        testUser.setUserId(1);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");

        // Setup test hotel booking
        testHotelBooking = new BookingHotelEntity();
        testHotelBooking.setBookingId(100);
        testHotelBooking.setEmail("test@example.com");
        testHotelBooking.setState("PENDING");
        testHotelBooking.setTotalAmount(1000000);

        // Setup test ship booking
        testShipBooking = new BookingShipEntity();
        testShipBooking.setBookingId(200);
        testShipBooking.setEmail("test@example.com");
        testShipBooking.setState("PENDING");
        testShipBooking.setTotalAmount(2000000);
    }

    @Test
    @DisplayName("Should create payment URL successfully")
    void testCreatePayment_Success() throws UnsupportedEncodingException {
        // Given
        long amount = 1000000;
        String orderId = "100";
        String customerEmail = "test@example.com";
        String bookingType = "hotel";

        // When
        String paymentUrl = vnPayService.createPayment(amount, orderId, customerEmail, bookingType);

        // Then
        assertNotNull(paymentUrl, "Payment URL should not be null");
        assertTrue(paymentUrl.startsWith(VNPayConfig.vnp_PayUrl), "Payment URL should start with VNPay base URL");
        assertTrue(paymentUrl.contains("vnp_Amount="), "Payment URL should contain amount parameter");
        assertTrue(paymentUrl.contains("vnp_OrderInfo="), "Payment URL should contain order info");
        assertTrue(paymentUrl.contains("vnp_SecureHash="), "Payment URL should contain secure hash");
        assertTrue(paymentUrl.contains(bookingType), "Payment URL should contain booking type");
    }

    @Test
    @DisplayName("Should save transaction info when user exists")
    void testSaveTransactionInfo_UserExists() {
        // Given
        String paymentMethod = "VNPay";
        String content = "test@example.com|hotel|order100";
        String date = "20231207162830";
        String userEmail = "test@example.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(transactionInfoRepository.save(any(TransactionInfo.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        vnPayService.saveTransactionInfo(paymentMethod, content, date, userEmail);

        // Then
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(transactionInfoRepository, times(1)).save(any(TransactionInfo.class));
    }

    @Test
    @DisplayName("Should not save transaction info when user does not exist")
    void testSaveTransactionInfo_UserNotFound() {
        // Given
        String paymentMethod = "VNPay";
        String content = "test@example.com|hotel|order100";
        String date = "20231207162830";
        String userEmail = "nonexistent@example.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // When
        vnPayService.saveTransactionInfo(paymentMethod, content, date, userEmail);

        // Then
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(transactionInfoRepository, never()).save(any(TransactionInfo.class));
    }

    @Test
    @DisplayName("Should handle payment success and update hotel booking status")
    void testHandlePaymentSuccess_HotelBooking() {
        // Given
        String date = "20231207162830";
        String content = "test@example.com|hotel|order100";
        String customerEmail = "test@example.com";
        String orderId = "100";

        when(userRepository.findByEmail(customerEmail)).thenReturn(Optional.of(testUser));
        when(bookingHotelRepository.findById(100)).thenReturn(Optional.of(testHotelBooking));
        doNothing().when(emailService).sendSuccessOrderConfirmationEmail(anyString(), anyString());

        // When
        vnPayService.handlePaymentSuccess(date, content, customerEmail, orderId);

        // Then
        verify(bookingHotelRepository, times(1)).findById(100);
        verify(bookingHotelRepository, times(1)).save(any(BookingHotelEntity.class));
        verify(bookingShipRepository, never()).findById(anyInt());
        verify(emailService, times(1)).sendSuccessOrderConfirmationEmail(customerEmail, orderId);
        assertEquals("PAID", testHotelBooking.getState(), "Hotel booking status should be updated to PAID");
    }

    @Test
    @DisplayName("Should handle payment success and update ship booking status")
    void testHandlePaymentSuccess_ShipBooking() {
        // Given
        String date = "20231207162830";
        String content = "test@example.com|ship|order200";
        String customerEmail = "test@example.com";
        String orderId = "200";

        when(userRepository.findByEmail(customerEmail)).thenReturn(Optional.of(testUser));
        when(bookingShipRepository.findById(200)).thenReturn(Optional.of(testShipBooking));
        doNothing().when(emailService).sendSuccessOrderConfirmationEmail(anyString(), anyString());

        // When
        vnPayService.handlePaymentSuccess(date, content, customerEmail, orderId);

        // Then
        verify(bookingShipRepository, times(1)).findById(200);
        verify(bookingShipRepository, times(1)).save(any(BookingShipEntity.class));
        verify(bookingHotelRepository, never()).findById(anyInt());
        verify(emailService, times(1)).sendSuccessOrderConfirmationEmail(customerEmail, orderId);
        assertEquals("PAID", testShipBooking.getState(), "Ship booking status should be updated to PAID");
    }

    @Test
    @DisplayName("Should not update booking when booking type is unknown")
    void testHandlePaymentSuccess_UnknownBookingType() {
        // Given
        String date = "20231207162830";
        String content = "test@example.com|unknown|order100";
        String customerEmail = "test@example.com";
        String orderId = "100";

        when(userRepository.findByEmail(customerEmail)).thenReturn(Optional.of(testUser));

        // When
        vnPayService.handlePaymentSuccess(date, content, customerEmail, orderId);

        // Then
        verify(bookingHotelRepository, never()).findById(anyInt());
        verify(bookingShipRepository, never()).findById(anyInt());
        verify(emailService, times(1)).sendSuccessOrderConfirmationEmail(customerEmail, orderId);
    }

    @Test
    @DisplayName("Should handle payment failure and send failure email")
    void testHandlePaymentFail() {
        // Given
        String customerEmail = "test@example.com";
        String orderId = "100";

        doNothing().when(emailService).sendFailedOrderConfirmationEmail(anyString(), anyString());

        // When
        vnPayService.handlePaymentFail(customerEmail, orderId);

        // Then
        verify(emailService, times(1)).sendFailedOrderConfirmationEmail(customerEmail, orderId);
        verify(bookingHotelRepository, never()).findById(anyInt());
        verify(bookingShipRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Should handle invalid date format gracefully")
    void testSaveTransactionInfo_InvalidDateFormat() {
        // Given
        String paymentMethod = "VNPay";
        String content = "test@example.com|hotel|order100";
        String invalidDate = "invalid-date";
        String userEmail = "test@example.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(transactionInfoRepository.save(any(TransactionInfo.class))).thenAnswer(i -> {
            TransactionInfo saved = (TransactionInfo) i.getArguments()[0];
            assertEquals(LocalDate.now(), saved.getDates(), "Should use current date when parsing fails");
            return saved;
        });

        // When
        vnPayService.saveTransactionInfo(paymentMethod, content, invalidDate, userEmail);

        // Then
        verify(transactionInfoRepository, times(1)).save(any(TransactionInfo.class));
    }

    @Test
    @DisplayName("Should convert amount correctly (multiply by 100)")
    void testCreatePayment_AmountConversion() throws UnsupportedEncodingException {
        // Given
        long amount = 1000000; // 1,000,000 VND
        String orderId = "100";
        String customerEmail = "test@example.com";
        String bookingType = "hotel";

        // When
        String paymentUrl = vnPayService.createPayment(amount, orderId, customerEmail, bookingType);

        // Then
        assertTrue(paymentUrl.contains("vnp_Amount=100000000"),
                "Amount should be multiplied by 100 (1,000,000 * 100 = 100,000,000)");
    }

    @Test
    @DisplayName("Should handle invalid booking ID format")
    void testHandlePaymentSuccess_InvalidBookingId() {
        // Given
        String date = "20231207162830";
        String content = "test@example.com|hotel|orderABC";
        String customerEmail = "test@example.com";
        String orderId = "ABC"; // Invalid - not a number

        when(userRepository.findByEmail(customerEmail)).thenReturn(Optional.of(testUser));

        // When
        vnPayService.handlePaymentSuccess(date, content, customerEmail, orderId);

        // Then
        verify(bookingHotelRepository, never()).findById(anyInt());
        verify(bookingShipRepository, never()).findById(anyInt());
        verify(emailService, times(1)).sendSuccessOrderConfirmationEmail(customerEmail, orderId);
    }

    @Test
    @DisplayName("Should not update booking when booking not found")
    void testHandlePaymentSuccess_BookingNotFound() {
        // Given
        String date = "20231207162830";
        String content = "test@example.com|hotel|order999";
        String customerEmail = "test@example.com";
        String orderId = "999";

        when(userRepository.findByEmail(customerEmail)).thenReturn(Optional.of(testUser));
        when(bookingHotelRepository.findById(999)).thenReturn(Optional.empty());

        // When
        vnPayService.handlePaymentSuccess(date, content, customerEmail, orderId);

        // Then
        verify(bookingHotelRepository, times(1)).findById(999);
        verify(bookingHotelRepository, never()).save(any(BookingHotelEntity.class));
        verify(emailService, times(1)).sendSuccessOrderConfirmationEmail(customerEmail, orderId);
    }
}
