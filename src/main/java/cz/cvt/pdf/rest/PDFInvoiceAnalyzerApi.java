package cz.cvt.pdf.rest;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.persistence.InvoiceRepository;
import cz.cvt.pdf.service.api.InvoiceParserService;

@RestController
@RequestMapping("/api/pdf")
public class PDFInvoiceAnalyzerApi {

    @Autowired
    private InvoiceParserService pdfService;
    @Autowired
    private InvoiceRepository invoiceRepository;

   Logger logger = LoggerFactory.getLogger(PDFInvoiceAnalyzerApi.class);


    @PostMapping("/processInvoice")
    public ResponseEntity<String> processInvoice(@RequestParam("invoice") MultipartFile invoiceFile) {

        try {
            InputStream is = invoiceFile.getInputStream();
            Invoice invoice = pdfService.parse(is);
            invoiceRepository.save(invoice);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

}