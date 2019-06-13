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

@Entity
public class Invoice {

  @OneToMany(cascade = CascadeType.ALL,mappedBy = "invoice")
  private List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String accountId;
  private String transactionId;
  private String totalPaid;
  private String paidOn;
  private String referentialNumber;

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

  @Override
  public String toString() {
    return "{" + " invoiceItems='" + getInvoiceItems() + "'" + ", accountId='" + getAccountId() + "'"
        + ", transactionId='" + getTransactionId() + "'" + ", totalPaid='" + getTotalPaid() + "'" + ", paidOn='"
        + getPaidOn() + "'" + ", referentialNumber='" + getReferentialNumber() + "'" + "}";
  }

public Long getId() {
	return id;
}

}