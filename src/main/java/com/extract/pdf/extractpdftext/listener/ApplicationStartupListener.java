package com.extract.pdf.extractpdftext.listener;

import com.extract.pdf.extractpdftext.config.PathConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Application startup listener to log path configuration
 * This displays the configuration on startup to help with debugging
 */
@Component
public class ApplicationStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Autowired
    private PathConfig pathConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Application startup event triggered");
        // Log configuration when application is ready
        pathConfig.logConfiguration();
        logger.info("Application is ready and fully initialized");
    }
}

