package com.extract.pdf.extractpdftext.util;
import net.sourceforge.tess4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class OcrProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OcrProcessor.class);
    private Tesseract tesseract;

    public OcrProcessor() {
        logger.info("Initializing OCR Processor");
        tesseract = new Tesseract();

       // Set installation path
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        logger.debug("Tesseract data path set");

       // Set languages (VERY IMPORTANT)
        tesseract.setLanguage("eng+tam");
        logger.info("Tesseract languages set to: eng+tam");
        //tesseract.setLanguage("eng"); // Set OCR language (you can add more languages if needed)
    }

    // Perform OCR on an image file
    public String extractTextFromImage(File imageFile) throws Exception {
        logger.info("Extracting text from image: {}", imageFile.getName());
        try {
            BufferedImage image = ImageIO.read(imageFile);
            logger.debug("Image loaded from: {}", imageFile.getAbsolutePath());
            String result = tesseract.doOCR(image);
            logger.info("OCR extraction completed. Text length: {}", result.length());
            return result;
        } catch (Exception e) {
            logger.error("Error extracting text from image: {}", imageFile.getName(), e);
            throw e;
        }
    }

    // Example method to extract text from image-based PDFs using OCR
    public String extractTextFromPdfWithOcr(String pdfFilePath) throws Exception {
        logger.info("Extracting text from PDF with OCR: {}", pdfFilePath);
        try {
            // You'd need to convert PDF pages into images (e.g., using PDFBox or other libraries)
            // and then pass those images to Tesseract for OCR
            // Assuming you have already converted the PDF pages to image files
            File imageFile = new File(pdfFilePath); // Placeholder for actual image
            return extractTextFromImage(imageFile);
        } catch (Exception e) {
            logger.error("Error extracting text from PDF with OCR: {}", pdfFilePath, e);
            throw e;
        }
    }
}

