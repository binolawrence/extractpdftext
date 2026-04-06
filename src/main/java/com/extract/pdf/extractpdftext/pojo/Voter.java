package com.extract.pdf.extractpdftext.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voter {
    String epicNo;
    String name;
    String relativeName;
    String address;
    String assembly;
    String district;
    int age;
    String wardNo;
    String part;
    String getPollingStation;
    String partSerialNo;
    int pageNo;
    String fileLocation;
}
