package com.extract.pdf.extractpdftext.service;

import com.extract.pdf.extractpdftext.util.OcrProcessor;
import com.extract.pdf.extractpdftext.util.PDFTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class PDFProcessingService {

    @Autowired
    private PDFTextExtractor extractor;

    @Autowired
    private PDFIndexService indexService;

    private OcrProcessor ocrProcessor;

    public PDFProcessingService() {
        this.ocrProcessor = new OcrProcessor();
    }


    public void processPDF(File file) throws Exception {


        PDDocument pdf = PDDocument.load(file);

        PDFTextStripper stripper = new PDFTextStripper();
//        PDFRenderer pdfRenderer = new PDFRenderer(pdf);
//        int dpi=150;


        for (int page = 1; page <= pdf.getNumberOfPages(); page++) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);
            //BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page,dpi);

            String text = stripper.getText(pdf);
            if (text == null || text.isEmpty()) {
                System.out.println("Applying OCR to scanned PDF: " + file.getName());
                //text = ocrProcessor.extractTextFromPdfWithOcr(file.getAbsolutePath());
                text="dummy text";
            }
            indexService.indexPDF(text, file.getName(), file.getAbsolutePath(), page);
        }
        pdf.close();
    }
}