package com.extract.pdf.extractpdftext.util;

public class Main {
    public static void main(String[] args) {
        String text = "ection No and Name 1-Tambaram (C), Pulikoradu (R.V), Ward 32 Amman Koil Street";

        // Convert both the text and the search term to lowercase for case-insensitive comparison
        String searchTerm = "amman";

        if (text.toLowerCase().contains(searchTerm.toLowerCase())) {
            System.out.println("The string contains the word 'amman'.");
        } else {
            System.out.println("The string does not contain the word 'amman'.");
        }
    }
}