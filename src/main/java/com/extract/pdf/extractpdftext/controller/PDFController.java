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
    public List<SearchResult> search(@RequestParam String name, @RequestParam(required = false) String relativeName, @RequestParam(required = false) String streetName) throws Exception {
          logger.info("Search endpoint called - Name: {}, RelativeName: {}, StreetName: {}", name, relativeName, streetName);
          logger.debug("Invoking search service");
          List<SearchResult> results = searchService.search(name, relativeName, streetName);
          logger.info("Search completed. Found {} results", results.size());
          return results;
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

