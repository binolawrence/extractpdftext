package com.extract.pdf.extractpdftext.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PDFTextExtractor {

    private static final Logger logger = LoggerFactory.getLogger(PDFTextExtractor.class);

    public String extractText(File file) throws Exception {
        logger.info("Extracting text from PDF file: {}", file.getName());
        try {
            PDDocument document = PDDocument.load(file);
            logger.debug("PDF document loaded from: {}", file.getAbsolutePath());

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.debug("Text extracted. Length: {}", text.length());

            document.close();
            logger.info("PDF text extraction completed for: {}", file.getName());

            return text;
        } catch (Exception e) {
            logger.error("Error extracting text from PDF: {}", file.getName(), e);
            throw e;
        }
    }
}

