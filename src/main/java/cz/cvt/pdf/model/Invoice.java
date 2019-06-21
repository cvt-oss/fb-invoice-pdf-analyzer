package cz.cvt.pdf.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Entity
@RegisterForReflection
public class Invoice extends PanacheEntity {

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice", fetch = FetchType.EAGER)
  public List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();

  public String accountId;
  public String transactionId;
  public Double totalPaid;
  public Currency currency;
  public LocalDateTime paidOn;
  public String referentialNumber;
  public String originalFileName;

  public void addInvoiceItem(InvoiceItem item) {
    invoiceItems.add(item);
    item.invoice = this;

  }

  public void removeInvoiceItem(InvoiceItem item) {
    invoiceItems.remove(item);
    item.invoice = null;
  }
}