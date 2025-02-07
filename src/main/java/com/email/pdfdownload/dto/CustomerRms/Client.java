package com.email.pdfdownload.dto.CustomerRms;

import lombok.Data;

import java.util.List;

@Data
public class Client {

    private String clientName;
    private List<CustomerRmsAllScript> scripts;
    private String ledgerBalance;
}
