package com.email.pdfdownload.service;


import com.email.pdfdownload.dto.CustomerRms.Client;
import com.email.pdfdownload.dto.CustomerRms.CustomerRmsAllScript;
import com.email.pdfdownload.dto.CustomerRms.CustomerRmsRequestDto;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class CustomerRmsPdfGeneratorService {

    public byte[] generateTablePdf(CustomerRmsRequestDto customerRmsRequestDtos) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            PageSize customPageSize = new PageSize(PageSize.A4.getWidth(), PageSize.A4.getHeight()); // Increase height by 200 points
            Document document = new Document(pdfDoc, customPageSize.rotate());
            document.setMargins(50, 15, 50, 15);

            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderFooterPageEvent("leftHeaderText", "rightHeaderText", false, true, false));


            float[] topLine = {1f, 1f};
            Table topBoxTable = new Table(topLine);
            topBoxTable.setWidth(UnitValue.createPercentValue(100));
            topBoxTable.setMargin(0).setPadding(0);
            topBoxTable.setFixedLayout();

            Cell leftCell = new Cell()
                    .add(new Paragraph("Customer RMS")
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                            .setFontSize(14))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT);

            String fromDate = customerRmsRequestDtos.getFromDate();  // Fetch from DTO
            String toDate = customerRmsRequestDtos.getToDate();

            Cell rightCell = new Cell()
                    .add(new Paragraph("From: " + fromDate + "  To: " + toDate)
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                            .setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);

            topBoxTable.addCell(leftCell);
            topBoxTable.addCell(rightCell);

            document.add(topBoxTable);
            for (Client customerRmsRequestDto : customerRmsRequestDtos.getClients()) {

                if (customerRmsRequestDtos.getClients().indexOf(customerRmsRequestDto) != 0) {
                    document.add(new Paragraph("\n"));  // Adds a line of space between clients
                }
                // Create a 9-column table for the client name and ledger balance
                float[] boxWidths = {5f, 3.5f, 3.5f, 3.5f, 3.5f, 4f, 3.5f, 3.5f, 4f};
                Table boxTable = new Table(boxWidths);
                boxTable.setWidth(UnitValue.createPercentValue(100));
                boxTable.setMargin(0).setPadding(0);
                boxTable.setFixedLayout();

                double totalBuyQty = 0.0;
                double totalM2M = 0.0;

                // Add client name and ledger balance in separate boxes
                boxTable.addCell(createTopLeftBoxCell(customerRmsRequestDto.getClientName()));
                boxTable.addCell(createBoxTextSeperate("Ledger Balance:", customerRmsRequestDto.getLedgerBalance()));

                // Add header row
                boxTable.addCell(createHeaderCell1("Script Name"));
                boxTable.addCell(createHeaderCell("Buy Qty"));
                boxTable.addCell(createHeaderCell("Buy Avg"));
                boxTable.addCell(createHeaderCell("Sell Qty"));
                boxTable.addCell(createHeaderCell("Sell Avg"));
                boxTable.addCell(createHeaderCell("Pos Qty"));
                boxTable.addCell(createHeaderCell("B.E.Avg."));
                boxTable.addCell(createHeaderCell("LTP"));
                boxTable.addCell(createHeaderCell("M2M"));

                // Add rows for each script
                for (CustomerRmsAllScript script : customerRmsRequestDto.getScripts()) {
                    boxTable.addCell(createDataCell1(script.getScriptName()));
                    boxTable.addCell(createDataCell(formatDouble(script.getBuyQty())));
                    boxTable.addCell(createDataCell(formatDouble(script.getBuyAvg())));
                    boxTable.addCell(createDataCell(formatDouble(script.getSellQty())));
                    boxTable.addCell(createDataCell(formatDouble(script.getSellAvg())));

                    Double posQtyValue = script.getPosQty();
                    Double lotValue = script.getLot();

                    String lotDisplay = (lotValue == null || lotValue == 0) ? "" : String.format("(%d)", lotValue.intValue());
                    String combinedPosQty = (posQtyValue == null || posQtyValue == 0) ? "" : String.format("%.0f%s", posQtyValue, lotDisplay);

                    try {
                        if (posQtyValue != null && posQtyValue < 0) {
                            boxTable.addCell(createColoredDataCell(combinedPosQty, ColorConstants.RED));
                        } else {
                            boxTable.addCell(createColoredDataCell(combinedPosQty, ColorConstants.BLUE));
                        }
                    } catch (NumberFormatException e) {
                        boxTable.addCell(createDataCell(combinedPosQty));
                    }

                    boxTable.addCell(createDataCell(formatDouble(script.getBeAvg())));
                    boxTable.addCell(createDataCell(formatDouble(script.getLtp())));

                    Double m2mValue = script.getM2m();
                    try {
                        String formattedM2M = formatDouble(m2mValue);
                        if (m2mValue != null && m2mValue < 0) {
                            boxTable.addCell(createColoredDataCell(formattedM2M, ColorConstants.RED));
                        } else {
                            boxTable.addCell(createColoredDataCell(formattedM2M, ColorConstants.BLUE));
                        }
                    } catch (NumberFormatException e) {
                        boxTable.addCell(createDataCell(String.valueOf(m2mValue)));
                    }

                    totalBuyQty += (script.getBuyQty() != null) ? script.getBuyQty() : 0;
                    totalM2M += (m2mValue != null) ? m2mValue : 0;
                }

                // Add the second set of boxes immediately below the table

                boxTable.addCell(createBottomLeftBoxCell(formatDouble(totalBuyQty)));
                boxTable.addCell(createBottomRightBoxCell(formatDouble(totalM2M)));

                document.add(boxTable);
            }
            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public String formatDouble(Double value) {
        if (value == null || value == 0) {
            return "";
        }
        return String.format("%.2f", value);
    }

    public Cell createBottomLeftBoxCell(String content) {
        Color customColor = new DeviceRgb(255, 255, 153);

        return new Cell(1, 5)
                .add(new Paragraph(content).setBold())
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    public Cell createBottomRightBoxCell(String content) {
        Color customColor = new DeviceRgb(99, 99, 99);
        return new Cell(1, 4)
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5)
                .setMargin(0)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    public Cell createTopLeftBoxCell(String content) {
        Color customColor = new DeviceRgb(203, 195, 227);

        return new Cell(1, 5)
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }


    public Cell createBoxTextSeperate(String label, String value) {
        Color customColor = new DeviceRgb(203, 195, 227);
        Paragraph paragraph = new Paragraph()
                .add(label)  // Add the label text
                .add(new Tab())  // Add a tab space to push the value to the right
                .add(value)  // Add the value
                .addTabStops(new TabStop(1000, TabAlignment.RIGHT));  // Ensure right alignment

        return new Cell(1, 4)
                .add(paragraph.setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    public Cell createHeaderCell(String content) {
        Color customColor = new DeviceRgb(99, 99, 99);
        return new Cell().add(new Paragraph(content).setBold())
                .setBackgroundColor(customColor)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(ColorConstants.WHITE);
    }

    public Cell createHeaderCell1(String content) {
        Color customColor = new DeviceRgb(99, 99, 99);
        return new Cell().add(new Paragraph(content).setBold())
                .setBackgroundColor(customColor)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.WHITE);
    }

    public Cell createDataCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setTextAlignment(TextAlignment.RIGHT);
    }

    public Cell createDataCell1(String content) {
        return new Cell().add(new Paragraph(content))
                .setTextAlignment(TextAlignment.LEFT);
    }

    public Cell createColoredDataCell(String content, Color color) {
        return new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(color); // Dynamically set color
    }


    public void savePdfToFile(byte[] pdfBytes, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
            System.out.println("PDF saved to " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving PDF: " + e.getMessage());
        }
    }
}