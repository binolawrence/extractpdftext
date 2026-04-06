package com.extract.pdf.extractpdftext.util;
import net.sourceforge.tess4j.*;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class OcrProcessor {

    private Tesseract tesseract;

    public OcrProcessor() {
        tesseract = new Tesseract();

       // Set installation path
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

       // Set languages (VERY IMPORTANT)
        tesseract.setLanguage("eng+tam");
        //tesseract.setLanguage("eng"); // Set OCR language (you can add more languages if needed)
    }

    // Perform OCR on an image file
    public String extractTextFromImage(File imageFile) throws Exception {
        BufferedImage image = ImageIO.read(imageFile);
        return tesseract.doOCR(image);
    }

    // Example method to extract text from image-based PDFs using OCR
    public String extractTextFromPdfWithOcr(String pdfFilePath) throws Exception {
        // You'd need to convert PDF pages into images (e.g., using PDFBox or other libraries)
        // and then pass those images to Tesseract for OCR
        // Assuming you have already converted the PDF pages to image files
        File imageFile = new File(pdfFilePath); // Placeholder for actual image
        return extractTextFromImage(imageFile);
    }
}