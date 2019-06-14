package cz.cvt.pdf.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Invoice {

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice")
  @JsonManagedReference
  private List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String accountId;
  private String transactionId;
  private String totalPaid;
  private String paidOn;
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

  @Override
  public String toString() {
    return "{" + " invoiceItems='" + invoiceItems + "'" + ", id='" + id + "'" + ", accountId='" + accountId + "'"
        + ", transactionId='" + transactionId + "'" + ", totalPaid='" + totalPaid + "'" + ", paidOn='" + paidOn + "'"
        + ", referentialNumber='" + referentialNumber + "'" + ", originalFileName='" + originalFileName + "'" + "}";
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

  public String getTotalPaid() {
    return totalPaid;
  }

  public void setTotalPaid(String totalPaid) {
    this.totalPaid = totalPaid;
  }

  public String getPaidOn() {
    return paidOn;
  }

  public void setPaidOn(String paidOn) {
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

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Invoice)) {
      return false;
    }
    Invoice invoice = (Invoice) o;
    return Objects.equals(accountId, invoice.accountId) && Objects.equals(transactionId, invoice.transactionId)
        && Objects.equals(referentialNumber, invoice.referentialNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, transactionId, referentialNumber);
  }

  public Long getId() {
    return id;
  }

}