package com.extract.pdf.extractpdftext.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PDFTextExtractor {

    public String extractText(File file) throws Exception {

        PDDocument document = PDDocument.load(file);

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        document.close();

        return text;
    }
}