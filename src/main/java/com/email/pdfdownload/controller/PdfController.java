package com.email.pdfdownload.controller;

import com.email.pdfdownload.dto.CustomerRms.CustomerRmsRequestDto;
import com.email.pdfdownload.dto.TradeBook.TradeBookRequestDto;
import com.email.pdfdownload.service.CustomerRmsPdfGeneratorService;
import com.email.pdfdownload.service.TradeBookPdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pdf")
public class PdfController {



    @Autowired
    private CustomerRmsPdfGeneratorService pdfService;

    @Autowired
    private TradeBookPdfGenerator tradeBookPdfGenerator;

    @PostMapping("/generateCustomerRmsPdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody CustomerRmsRequestDto customerRmsRequestDtos) {
        byte[] pdfBytes = pdfService.generateTablePdf(customerRmsRequestDtos);

        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = "/home/bizott-2/CodingPractice/table_report_" + timestamp + ".pdf";

        pdfService.savePdfToFile(pdfBytes, filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=table_report_" + timestamp + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateTradePdf(@RequestBody TradeBookRequestDto tradeBookRequestDto) {
        byte[] pdfBytes = tradeBookPdfGenerator.generateTablePdf(tradeBookRequestDto);

        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = "/home/bizott-2/CodingPractice/trade_report_" + timestamp + ".pdf";

        tradeBookPdfGenerator .savePdfToFile(pdfBytes, filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trade_report_" + timestamp + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
