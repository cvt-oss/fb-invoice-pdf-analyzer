package cz.cvt.pdf.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@SequenceGenerator(name = "invoiceIdSeq", sequenceName = "INVOICE_SEQUENCE", initialValue = 1,allocationSize = 1)

public class Invoice {

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice")
  @JsonManagedReference
  private List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceIdSeq")
  private Long id;

  private String accountId;
  private String transactionId;
  private Double totalPaid;

  private LocalDateTime paidOn;
  private String referentialNumber;
  private String originalFileName;

  public void setId(Long id) {
    this.id = id;
  }

  public String getOriginalFileName() {
    return this.originalFileName;
  }

  public void setOriginalFileName(String originalFileName) {
    this.originalFileName = originalFileName;
  }

  public void addInvoiceItem(InvoiceItem item) {
    invoiceItems.add(item);
    item.setInvoice(this);
  }

  public void removeInvoiceItem(InvoiceItem item) {
    invoiceItems.remove(item);
    item.setInvoice(null);
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public Double getTotalPaid() {
    return totalPaid;
  }

  public void setTotalPaid(Double totalPaid) {
    this.totalPaid = totalPaid;
  }

  public LocalDateTime getPaidOn() {
    return paidOn;
  }

  public void setPaidOn(LocalDateTime paidOn) {
    this.paidOn = paidOn;
  }

  public List<InvoiceItem> getInvoiceItems() {
    return invoiceItems;
  }

  public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
    this.invoiceItems = invoiceItems;
  }

  public String getReferentialNumber() {
    return referentialNumber;
  }

  public void setReferentialNumber(String referentialNumber) {
    this.referentialNumber = referentialNumber;
  }

  public Long getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Invoice)) {
      return false;
    }
    Invoice invoice = (Invoice) o;
    return Objects.equals(accountId, invoice.accountId) && Objects.equals(transactionId, invoice.transactionId)
        && Objects.equals(totalPaid, invoice.totalPaid) && Objects.equals(paidOn, invoice.paidOn)
        && Objects.equals(referentialNumber, invoice.referentialNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, transactionId, totalPaid, paidOn, referentialNumber);
  }

  @Override
  public String toString() {
    return "{" + " invoiceItems='" + getInvoiceItems() + "'" + ", id='" + getId() + "'" + ", accountId='"
        + getAccountId() + "'" + ", transactionId='" + getTransactionId() + "'" + ", totalPaid='" + getTotalPaid() + "'"
        + ", paidOn='" + getPaidOn() + "'" + ", referentialNumber='" + getReferentialNumber() + "'"
        + ", originalFileName='" + getOriginalFileName() + "'" + "}";
  }

}