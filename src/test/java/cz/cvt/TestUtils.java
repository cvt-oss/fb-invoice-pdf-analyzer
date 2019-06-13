package cz.cvt;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.model.InvoiceItem;
import cz.cvt.pdf.service.impl.FacebookParserServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestUtils {

    private static final String FONT_LOCATION = "LiberationSans-Regular.ttf";
    private static final String SAMPLE_DATE = "Od 30. 12. 2018 13:00 do 19. 1. 2019 15:56";
    public static final String PDF_NAME = "test.pdf";


    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static Invoice sampleInvoice() {

        Invoice invoice = new Invoice();
        invoice.setAccountId(randomString());
        invoice.setPaidOn("1.1.1971 10:00");
        invoice.setReferentialNumber(randomString());
        invoice.setTotalPaid("100 CZK");
        invoice.setTransactionId(randomString());

        InvoiceItem item = new InvoiceItem(randomString(), "100 CZK");
        invoice.addInvoiceItem(item);

        InvoiceItem item2 = new InvoiceItem(randomString(), "200 CZK");
        invoice.addInvoiceItem(item2);

        return invoice;

    }

    public static File samplePDFInvoice(String name, Invoice invoice) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDType0Font font = PDType0Font.load(doc, TestUtils.class.getClassLoader().getResourceAsStream(FONT_LOCATION));

        PDPageContentStream contents = new PDPageContentStream(doc, page);
        contents.beginText();
        contents.setFont(font, 12);
        contents.setLeading(12 * 1.2f);
        contents.newLineAtOffset(50, 600);

        // populate the invoice with valid attributes but fake values
        addLine(contents, FacebookParserServiceImpl.ACCOUNT_ID + invoice.getAccountId());
        addLine(contents, FacebookParserServiceImpl.PAID_ON);
        addLine(contents, invoice.getPaidOn());
        addLine(contents, FacebookParserServiceImpl.REFERENTIAL_NUMBER + invoice.getReferentialNumber());
        addLine(contents, FacebookParserServiceImpl.TRANSACTION_ID);
        addLine(contents, invoice.getTransactionId());
        addLine(contents, FacebookParserServiceImpl.AMOUNT_PAID);
        addLine(contents, invoice.getTotalPaid());
        addLine(contents, FacebookParserServiceImpl.METADATA_END_DELIMITER);

        invoice.getInvoiceItems().forEach(item -> {

            try {
                addLine(contents, item.getCampaingName());
                addLine(contents, SAMPLE_DATE);
                addLine(contents, item.getPrice());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        contents.showText(FacebookParserServiceImpl.INVOICE_END);

        contents.endText();
        contents.close();
        doc.save(name);
        doc.close();

        return new File(name);
    }

    public static void addLine(PDPageContentStream stream, String content) throws IOException {

        stream.showText(content);
        stream.newLine();
    }
}