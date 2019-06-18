package cz.cvt;

import static cz.cvt.TestUtils.PDF_NAME;
import static cz.cvt.TestUtils.sampleInvoice;
import static cz.cvt.TestUtils.samplePDFInvoice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.rest.InvoiceResponse;
import lombok.extern.slf4j.Slf4j;

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

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = TestPostgresSQLContainer.getInstance();

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

    @Test
    public void swaggerDevTest() throws Exception {
        assumeTrue(activeProfile.contains("swagger"));
        log.info("executing swagger dev test");
        mvc.perform(get("/swagger-ui.html#/pdf-invoice-analyzer-api")).andExpect(status().isOk());

    }

    @Test
    public void swaggerProdTest() throws Exception {
        assumeTrue(!activeProfile.contains("swagger"));
        log.info("executing swagger prod test");
        mvc.perform(get("/swagger-ui.html#/pdf-invoice-analyzer-api")).andExpect(status().isNotFound());

    }

}