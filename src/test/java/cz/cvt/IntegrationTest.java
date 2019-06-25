package cz.cvt;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import cz.cvt.pdf.model.Invoice;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.internal.util.IOUtils;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

@QuarkusTest

public class IntegrationTest {

    @Test
    @Disabled
    public void fullIntegrationTest() throws IOException {

        // disabled because of:
        // https://github.com/quarkusio/quarkus/issues/2949

        Invoice invoice = TestUtils.sampleInvoice();
        File invoicePDF = TestUtils.samplePDFInvoice(TestUtils.PDF_NAME, invoice);

        InputStream input = new DataInputStream(new FileInputStream(invoicePDF));

        byte[] invoiceByteArray = IOUtils.toByteArray(input);
        Response r = given().multiPart("invoice", TestUtils.PDF_NAME, invoiceByteArray).when()
                .post("/api/pdf/invoice/process").then().statusCode(200).extract().response();

        // TODO GET request

    }

}