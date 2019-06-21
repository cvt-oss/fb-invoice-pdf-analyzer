package cz.cvt.pdf.model;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Entity
@RegisterForReflection
public class InvoiceItem extends PanacheEntity {

  public String campaignName;
  public Double price;
  public String prefix;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn
  @JsonbTransient //to avoid circular serialization issue
  public Invoice invoice;

  public InvoiceItem() {
  }

  public InvoiceItem(String campaignName, Double price, String prefix) {

    this.campaignName = campaignName;
    this.price = price;
    this.prefix = prefix;
  }

}
