package com.extract.pdf.extractpdftext.controller;

import com.extract.pdf.extractpdftext.pojo.SearchResult;
import com.extract.pdf.extractpdftext.pojo.Voter;
import com.extract.pdf.extractpdftext.service.PDFSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/pdf")
public class PDFController {

    private static final Logger logger = LoggerFactory.getLogger(PDFController.class);

    @Autowired
    private PDFSearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String relativeName,
            @RequestParam(required = false) String streetName) {

        logger.info("Search endpoint called - Name: {}, RelativeName: {}, StreetName: {}",
                name, relativeName, streetName);

        // 🔴 Validation: at least one parameter must be present
        if (isBlank(name) && isBlank(relativeName) && isBlank(streetName)) {
            logger.warn("Validation failed - no parameters provided");

            return ResponseEntity
                    .badRequest()
                    .body("At least one parameter (name, relativeName, streetName) must be provided");
        }

        try {
            logger.debug("Invoking search service");

            List<SearchResult> results = searchService.search(name, relativeName, streetName);

            logger.info("Search completed. Found {} results", results.size());

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            logger.error("Error during search", e);

            return ResponseEntity
                    .status(500)
                    .body("Internal server error");
        }
    }

    // 🔧 Helper
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    @GetMapping("/loadPDF")
    public ResponseEntity<ByteArrayResource> loadPDF(@RequestParam String fileName, @RequestParam int pageNo) throws Exception {
        logger.info("LoadPDF endpoint called - FileName: {}, PageNo: {}", fileName, pageNo);
        try {
            ByteArrayResource byteArrayResource = searchService.loadPDF(fileName, pageNo);
            logger.info("PDF loaded successfully from {} at page {}", fileName, pageNo);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(byteArrayResource);
        } catch (Exception e) {
            logger.error("Error loading PDF from {} at page {}", fileName, pageNo, e);
            throw e;
        }
    }
}

