package cz.cvt;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.jboss.logging.Logger;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.model.InvoiceItem;
import cz.cvt.pdf.service.impl.FacebookParserServiceImpl;

public class TestUtils {

    private static final Logger log = Logger.getLogger(TestUtils.class);

    private static final String FONT_LOCATION = "LiberationSans-Regular.ttf";
    private static final String SAMPLE_DATE = "Od 30. 12. 2018 13:00 do 19. 1. 2019 15:56";
    public static final String PDF_NAME = "test.pdf";

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static Invoice sampleInvoice() {

        Invoice invoice = new Invoice();
        invoice.accountId = randomString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FacebookParserServiceImpl.LOCAL_DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse("19. 1. 2019 15:56", formatter);
        invoice.paidOn = localDateTime;
        invoice.referentialNumber = randomString();
        invoice.totalPaid = new Double(36000.00);
        invoice.transactionId = randomString();
        invoice.currency = Currency.getInstance("CZK");

        InvoiceItem item = new InvoiceItem(randomString(), new Double(1950.86),
                FacebookParserServiceImpl.CZ_EVENT_PREFIX);
        invoice.addInvoiceItem(item);

        InvoiceItem item2 = new InvoiceItem(randomString(), new Double(257.97),
                FacebookParserServiceImpl.CZ_POST_PREFIX);
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
        addLine(contents, FacebookParserServiceImpl.ACCOUNT_ID + invoice.accountId);
        addLine(contents, FacebookParserServiceImpl.PAID_ON);
        addLine(contents, invoice.paidOn
                .format(DateTimeFormatter.ofPattern(FacebookParserServiceImpl.LOCAL_DATE_TIME_FORMAT)));
        addLine(contents, FacebookParserServiceImpl.REFERENTIAL_NUMBER + invoice.referentialNumber);
        addLine(contents, FacebookParserServiceImpl.TRANSACTION_ID);
        addLine(contents, invoice.transactionId);
        addLine(contents,
                FacebookParserServiceImpl.AMOUNT_PAID);
        addLine(contents, extractStringPrice(invoice.totalPaid)+ " (" + Currency.getInstance("CZK").getCurrencyCode() + ")");
        addLine(contents, FacebookParserServiceImpl.METADATA_END_DELIMITER);

        invoice.invoiceItems.forEach(item -> {

            try {
                addLine(contents, item.campaignName);
                addLine(contents, SAMPLE_DATE);
                addLine(contents, extractStringPrice(item.price));
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

    public static String extractStringPrice(Double price) {

        String tmp = String.valueOf(price);
        tmp = tmp.replace(".", ",");
        return tmp + " Kč";
    }
}
