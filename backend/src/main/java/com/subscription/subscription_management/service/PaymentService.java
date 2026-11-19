
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.model.Payment;
import com.subscription.subscription_management.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private static final Set<String> VALID_STATUSES =
            Set.of("PENDING", "SUCCESS", "FAILED");

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get one payment by ID
    public Payment getPaymentById(int paymentId) {
        Payment payment = paymentRepository.findById(paymentId);

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment not found with ID: " + paymentId
            );
        }

        return payment;
    }

    // Create payment
    public Payment createPayment(Payment payment) {
        validatePayment(payment);

        if (!paymentRepository.subscriptionExists(
                payment.getSubscriptionId())) {
            throw new IllegalArgumentException(
                    "Subscription not found with ID: "
                            + payment.getSubscriptionId()
            );
        }

        if (payment.getPaymentStatus() == null ||
                payment.getPaymentStatus().isBlank()) {
            payment.setPaymentStatus("PENDING");
        }

        validateStatus(payment.getPaymentStatus());

        return paymentRepository.save(payment);
    }

    // Update payment
    public Payment updatePayment(int paymentId, Payment payment) {
        getPaymentById(paymentId);
        validatePayment(payment);

        if (!paymentRepository.subscriptionExists(
                payment.getSubscriptionId())) {
            throw new IllegalArgumentException(
                    "Subscription not found with ID: "
                            + payment.getSubscriptionId()
            );
        }

        validateStatus(payment.getPaymentStatus());

        boolean updated = paymentRepository.update(paymentId, payment);

        if (!updated) {
            throw new IllegalArgumentException("Payment update failed");
        }

        return getPaymentById(paymentId);
    }

    // Update payment status
    public Payment updatePaymentStatus(int paymentId, String status) {
        getPaymentById(paymentId);
        validateStatus(status);

        paymentRepository.updateStatus(
                paymentId, status.toUpperCase()
        );

        return getPaymentById(paymentId);
    }

    // Delete payment
    public void deletePayment(int paymentId) {
        getPaymentById(paymentId);
        paymentRepository.delete(paymentId);
    }

    // Validate payment details
    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment details are required"
            );
        }

        if (payment.getSubscriptionId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid subscription ID is required"
            );
        }

        if (payment.getAmount() == null ||
                payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        if (payment.getPaymentDate() == null) {
            throw new IllegalArgumentException(
                    "Payment date is required"
            );
        }

        if (payment.getPaymentMethod() == null ||
                payment.getPaymentMethod().isBlank()) {
            throw new IllegalArgumentException(
                    "Payment method is required"
            );
        }
    }

    // Validate payment status
    private void validateStatus(String status) {
        if (status == null ||
                !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Invalid payment status. Use PENDING, SUCCESS or FAILED"
            );
        }
    }
}
