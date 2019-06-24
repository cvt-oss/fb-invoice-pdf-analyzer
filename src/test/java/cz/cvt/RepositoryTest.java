package cz.cvt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cvt.pdf.model.Invoice;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(RepositoryTest.class);
    private Invoice sampleInvoice;
    @Inject
    EntityManager em;

    @BeforeEach
    public void setup() {
        sampleInvoice = TestUtils.sampleInvoice();
    }

    @Test
    @Transactional
    @Disabled
    public void findInvoice() {

        // This doesn't work due to:
        // https://github.com/quarkusio/quarkus/issues/1724
        assertNull(sampleInvoice.id);
        em.persist(sampleInvoice);
        Invoice foundInvoice = Invoice.findById(sampleInvoice.id);
        assertNotNull(foundInvoice);
        assertEquals(sampleInvoice, foundInvoice);

    }

    @Test
    @Transactional
    public void storeInvoice() {
        Invoice.persist(sampleInvoice);
        Invoice found = em.find(Invoice.class, sampleInvoice.id);
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(sampleInvoice);
    }

}