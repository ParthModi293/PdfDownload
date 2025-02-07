package com.email.pdfdownload.dto.TradeBook;

import lombok.Data;

import java.util.List;

@Data
public class TradeBookRequestDto {

    private String tradeSymbol;
    private  String month;
    private  String fromDate;
    private  String toDate;
    private List<TradeClient> clientList;

}
