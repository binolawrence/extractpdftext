package com.extract.pdf.extractpdftext.controller;

import com.extract.pdf.extractpdftext.config.PathConfig;
import com.extract.pdf.extractpdftext.service.PDFProcessingService;
import com.extract.pdf.extractpdftext.service.PDFSearchService;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
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

        // 🔥 Limit threads for large PDF safety
        int threads = Math.min(2, Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        int batchSize = 10;
        List<File> batch = new ArrayList<>();

        for (File processedFile : files) {
            batch.add(processedFile);

            if (batch.size() == batchSize) {
                processBatch(batch, executor, successCount, failureCount);
                batch.clear();
            }
        }

        // process remaining files
        if (!batch.isEmpty()) {
            processBatch(batch, executor, successCount, failureCount);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                logger.warn("Executor did not terminate in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long duration = System.currentTimeMillis() - startTime;

        logger.info("PDF indexing completed in {} ms", duration);
        logger.info("Success: {}, Failed: {}", successCount.get(), failureCount.get());

        return String.format(
                "Completed. Success: %d, Failed: %d",
                successCount.get(),
                failureCount.get()
        );
    }

    private void processBatch(
            List<File> batch,
            ExecutorService executor,
            AtomicInteger successCount,
            AtomicInteger failureCount) throws IOException {

        Directory dir = FSDirectory.open(Paths.get(pathConfig.getLuceneIndexDir()));

        //IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriterConfig config = new IndexWriterConfig(PDFSearchService.buildAnalyzerv1());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        IndexWriter writer = new IndexWriter(dir, config);

        List<Future<?>> futures = new ArrayList<>();

        for (File processedFile : batch) {
            futures.add(executor.submit(() -> {
                try {
                    logger.debug("Processing file: {}", processedFile.getName());

                    // Skip empty files
                    if (processedFile.length() == 0) {
                        logger.warn("Skipping empty file: {}", processedFile.getName());
                        return;
                    }

                    // Optional: skip very large files
                    long MAX_SIZE = 100 * 1024 * 1024; // 100MB
                    if (processedFile.length() > MAX_SIZE) {
                        logger.warn("Skipping large file: {}", processedFile.getName());
                        failureCount.incrementAndGet();
                        return;
                    }

                    processingService.processPDF(processedFile,writer);
                    successCount.incrementAndGet();

                } catch (Exception ex) {
                    failureCount.incrementAndGet();
                    logger.error("Failed processing file: {}", processedFile.getName(), ex);
                }
            }));
        }


        // Wait for batch completion with timeout
        for (Future<?> future : futures) {
            try {
                future.get(10, TimeUnit.MINUTES);
            } catch (TimeoutException e) {
                logger.error("Task timeout in batch");
                future.cancel(true);
                failureCount.incrementAndGet();
            } catch (Exception e) {
                logger.error("Error waiting for batch task", e);
            }
        }

        writer.commit();
        writer.close();
    }

}

