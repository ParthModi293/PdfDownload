package com.email.pdfdownload.dto.TradeBook;

import lombok.Data;

import java.util.List;

@Data
public class TradeClient {

    private String clientName;
    private List<TradeWithDates> tradeWithDates;



}
