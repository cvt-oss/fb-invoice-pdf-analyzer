package cz.cvt;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.persistence.InvoiceRepository;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class RepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private InvoiceRepository invoiceRepository;
    private Invoice sampleInvoice;

    @Before
    public void setup() {
        sampleInvoice = TestUtils.sampleInvoice();
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

}