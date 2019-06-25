package cz.cvt;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.service.api.InvoiceParserService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)

public class FacebookParserServiceTest {

    private static final String PDF_NAME = "test.pdf";
    private static final Logger log = LoggerFactory.getLogger(FacebookParserServiceTest.class);

    @Inject
    InvoiceParserService parser;

    @Test
    @Disabled
    public void parsePDF() throws FileNotFoundException, IOException {

        // disabled due to:
        // https://github.com/quarkusio/quarkus/issues/2949
        File invoicePDF;
        Invoice invoice;

        invoice = TestUtils.sampleInvoice();
        invoicePDF = TestUtils.samplePDFInvoice(PDF_NAME, invoice);

         Invoice result = parser.parse(new DataInputStream(new FileInputStream(invoicePDF)));
    
         assertThat(result).isEqualTo(invoice);
    }

}