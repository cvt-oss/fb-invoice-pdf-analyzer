package cz.cvt.pdf.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.cvt.pdf.model.InvoiceItem;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem,Long> {

    
}