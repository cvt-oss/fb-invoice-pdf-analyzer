package cz.cvt;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.service.api.InvoiceParserService;
import cz.cvt.pdf.service.impl.FacebookParserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import static cz.cvt.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Slf4j
public class FacebookParserServiceTest {

    private static final String PDF_NAME = "test.pdf";

    @Autowired
    private InvoiceParserService parser;

    private File invoicePDF;
    private Invoice invoice;

    @TestConfiguration
    static class FacebookParserServiceTestConfiguration {

        @Bean
        public InvoiceParserService employeeService() {
            return new FacebookParserServiceImpl();
        }
    }

    @Before
    public void setup() throws IOException {
        invoice = sampleInvoice();
        invoicePDF = samplePDFInvoice(PDF_NAME, invoice);

    }

    @After
    public void cleanup() {

        invoicePDF.delete();
    }

    @Test
    public void parsePDFTest() throws FileNotFoundException, IOException {
        Invoice result = parser.parse(new DataInputStream(new FileInputStream(invoicePDF)));
        assertThat(result).isEqualTo(invoice);
    }

}