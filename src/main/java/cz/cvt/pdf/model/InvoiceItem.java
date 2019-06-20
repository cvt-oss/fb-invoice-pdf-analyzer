package cz.cvt.pdf.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class InvoiceItem extends PanacheEntity {

  public String campaignName;
  public Double price;
  public String prefix;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  public Invoice invoice;

  public InvoiceItem() {
  }

  public InvoiceItem(String campaignName, Double price, String prefix) {

    this.campaignName = campaignName;
    this.price = price;
    this.prefix = prefix;
  }

}
