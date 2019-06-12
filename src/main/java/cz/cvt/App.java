package cz.cvt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cz.cvt.pdf.model.InvoiceItem;

@SpringBootApplication
public class App {

  // Matches following string
  // Od 30. 12. 2018 13:00 do 19. 1. 2019 15:56
  private static final String CAMPAING_DELIMITER_REGEXP = "Od\\s\\d{1,2}\\.\\s\\d{1,2}\\.\\s\\d{4}\\s\\d{1,2}:\\d{1,2}\\sdo\\s\\d{1,2}\\.\\s\\d{1,2}\\.\\s\\d{4}\\s\\d{1,2}:\\d{1,2}";
  private static final String METADATA_END_DELIMITER = "Kampaně";
  private static final String INVOICE_END = "Facebook Ireland Limited";
  private static final String PAID_ON = "Datum platby";
  private static final String TRANSACTION_ID = "ID transakce";
  private static final String AMOUNT_PAID = "PLACENO";
  private static final String REFERENTIAL_NUMBER = "Referenční číslo: ";
  private static final String ACCOUNT_ID = "ID účtu: ";

  public static void main(String[] args) throws InvalidPasswordException, IOException {
    SpringApplication.run(App.class, args);

    // InputStream inputStream =
    // App.class.getClassLoader().getResourceAsStream("invoice.pdf");
    // App.parse(inputStream);
  }

  public static void parse(InputStream is) throws IOException {
    PDFTextStripper stripper = new PDFTextStripper();
    PDDocument doc = PDDocument.load(is);

    String invoiceText = stripper.getText(doc);

    String metadata = invoiceText.substring(0, invoiceText.indexOf(METADATA_END_DELIMITER));

    String invoiceItems = invoiceText.substring(
        invoiceText.indexOf(METADATA_END_DELIMITER) + METADATA_END_DELIMITER.length(),
        invoiceText.indexOf(INVOICE_END));

    List<String> split = Arrays.asList(invoiceItems.split(CAMPAING_DELIMITER_REGEXP));
    List<InvoiceItem> campaings = new ArrayList<InvoiceItem>();

    for (int i = 0; i < split.size() - 1; i++) {

      InvoiceItem item = null;

      String current = split.get(i);
      String next = split.get(i + 1);

      if (i == 0) {

        String campaingName = current.trim();
        String totalPrice = next.split("\\n")[1];

        item = new InvoiceItem(campaingName, totalPrice);
        System.out.println(item);

      } else {
        String[] tmp = current.split("\n");
        String campaingName = tmp[tmp.length - 1];
        String totalPrice = next.split("\n")[1];
        item = new InvoiceItem(campaingName, totalPrice);

      }

      if (item != null)
        campaings.add(item);
    }

    campaings.forEach(i -> {

      System.out.println(i);
    });
  }

}
