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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FacebookParserServiceImpl.LOCAL_DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse("19. 1. 2019 15:56", formatter);
        invoice.setPaidOn(localDateTime);
        invoice.setReferentialNumber(randomString());
        invoice.setTotalPaid(new Double(36000.00));
        invoice.setTransactionId(randomString());
        invoice.setCurrency(Currency.getInstance("CZK"));

        InvoiceItem item = new InvoiceItem(randomString(), new Double(1950.86), FacebookParserServiceImpl.EVENT_PREFIX);
        invoice.addInvoiceItem(item);

        InvoiceItem item2 = new InvoiceItem(randomString(), new Double(257.97), FacebookParserServiceImpl.POST_PREFIX);
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
        addLine(contents, invoice.getPaidOn()
                .format(DateTimeFormatter.ofPattern(FacebookParserServiceImpl.LOCAL_DATE_TIME_FORMAT)));
        addLine(contents, FacebookParserServiceImpl.REFERENTIAL_NUMBER + invoice.getReferentialNumber());
        addLine(contents, FacebookParserServiceImpl.TRANSACTION_ID);
        addLine(contents, invoice.getTransactionId());
        addLine(contents,
                FacebookParserServiceImpl.AMOUNT_PAID);
        addLine(contents, extractStringPrice(invoice.getTotalPaid())+ " (" + Currency.getInstance("CZK").getCurrencyCode() + ")");
        addLine(contents, FacebookParserServiceImpl.METADATA_END_DELIMITER);

        invoice.getInvoiceItems().forEach(item -> {

            try {
                addLine(contents, item.getCampaignName());
                addLine(contents, SAMPLE_DATE);
                addLine(contents, extractStringPrice(item.getPrice()));
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
