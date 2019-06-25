package cz.cvt.pdf.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jboss.logging.Logger;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.model.InvoiceItem;
import cz.cvt.pdf.service.api.InvoiceParserService;

@ApplicationScoped
public class FacebookParserServiceImpl implements InvoiceParserService {

    private static final Logger log = Logger.getLogger(FacebookParserServiceImpl.class);

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
    public static final String CZ_POST_PREFIX = "Příspěvek: ";
    public static final String CZ_EVENT_PREFIX = "Událost: ";
    public static final String EN_POST_PREFIX = "Post: ";

    public Invoice parse(InputStream is) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        PDDocument doc = PDDocument.load(is);
        String invoiceText = stripper.getText(doc);

        String metadata = invoiceText.substring(0, invoiceText.indexOf(METADATA_END_DELIMITER));

        Invoice invoice = extractMetadata(metadata);

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
                String prefix = processPrefix(campaignName);

                switch (prefix) {
                case CZ_EVENT_PREFIX:
                    campaignName = campaignName.replace(CZ_EVENT_PREFIX, "");
                    break;
                case CZ_POST_PREFIX:
                case EN_POST_PREFIX:
                    campaignName = removePostPrefix(campaignName, prefix);
                    break;
                default:
                    break;
                }

                String totalPrice = next.split("\\n")[1];

                item = new InvoiceItem(campaignName, extractPrice(totalPrice), prefix);
                log.debugf("invoice item found: \n %s", item);

            } else {
                String[] tmp = current.split("\n");
                String campaignName = tmp[tmp.length - 1];
                String totalPrice = next.split("\n")[1];
                String prefix = processPrefix(campaignName);
                switch (prefix) {
                case CZ_EVENT_PREFIX:
                    campaignName = campaignName.replace(CZ_EVENT_PREFIX, "");
                    break;
                case CZ_POST_PREFIX:
                case EN_POST_PREFIX:
                    campaignName = removePostPrefix(campaignName, prefix);
                    break;
                default:
                    break;
                }

                item = new InvoiceItem(campaignName, extractPrice(totalPrice), prefix);
                log.debugf("invoice item found: \n %s", item);

            }

            if (item != null)
                invoice.addInvoiceItem(item);
        }
        doc.close();
        is.close();

        return invoice;
    }

    private Invoice extractMetadata(String metadata) {

        Invoice invoice = new Invoice();

        List<String> metadataLines = Arrays.asList(metadata.split("\n"));

        for (int i = 0; i < metadataLines.size(); i++) {
            String line = metadataLines.get(i);

            if (line.startsWith(ACCOUNT_ID)) {
                invoice.accountId = line.replace(ACCOUNT_ID, "");
                
            }

            else if (line.startsWith(REFERENTIAL_NUMBER)) {
                invoice.referentialNumber = line.replace(REFERENTIAL_NUMBER, "");
            }

            else if (line.startsWith(PAID_ON)) {
                String rawTimeString = metadataLines.get(i + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT);
                LocalDateTime localDateTime = LocalDateTime.parse(rawTimeString, formatter);
                invoice.paidOn = localDateTime;
            }

            else if (line.startsWith(TRANSACTION_ID)) {
                invoice.transactionId = metadataLines.get(i + 1);
            } else if (line.startsWith(AMOUNT_PAID)) {

                String rawPrice = metadataLines.get(i + 1);

                invoice.totalPaid = extractPrice(rawPrice);
                invoice.currency = extractCurrency(rawPrice);
            }
        }

        return invoice;
    }

    public Currency extractCurrency(String price) {
        String trimmedCurrency = price.substring(price.length() - 5);
        trimmedCurrency = trimmedCurrency.replace("(", "");
        trimmedCurrency = trimmedCurrency.replace(")", "");

        return Currency.getInstance(trimmedCurrency);
    }

    public Double extractPrice(String price) {

        String[] split = price.split(",");
        String intPart = split[0];
        String fractionalPart = split[1].substring(0, 2); // we only care about the first two numbers afer comma
        String finalStringPrice = intPart.trim() + "." + fractionalPart.trim();

        return Double.valueOf(finalStringPrice.replace(" ", ""));
    }

    /**
     * 
     * @param campaignName, i.e. Příspěvek: „Mezi námi žije spousta lidí,
     *                      kteří se potýkají s...“
     * @param prefix        i.e. Příspěvek:
     * @return i.e. Mezi námi žije spousta lidí, kteří se potýkají s...
     */
    public String removePostPrefix(String campaignName, String prefix) {

        String trimmedName = campaignName.replace(prefix, "");
        StringBuilder builder = new StringBuilder(trimmedName);
        // delete quotes at the start and end
        builder = builder.deleteCharAt(0);
        builder = builder.deleteCharAt(builder.length() - 1);
        trimmedName = builder.toString();
        return trimmedName;

    }

    public String processPrefix(String campaignName) {

        log.debugf("Processing prefix: %s", campaignName);
        if (campaignName.startsWith(CZ_EVENT_PREFIX)) {
            return CZ_EVENT_PREFIX;
        } else if (campaignName.startsWith(CZ_POST_PREFIX)) {
            return processPostPrefix(campaignName, CZ_POST_PREFIX);
        } else if (campaignName.startsWith(EN_POST_PREFIX)) {
            return processPostPrefix(campaignName, EN_POST_PREFIX);
        } else
            return "";
    }

    public String processPostPrefix(String campaignName, String prefix) {

        campaignName = campaignName.replace(prefix, "");
        StringBuilder builder = new StringBuilder(campaignName);
        // delete quotes at the start and end
        builder = builder.deleteCharAt(0);
        builder = builder.deleteCharAt(builder.length() - 1);
        campaignName = builder.toString();

        return prefix;

    }

}
