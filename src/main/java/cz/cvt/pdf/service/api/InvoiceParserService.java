package cz.cvt.pdf.service.api;

import java.io.IOException;
import java.io.InputStream;

import cz.cvt.pdf.model.Invoice;


public interface InvoiceParserService {

    public Invoice parse (InputStream invoicePDFStream) throws IOException;

    
}