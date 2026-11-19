
package com.subscription.subscription_management;

import com.subscription.subscription_management.model.Invoice;
import com.subscription.subscription_management.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceApiController {

    private final InvoiceService invoiceService;

    public InvoiceApiController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // Get all invoices
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(
                invoiceService.getAllInvoices()
        );
    }

    // Get invoice by ID
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(
            @PathVariable int id) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceById(id)
        );
    }

    // Create invoice
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestBody Invoice invoice) {

        return ResponseEntity.status(201)
                .body(invoiceService.createInvoice(invoice));
    }

}
