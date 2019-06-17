package cz.cvt;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.rest.InvoiceResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.DataInputStream;
import java.io.FileInputStream;

import static cz.cvt.TestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    private static ObjectMapper mapper;

    @BeforeClass
    public static void init() {

        mapper = new ObjectMapper();
        JavaTimeModule timeModule = new JavaTimeModule();
        mapper.registerModule(timeModule);

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
    public void fullIntegrationTest() throws Exception {

        InputStream input = new DataInputStream(new FileInputStream(invoicePDF));

        MockMultipartFile pdfInvoice = new MockMultipartFile(PDF_NAME, input);

        MvcResult postResult = mvc.perform(multipart("/api/pdf/invoice/process").file("invoice", pdfInvoice.getBytes()))
                .andExpect(status().isOk()).andReturn();

        InvoiceResponse r = mapper.readValue(postResult.getResponse().getContentAsString(), InvoiceResponse.class);

        MvcResult getResult = mvc.perform(get("/api/pdf/invoice/" + r.getId())).andExpect(status().isOk()).andReturn();
        Invoice returnedInvoice = mapper.readValue(getResult.getResponse().getContentAsString(), Invoice.class);
        assertThat(returnedInvoice).isEqualTo(invoice);

        Long fakeId = r.getId() + 1;

        mvc.perform(get("/api/pdf/invoice" + fakeId)).andExpect(status().isNotFound());

    }

}