package com.extract.pdf.extractpdftext.controller;

import com.extract.pdf.extractpdftext.service.PDFProcessingService;
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

    @Autowired
    private PDFProcessingService processingService;

    @PostMapping("/upload1")
    public String upload() throws Exception {
        return upload(null);
    }

    @PostMapping("/upload")
    public String upload(@RequestParam(required = false) MultipartFile file) throws Exception {

        File folder = new File("C:\\Users\\bfrancis\\projects\\pdfdocs");
        for (File filenew : folder.listFiles()) {
            System.out.println(filenew.getName());
            processingService.processPDF(filenew);
        }

        return "PDF indexed successfully";
    }
}