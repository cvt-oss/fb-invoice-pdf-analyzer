package cz.cvt;

import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.model.InvoiceItem;
import cz.cvt.pdf.persistence.InvoiceRepository;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private InvoiceRepository invoiceRepository;
    private Invoice sampleInvoice;

    @Before
    public void setup() {
        sampleInvoice = sampleInvoice();
    }

    @Test
    public void findInvoice() {

        em.persist(sampleInvoice);
        em.flush();
        Optional<Invoice> found = invoiceRepository.findById(sampleInvoice.getId());
        Invoice invoice = found.get();

        assertThat(invoice).isNotNull();
        assertThat(invoice).isEqualTo(sampleInvoice);

    }

    @Test
    public void storeInvoice() {

        invoiceRepository.save(sampleInvoice);
        Invoice found = em.find(Invoice.class, sampleInvoice.getId());

        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(sampleInvoice);
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

        return invoice;

    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

}