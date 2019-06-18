package cz.cvt.pdf.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.persistence.InvoiceRepository;
import cz.cvt.pdf.service.api.InvoiceParserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pdf")
@Slf4j
@Transactional
@Api(value = "Facebook PDF Invoice REST API", description = "Operations related to PDF invoices (processing, retrieving)")
public class PDFInvoiceAnalyzerApi {

    @Autowired
    private InvoiceParserService pdfService;
    @Autowired
    private InvoiceRepository invoiceRepository;

    Logger logger = LoggerFactory.getLogger(PDFInvoiceAnalyzerApi.class);

    @PostMapping(path="/invoice/process")@ApiOperation(value="Parse and store Facebook PDF invoice",response=List.class)

    @ApiResponses(value={
        @ApiResponse(code=200,message="Invoice processed succsesfully",response = InvoiceResponse.class),
        @ApiResponse(code=400,message="Bad Request")
    })

    public ResponseEntity<InvoiceResponse> processInvoice(@ApiParam(required = true, value = "Valid Facebook PDF invoice in Czech Language")@RequestParam("invoice") MultipartFile invoiceFile) {

        try {
            InputStream is = invoiceFile.getInputStream();
            Invoice invoice = pdfService.parse(is);
            invoice.setOriginalFileName(invoiceFile.getOriginalFilename());
            invoiceRepository.save(invoice);
            return ResponseEntity.ok().body(new InvoiceResponse(invoice.getId()));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/invoice/{id}")
    @ApiResponses(value={
        @ApiResponse(code=200,message="Invoice retrieved successfully}", response = Invoice.class),
        @ApiResponse(code=404,message = "Invoice not found")
    })
    public ResponseEntity<Invoice> getInvoiceById(@ApiParam(required = true, value = "Invoice ID")@PathVariable("id") Long id) {

        try {
            Invoice invoice = invoiceRepository.getOne(id);
            log.debug("found result:" + invoice);
            return ResponseEntity.ok().body(invoice);
        } catch (javax.persistence.EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }
    }

}