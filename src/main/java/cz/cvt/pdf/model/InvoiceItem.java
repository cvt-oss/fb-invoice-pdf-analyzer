package cz.cvt.pdf.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@SequenceGenerator(name = "invoiceItemIdSeq", sequenceName = "INVOICE_ITEM_SEQUENCE", initialValue = 1, allocationSize = 1)
public class InvoiceItem {

  private String campaignName;

  private Double price;
  private String prefix;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceItemIdSeq")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  @JsonBackReference
  private Invoice invoice;

  public Invoice getInvoice() {
    return this.invoice;
  }

  public void setInvoice(Invoice invoice) {
    this.invoice = invoice;
  }

  public InvoiceItem() {
  }

  public InvoiceItem(String campaignName, Double price, String prefix) {

    this.campaignName = campaignName;
    this.price = price;
    this.prefix = prefix;
  }

  public String getCampaignName() {
    return campaignName;
  }

  public void setCampaignName(String campaignName) {
    this.campaignName = campaignName;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return "InvoiceItem [campaignName=" + campaignName + ", price=" + price + ", prefix=" + prefix + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof InvoiceItem)) {
      return false;
    }
    InvoiceItem invoiceItem = (InvoiceItem) o;
    return Objects.equals(campaignName, invoiceItem.campaignName) && Objects.equals(price, invoiceItem.price)
        && Objects.equals(prefix, invoiceItem.prefix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(campaignName, price, prefix);
  }

  public Long getId() {
    return id;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

}
