package cz.cvt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.persistence.InvoiceRepository;
import cz.cvt.pdf.service.api.InvoiceParserService;
import cz.cvt.pdf.service.impl.FacebookParserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

import static cz.cvt.TestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class IntegrationTest {

    private File invoicePDF;
    private Invoice invoice;

    @Autowired
    private MockMvc mvc;

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
    public void fullIntegrationTest() throws Exception {

        InputStream input = new DataInputStream(new FileInputStream(invoicePDF));

        MockMultipartFile pdfInvoice = new MockMultipartFile(PDF_NAME, input);

        mvc.perform(multipart("/api/pdf/processInvoice").file("invoice", pdfInvoice.getBytes()))
                .andExpect(status().isOk());


    }

}