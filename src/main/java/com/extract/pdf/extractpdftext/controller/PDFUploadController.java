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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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

        long startTime = System.currentTimeMillis();

        File folder = new File(pathConfig.getPdfDocsDir());

        if (!folder.exists() || !folder.isDirectory()) {
            logger.error("Invalid directory: {}", folder.getAbsolutePath());
            return "Invalid directory";
        }

        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            logger.warn("No PDF files found in folder: {}", folder.getAbsolutePath());
            return "No PDF files found";
        }

        logger.info("Found {} PDF files to process", files.length);

// 🔥 Parallel processing (controlled)
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(files.length, Runtime.getRuntime().availableProcessors())
        );

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();

        for (File processedFile : files) {
            futures.add(executor.submit(() -> {
                try {
                    logger.debug("Processing file: {}", processedFile.getName());

                    // Optional: skip empty files
                    if (processedFile.length() == 0) {
                        logger.warn("Skipping empty file: {}", file.getName());
                        return;
                    }

                    processingService.processPDF(processedFile);
                    successCount.incrementAndGet();

                } catch (Exception ex) {
                    failureCount.incrementAndGet();
                    logger.error("Failed processing file: {}", file.getName(), ex);
                }
            }));
        }

// Wait for completion
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.error("Error waiting for task completion", e);
            }
        }

        executor.shutdown();

        long duration = System.currentTimeMillis() - startTime;

        logger.info("PDF indexing completed in {} ms", duration);
        logger.info("Success: {}, Failed: {}", successCount.get(), failureCount.get());

        return String.format(
                "Completed. Success: %d, Failed: %d",
                successCount.get(),
                failureCount.get()
        );
    }
}

