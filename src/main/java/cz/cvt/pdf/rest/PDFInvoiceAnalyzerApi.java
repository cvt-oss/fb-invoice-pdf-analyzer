package cz.cvt.pdf.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.service.api.InvoiceParserService;

@Path("/api/pdf")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class PDFInvoiceAnalyzerApi {

    @Inject
    InvoiceParserService pdfService;

    private static final Logger log = Logger.getLogger(PDFInvoiceAnalyzerApi.class);

    @POST
    @Path("/invoice/process")
    @Transactional

    public Response processInvoice(@MultipartForm FormData formData) throws IOException {

        try {
            File file = formData.getPdfFile();
            InputStream is = new FileInputStream(file);
            Invoice invoice = pdfService.parse(is);
            invoice.originalFileName = file.getName();
            invoice.persist();
            file.delete();

            return Response.ok(new InvoiceResponse(invoice.id)).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Transactional
    @Path("/invoice/{id}")
    public Response getInvoiceById(@PathParam("id") long id) {
        try {
            Invoice invoice = Invoice.findById(id);
            return Response.ok(invoice).build();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return Response.status(Status.NOT_FOUND).build();
        }
    }

}