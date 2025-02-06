package com.email.pdfdownload.controller;

import com.email.pdfdownload.dto.RequestDto;
import com.email.pdfdownload.service.PdfServiceNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfController {

/*    @Autowired
    private PdfService pdfService;*/

    @Autowired
    private PdfServiceNew pdfService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody List<RequestDto> requestDtos) {
        byte[] pdfBytes = pdfService.generateTablePdf(requestDtos);

        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = "/home/bizott-2/CodingPractice/table_report_" + timestamp + ".pdf";

        pdfService.savePdfToFile(pdfBytes, filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=table_report_" + timestamp + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
