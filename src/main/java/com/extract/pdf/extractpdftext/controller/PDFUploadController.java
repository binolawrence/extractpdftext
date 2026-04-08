package com.extract.pdf.extractpdftext.controller;

import com.extract.pdf.extractpdftext.config.PathConfig;
import com.extract.pdf.extractpdftext.service.PDFProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/pdf")
public class PDFUploadController {

    private static final Logger logger = LoggerFactory.getLogger(PDFUploadController.class);

    @Autowired
    private PDFProcessingService processingService;

    @Autowired
    private PathConfig pathConfig;

    @PostMapping("/upload1")
    public String upload() throws Exception {
        logger.info("Upload endpoint 1 called (no file parameter)");
        return upload(null);
    }

    @PostMapping("/upload")
    public String upload(@RequestParam(required = false) MultipartFile file) throws Exception {
        logger.info("Upload endpoint called");
        try {
            File folder = new File(pathConfig.getPdfDocsDir());
            logger.info("Processing PDFs from folder: {}", folder.getAbsolutePath());
            
            File[] files = folder.listFiles();
            if (files == null) {
                logger.warn("No PDF files found in folder: {}", folder.getAbsolutePath());
                return "No PDF files found";
            }
            
            logger.info("Found {} files to process", files.length);
            for (File filenew : files) {
                logger.debug("Processing file: {}", filenew.getName());
                processingService.processPDF(filenew);
            }
            logger.info("PDF indexing completed successfully");
            return "PDF indexed successfully";
        } catch (Exception e) {
            logger.error("Error during PDF upload/indexing", e);
            throw e;
        }
    }
}

