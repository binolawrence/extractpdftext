package com.extract.pdf.extractpdftext.util;

import org.apache.tika.Tika;
import java.io.File;

public class BulkPDFSearch {

    public static void main(String[] args) throws Exception {

        Tika tika = new Tika();
        String searchText = "தாண்டவராயன்";

        File folder = new File("C:\\sprojects\\electoral\\part245\\search");

        for (File file : folder.listFiles()) {

            if (file.getName().endsWith("2026-EROLLGEN-S22-31-SIR-DraftRoll-Revision1-TAM-1-WI.pdf")) {

                String text = tika.parseToString(file);
                System.out.println("Searching in: " + file.getName());
                System.out.println("Extracted Text: "    + text); // Print the extracted text for debugging

                if (text.contains(searchText)) {
                    System.out.println("Found in: " + file.getName());
                }else {
                    System.out.println("Not found");
                }

            }
        }
    }
}