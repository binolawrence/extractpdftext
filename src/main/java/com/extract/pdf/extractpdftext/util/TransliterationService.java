package com.extract.pdf.extractpdftext.util;

import org.springframework.web.client.RestTemplate;

public class TransliterationService {

    public String transliterate(String text) {
        String url = "https://inputtools.google.com/request?text="
                + text + "&itc=ta-t-i0-und&num=1";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return response;
    }

    public static void main(String[] args) {
        TransliterationService service = new TransliterationService();
        String input = "Amman koil theru";
        String transliterated = service.transliterate(input);
        System.out.println("Transliterated: " + transliterated);
    }
}