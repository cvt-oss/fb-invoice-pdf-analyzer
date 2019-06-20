package cz.cvt.pdf.rest;

import java.io.File;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class FormData {

    private File pdfFile;

    public File getPdfFile() {
        return pdfFile;
    }

    @FormParam("invoice")
    @PartType("application/octet-stream")
    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }
}