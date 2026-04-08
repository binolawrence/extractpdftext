package com.extract.pdf.extractpdftext.service;

import com.extract.pdf.extractpdftext.util.OcrProcessor;
import com.extract.pdf.extractpdftext.util.PDFTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class PDFProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(PDFProcessingService.class);

    @Autowired
    private PDFTextExtractor extractor;

    @Autowired
    private PDFIndexService indexService;

    private OcrProcessor ocrProcessor;

    public PDFProcessingService() {
        this.ocrProcessor = new OcrProcessor();
    }


    public void processPDF(File file) throws Exception {
        logger.info("Starting PDF processing for file: {}", file.getName());
        try {
            PDDocument pdf = PDDocument.load(file);
            logger.debug("PDF document loaded: {}", file.getAbsolutePath());

            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = pdf.getNumberOfPages();
            logger.info("PDF has {} pages", totalPages);

            for (int page = 1; page <= totalPages; page++) {
                logger.debug("Processing page {} of {}", page, totalPages);
                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String text = stripper.getText(pdf);
                if (text == null || text.isEmpty()) {
                    logger.warn("No text extracted from page {}. Applying OCR to scanned PDF: {}", page, file.getName());
                    text = "dummy text";
                } else {
                    logger.debug("Text extracted from page {}. Length: {}", page, text.length());
                }
                indexService.indexPDF(text, file.getName(), file.getAbsolutePath(), page);
            }
            pdf.close();
            logger.info("PDF processing completed successfully for file: {}", file.getName());
        } catch (Exception e) {
            logger.error("Error processing PDF file: {}", file.getName(), e);
            throw e;
        }
    }
}

