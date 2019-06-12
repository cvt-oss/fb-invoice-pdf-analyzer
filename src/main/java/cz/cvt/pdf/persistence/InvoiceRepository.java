package cz.cvt.pdf.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.cvt.pdf.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice,Long>{

    
}