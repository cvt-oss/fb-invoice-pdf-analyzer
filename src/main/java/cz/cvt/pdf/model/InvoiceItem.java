package cz.cvt.pdf.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class InvoiceItem {

  String campaingName;
  String price;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Invoice invoice;

  public Invoice getInvoice() {
    return this.invoice;
  }

  public void setInvoice(Invoice invoice) {
    this.invoice = invoice;
  }

  public InvoiceItem() {
  }

  public InvoiceItem(String campaingName, String price) {

    this.campaingName = campaingName;
    this.price = price;
  }

  public String getCampaingName() {
    return campaingName;
  }

  public void setCampaingName(String campaingName) {
    this.campaingName = campaingName;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return "InvoiceItem [campaingName=" + campaingName + ", price=" + price + "]";
  }

  public Long getId() {
    return id;
  }

}
