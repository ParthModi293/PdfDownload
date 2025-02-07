package com.email.pdfdownload.dto.CustomerRms;

import lombok.Data;

import java.util.List;

@Data
public class CustomerRmsRequestDto {

    private String fromDate;
    private String toDate;
    private List<Client> clients;



}
