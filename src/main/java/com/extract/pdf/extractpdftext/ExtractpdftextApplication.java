package com.extract.pdf.extractpdftext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExtractpdftextApplication {

	private static final Logger logger = LoggerFactory.getLogger(ExtractpdftextApplication.class);

	public static void main(String[] args) {
		logger.info("Starting ExtractPDF Text Application...");
		SpringApplication.run(ExtractpdftextApplication.class, args);
		logger.info("ExtractPDF Text Application started successfully");
	}

}


