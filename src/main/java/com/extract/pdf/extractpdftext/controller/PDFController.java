package com.extract.pdf.extractpdftext.controller;

import com.extract.pdf.extractpdftext.pojo.Voter;
import com.extract.pdf.extractpdftext.service.PDFSearchService;
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

    @Autowired
    private PDFSearchService searchService;

    @GetMapping("/search")
    public List<Voter> search(@RequestParam String name, @RequestParam String fathername, @RequestParam(required = false) String husbandname) throws Exception {
          System.out.println("search searching ");
          return searchService.search(name,fathername,husbandname);
    }

    @GetMapping("/loadPDF")
    public ResponseEntity<ByteArrayResource> loadPDF(@RequestParam String fileName, @RequestParam int pageNo,@RequestParam String[] searchText) throws Exception {
        System.out.println("searchv1 searching ");
        ByteArrayResource byteArrayResource=searchService.loadPDF(fileName,pageNo,searchText);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(byteArrayResource);
    }
}
