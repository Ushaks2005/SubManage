
package com.subscription.subscription_management;

import com.subscription.subscription_management.model.Payment;
import com.subscription.subscription_management.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentApiController {

    private final PaymentService paymentService;

    public PaymentApiController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable int id) {
        return ResponseEntity.ok(
                paymentService.getPaymentById(id)
        );
    }

    // Create payment
    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestBody Payment payment) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(payment));
    }

    // Update payment
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(
            @PathVariable int id,
            @RequestBody Payment payment) {
        return ResponseEntity.ok(
                paymentService.updatePayment(id, payment)
        );
    }

    // Update payment status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(
                        id, request.get("paymentStatus")
                )
        );
    }

    // Delete payment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable int id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
