package com.extract.pdf.extractpdftext.pojo;

import lombok.Data;

@Data
public class SearchResult {
    //Have fileName,pageNo,fileLocation as attributes
    private String fileName;
    private int pageNo;
    private String fileLocation;

    public SearchResult() {
    }

    public SearchResult(String fileName, int pageNo, String fileLocation) {
        this.fileName = fileName;
        this.pageNo = pageNo;
        this.fileLocation = fileLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}

