package com.extract.pdf.extractpdftext.listener;

import com.extract.pdf.extractpdftext.config.PathConfig;
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

    @Autowired
    private PathConfig pathConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Log configuration when application is ready
        pathConfig.logConfiguration();
    }
}

