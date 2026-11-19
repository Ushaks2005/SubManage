
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.model.Invoice;
import com.subscription.subscription_management.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(int invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId);

        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice not found with ID: " + invoiceId
            );
        }

        return invoice;
    }

    public Invoice createInvoice(Invoice invoice) {

        if (invoice == null) {
            throw new IllegalArgumentException("Invoice details are required");
        }

        if (invoice.getSubscriptionId() <= 0) {
            throw new IllegalArgumentException("Valid subscription ID is required");
        }

        if (invoice.getInvoiceDate() == null || invoice.getDueDate() == null) {
            throw new IllegalArgumentException("Invoice date and due date are required");
        }

        if (invoice.getDueDate().isBefore(invoice.getInvoiceDate())) {
            throw new IllegalArgumentException(
                    "Due date cannot be before invoice date"
            );
        }

        Map<String, Object> details =
                invoiceRepository.findSubscriptionDetails(invoice.getSubscriptionId());

        if (details == null) {
            throw new IllegalArgumentException("Subscription not found");
        }

        invoice.setCustomerId(
                ((Number) details.get("customer_id")).intValue()
        );

        invoice.setAmount(
                new java.math.BigDecimal(details.get("agreed_price").toString())
        );

        invoice.setInvoiceNumber(
                "INV-" + UUID.randomUUID()
                        .toString().substring(0, 12).toUpperCase()
        );

        invoice.setInvoiceStatus("UNPAID");

        return invoiceRepository.save(invoice);
    }
}
