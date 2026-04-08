package com.extract.pdf.extractpdftext.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class to manage file and directory paths
 * Priority order (highest to lowest):
 * 1. Environment variables (PDF_DOCS_DIR, LUCENE_INDEX_DIR)
 * 2. application.properties or application.yml
 * 3. Default hardcoded values
 */
@Component
public class PathConfig {

    private static final Logger logger = LoggerFactory.getLogger(PathConfig.class);

    // Default directories (fallback if not configured anywhere)
    private static final String DEFAULT_PDF_DOCS_DIR = "C:\\Users\\bfrancis\\projects\\pdfdocs";
    private static final String DEFAULT_LUCENE_INDEX_DIR = "C:\\Users\\bfrancis\\projects\\lucene-index";

    // Environment variable names
    private static final String PDF_DOCS_DIR_ENV = "PDF_DOCS_DIR";
    private static final String LUCENE_INDEX_DIR_ENV = "LUCENE_INDEX_DIR";

    // Read from application.properties/yml with defaults
    @Value("${app.pdf.docs-dir:}")
    private String configuredPdfDocsDir;

    @Value("${app.lucene.index-dir:}")
    private String configuredLuceneIndexDir;

    /**
     * Get the PDF documents directory
     * Priority: Environment Variable > application.properties > Default
     *
     * @return Path to PDF documents directory
     */
    public String getPdfDocsDir() {
        logger.debug("Retrieving PDF docs directory");
        // Check environment variable first (highest priority)
        String envPath = System.getenv(PDF_DOCS_DIR_ENV);
        if (envPath != null && !envPath.trim().isEmpty()) {
            logger.debug("Using PDF docs directory from environment variable: {}", envPath);
            return envPath;
        }

        // Check application.properties/yml
        if (configuredPdfDocsDir != null && !configuredPdfDocsDir.trim().isEmpty()) {
            logger.debug("Using PDF docs directory from application.properties: {}", configuredPdfDocsDir);
            return configuredPdfDocsDir;
        }

        // Return default
        logger.debug("Using default PDF docs directory: {}", DEFAULT_PDF_DOCS_DIR);
        return DEFAULT_PDF_DOCS_DIR;
    }

    /**
     * Get the Lucene index directory
     * Priority: Environment Variable > application.properties > Default
     *
     * @return Path to Lucene index directory
     */
    public String getLuceneIndexDir() {
        logger.debug("Retrieving Lucene index directory");
        // Check environment variable first (highest priority)
        String envPath = System.getenv(LUCENE_INDEX_DIR_ENV);
        if (envPath != null && !envPath.trim().isEmpty()) {
            logger.debug("Using Lucene index directory from environment variable: {}", envPath);
            return envPath;
        }

        // Check application.properties/yml
        if (configuredLuceneIndexDir != null && !configuredLuceneIndexDir.trim().isEmpty()) {
            logger.debug("Using Lucene index directory from application.properties: {}", configuredLuceneIndexDir);
            return configuredLuceneIndexDir;
        }

        // Return default
        logger.debug("Using default Lucene index directory: {}", DEFAULT_LUCENE_INDEX_DIR);
        return DEFAULT_LUCENE_INDEX_DIR;
    }

    /**
     * Log the configuration on startup (for debugging)
     */
    public void logConfiguration() {
        logger.info("\n===== PATH CONFIGURATION =====");
        logger.info("PDF Docs Directory: {}", getPdfDocsDir());
        if (System.getenv(PDF_DOCS_DIR_ENV) != null) {
            logger.info("  Source: Environment variable (PDF_DOCS_DIR)");
        } else if (configuredPdfDocsDir != null && !configuredPdfDocsDir.trim().isEmpty()) {
            logger.info("  Source: application.properties (app.pdf.docs-dir)");
        } else {
            logger.info("  Source: Default hardcoded value");
        }

        logger.info("Lucene Index Directory: {}", getLuceneIndexDir());
        if (System.getenv(LUCENE_INDEX_DIR_ENV) != null) {
            logger.info("  Source: Environment variable (LUCENE_INDEX_DIR)");
        } else if (configuredLuceneIndexDir != null && !configuredLuceneIndexDir.trim().isEmpty()) {
            logger.info("  Source: application.properties (app.lucene.index-dir)");
        } else {
            logger.info("  Source: Default hardcoded value");
        }
        logger.info("==============================\n");
    }
}


