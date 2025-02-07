package com.email.pdfdownload.dto.TradeBook;

import lombok.Data;

@Data
public class TradeBookAllScript {

    private String scriptName;
    private String time;
    private Double lot;
    private Double buyQty;
    private Double sellQty;
    private Double rate;
}
