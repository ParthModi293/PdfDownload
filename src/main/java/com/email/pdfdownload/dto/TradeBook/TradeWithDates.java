package com.email.pdfdownload.dto.TradeBook;

import lombok.Data;

import java.util.List;

@Data
public class TradeWithDates {

    private String date;
   private List<TradeBookAllScript> scripts;
}
