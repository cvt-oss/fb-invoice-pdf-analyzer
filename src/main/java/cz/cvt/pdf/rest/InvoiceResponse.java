package cz.cvt.pdf.rest;

import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class InvoiceResponse {
    private Long id;

    public InvoiceResponse() {
    }

    public InvoiceResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof InvoiceResponse)) {
            return false;
        }
        InvoiceResponse invoiceResponse = (InvoiceResponse) o;
        return Objects.equals(id, invoiceResponse.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}