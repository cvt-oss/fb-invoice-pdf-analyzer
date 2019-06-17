package cz.cvt.pdf.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.model.InvoiceItem;
import cz.cvt.pdf.service.api.InvoiceParserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FacebookParserServiceImpl implements InvoiceParserService {

    public FacebookParserServiceImpl() {
    }

    // Matches following string
    // Od 30. 12. 2018 13:00 do 19. 1. 2019 15:56
    public static final String CAMPAIGN_DELIMITER_REGEXP = "Od\\s\\d{1,2}\\.\\s\\d{1,2}\\.\\s\\d{4}\\s\\d{1,2}:\\d{1,2}\\sdo\\s\\d{1,2}\\.\\s\\d{1,2}\\.\\s\\d{4}\\s\\d{1,2}:\\d{1,2}";
    public static final String METADATA_END_DELIMITER = "Kampaně";
    public static final String INVOICE_END = "Facebook Ireland Limited";
    public static final String PAID_ON = "Datum platby";
    public static final String TRANSACTION_ID = "ID transakce";
    public static final String AMOUNT_PAID = "Placeno";
    public static final String REFERENTIAL_NUMBER = "Referenční číslo: ";
    public static final String ACCOUNT_ID = "ID účtu: ";
    public static final String LOCAL_DATE_TIME_FORMAT = "d. M. yyyy HH:mm";

    public Invoice parse(InputStream is) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        PDDocument doc = PDDocument.load(is);
        String invoiceText = stripper.getText(doc);
        log.debug("invoice text extracted :\n {} ", invoiceText);
        String metadata = invoiceText.substring(0, invoiceText.indexOf(METADATA_END_DELIMITER));

        Invoice invoice = extractMetadata(metadata);
        log.debug("invoice metadata extracted :\n {}", invoice.toString());

        String invoiceItems = invoiceText.substring(
                invoiceText.indexOf(METADATA_END_DELIMITER) + METADATA_END_DELIMITER.length(),
                invoiceText.indexOf(INVOICE_END));

        List<String> split = Arrays.asList(invoiceItems.split(CAMPAIGN_DELIMITER_REGEXP));

        for (int i = 0; i < split.size() - 1; i++) {

            InvoiceItem item = null;

            String current = split.get(i);
            String next = split.get(i + 1);

            if (i == 0) {

                String campaignName = current.trim();
                String totalPrice = next.split("\\n")[1];

                item = new InvoiceItem(campaignName, totalPrice);
                log.debug("invoice item found: \n {}", item);

            } else {
                String[] tmp = current.split("\n");
                String campaignName = tmp[tmp.length - 1];
                String totalPrice = next.split("\n")[1];
                item = new InvoiceItem(campaignName, totalPrice);
                log.debug("invoice item found: \n {}", item);

            }

            if (item != null)
                invoice.addInvoiceItem(item);
        }

        return invoice;
    }

    private Invoice extractMetadata(String metadata) {

        Invoice invoice = new Invoice();

        List<String> metadataLines = Arrays.asList(metadata.split("\n"));

        for (int i = 0; i < metadataLines.size(); i++) {
            String line = metadataLines.get(i);

            if (line.startsWith(ACCOUNT_ID)) {
                invoice.setAccountId(line.replace(ACCOUNT_ID, ""));
            }

            else if (line.startsWith(REFERENTIAL_NUMBER)) {
                invoice.setReferentialNumber(line.replace(REFERENTIAL_NUMBER, ""));
            }

            else if (line.startsWith(PAID_ON)) {
                String rawTimeString = metadataLines.get(i + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT);
                LocalDateTime localDateTime = LocalDateTime.parse(rawTimeString, formatter);
                invoice.setPaidOn(localDateTime);
            }

            else if (line.startsWith(TRANSACTION_ID)) {
                invoice.setTransactionId(metadataLines.get(i + 1));
            } else if (line.startsWith(AMOUNT_PAID)) {
                invoice.setTotalPaid(metadataLines.get(i + 1));
            }
        }

        return invoice;
    }

}
