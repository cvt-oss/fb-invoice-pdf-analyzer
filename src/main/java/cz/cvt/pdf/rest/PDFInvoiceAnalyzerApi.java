package cz.cvt.pdf.rest;

import java.io.File;
import java.io.FileInputStream;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cvt.pdf.model.Invoice;
import cz.cvt.pdf.service.api.InvoiceParserService;

@Path("/api/pdf")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class PDFInvoiceAnalyzerApi {

    @Inject
    InvoiceParserService pdfService;

    private static final Logger log = LoggerFactory.getLogger(PDFInvoiceAnalyzerApi.class);

    @POST
    @Path("/invoice/process")
    @Transactional
    public Response processInvoice(MultipartFormDataInput input) throws IOException {

        try {

            InputStream is = getPDF(input);
            Invoice invoice = pdfService.parse(is);
            invoice.originalFileName = getFileName(input);
            invoice.persist();

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
            if (invoice == null)
                return Response.status(Status.NOT_FOUND).build();
            return Response.ok(invoice).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    private InputStream getPDF(MultipartFormDataInput input) {
        if (input == null || input.getParts() == null || input.getParts().isEmpty()) {
            throw new IllegalArgumentException("Multipart request is empty");
        }

        try {
            InputStream result;
            if (input.getParts().size() == 1) {
                InputPart filePart = input.getParts().iterator().next();
                result = filePart.getBody(InputStream.class, null);
                log.debug("filename {}", filePart.getHeaders().getFirst("Content-Disposition"));
            } else {
                result = input.getFormDataPart("invoice", InputStream.class, null);

            }

            if (result == null) {
                throw new IllegalArgumentException("Can't find a valid 'invoice' part in the multipart request");
            }

            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while reading multipart request", e);
        }
    }

    private String getFileName(MultipartFormDataInput input) {

        if (input.getParts().size() == 1) {
            InputPart filePart = input.getParts().iterator().next();
            MultivaluedMap<String, String> header = filePart.getHeaders();
            String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

            for (String filename : contentDisposition) {
                if ((filename.trim().startsWith("filename"))) {

                    String[] name = filename.split("=");

                    String finalFileName = name[1].trim().replaceAll("\"", "");
                    return finalFileName;
                }
            }
        }

        return "unknown";
    }

}