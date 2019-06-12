package cz.cvt.pdf.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    private static final String CAMPAING_DELIMITER_REGEXP = "Od\\s\\d{1,2}\\.\\s\\d{1,2}\\.\\s\\d{4}\\s\\d{1,2}:\\d{1,2}\\sdo\\s\\d{1,2}\\.\\s\\d{1,2}\\.\\s\\d{4}\\s\\d{1,2}:\\d{1,2}";
    private static final String METADATA_END_DELIMITER = "Kampaně";
    private static final String INVOICE_END = "Facebook Ireland Limited";
    private static final String PAID_ON = "Datum platby";
    private static final String TRANSACTION_ID = "ID transakce";
    private static final String AMOUNT_PAID = "Placeno";
    private static final String REFERENTIAL_NUMBER = "Referenční číslo: ";
    private static final String ACCOUNT_ID = "ID účtu: ";

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

        List<String> split = Arrays.asList(invoiceItems.split(CAMPAING_DELIMITER_REGEXP));

        for (int i = 0; i < split.size() - 1; i++) {

            InvoiceItem item = null;

            String current = split.get(i);
            String next = split.get(i + 1);

            if (i == 0) {

                String campaingName = current.trim();
                String totalPrice = next.split("\\n")[1];

                item = new InvoiceItem(campaingName, totalPrice);
                log.debug("invoice item found: \n {}", item);

            } else {
                String[] tmp = current.split("\n");
                String campaingName = tmp[tmp.length - 1];
                String totalPrice = next.split("\n")[1];
                item = new InvoiceItem(campaingName, totalPrice);
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
                invoice.setPaidOn(metadataLines.get(i + 1));
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