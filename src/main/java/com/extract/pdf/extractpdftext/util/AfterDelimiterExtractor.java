package com.extract.pdf.extractpdftext.util;

public class AfterDelimiterExtractor {
    public static void main(String[] args) {
        // Example usage — in real use, pass these as args or read from input
        String input = " 1-Tambaram (C), Pulikoradu (R.V), Ward 32 Amman Koil Street , 2-Tamb ©). Kad, AV). Ward 32 Am Kol Street Main Town or Village : TAMBARAM CORPORATION , -ambaram (C), Kadaperi (R.V), War man Kol Street [0 ™ WARD 32 , B e Post Office : TAMBARAM , 3-Tambaram (C), Kadaperi (R.V), Ward 32 East Street Faoe Bestion + S11 TAMBARAM , 4-Tambaram (C), Kadaperi (R.V), Ward 32 Middle Street Bock S TAMBARAM , 2-Tambaram (C), Kadaperi (R.V), Ward 32 Gangaiamman Koil | ¢ oo TAMBARAM , e Distriot : CHENGALPATTU ,";
        String delimiter = " Distriot :";

        String result = extractAfterDelimiter(input, delimiter);
        System.out.println(result); // expected "CHENGALPATTU"
    }

    public static String extractAfterDelimiter(String input, String delimiter) {
        if (input == null || delimiter == null) return "";
        int idx = input.indexOf(delimiter);
        if (idx == -1) return ""; // delimiter not found

        int start = idx + delimiter.length();

        // substring from start, then trim leading whitespace
        String tail = input.substring(start).trim();

        // stop at next comma or newline or semicolon (common separators)
        int endIdx = tail.length();
        for (int i = 0; i < tail.length(); i++) {
            char c = tail.charAt(i);
            if (c == ',' || c == '\n' || c == ';') { endIdx = i; break; }
        }

        String candidate = tail.substring(0, endIdx).trim();

        // If the value may be followed by other words separated by spaces and punctuation,
        // you can optionally limit to first token. Here we return the whole segment before separator.
        return candidate;
    }
}
