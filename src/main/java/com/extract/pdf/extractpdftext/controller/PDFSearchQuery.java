package com.extract.pdf.extractpdftext.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PDFSearchQuery {
    public static void main(String[] args) {
        String resourcePath = "/SampleSearchablepdf.pdf";
        String searchQuery = "பாகம் எண் : 11"; // Example EPIC Number or Name

        try (InputStream is = PDFSearchQuery.class.getResourceAsStream(resourcePath);
             PDDocument document = PDDocument.load(is)) {

            PDFTextStripper stripper = new PDFTextStripper();

            // Loop through each page for precise querying
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String pageText = stripper.getText(document);

                // Split text into lines to find the specific match
                String[] lines = pageText.split("\\r?\\n");

                for (String line : lines) {
                    if (line.toLowerCase().contains(searchQuery.toLowerCase())) {
                        System.out.println("Match found on Page " + page + ":");
                        System.out.println("Result: " + line.trim());
                        System.out.println("-----------------------------------");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading PDF: " + e.getMessage());
        }
    }
}