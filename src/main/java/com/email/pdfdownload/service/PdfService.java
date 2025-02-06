/*
package com.email.pdfdownload.service;


import com.email.pdfdownload.dto.AllScript;
import com.email.pdfdownload.dto.RequestDto;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
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
import java.util.List;

@Service
public class PdfService {

    public byte[] generateTablePdf(List<RequestDto> requestDtos) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            PageSize customPageSize = new PageSize(PageSize.A4.getWidth()+200, PageSize.A4.getHeight()); // Increase height by 200 points
            Document document = new Document(pdfDoc, customPageSize.rotate());
            document.setMargins(50, 15, 50, 15);

            for (RequestDto requestDto : requestDtos) {
                String leftHeaderText = "Customer RMS: ";  // Left Corner
                String rightHeaderText = "From: " + requestDto.getFromDate() + " To: " + requestDto.getToDate();  // Right Corner

                // Attach header/footer handler
                pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderFooterPageEvent(leftHeaderText, rightHeaderText));


                // Create a 2-column table for the client name and ledger balance
                float[] boxWidths = {60f, 40f};
                Table boxTable = new Table(boxWidths);
                boxTable.setWidth(UnitValue.createPercentValue(100));
                boxTable.setMargin(0).setPadding(0);
                boxTable.setFixedLayout();


                // Add client name and ledger balance in separate boxes
                boxTable.addCell(createTopLeftBoxCell(requestDto.getClientName()));
                boxTable.addCell(createBoxTextSeperate("Ledger Balance:", requestDto.getLedgerBalance()));

                // Add the box table to the document
                document.add(boxTable);

                // Create a table for script details (8 columns)
                float[] columnWidths = {2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f};
                Table scriptTable = new Table(columnWidths);
                scriptTable.setWidth(UnitValue.createPercentValue(100));

                // Add header row
                scriptTable.addCell(createHeaderCell("Script Name"));
                scriptTable.addCell(createHeaderCell("Buy Qty"));
                scriptTable.addCell(createHeaderCell("Buy Avg"));
                scriptTable.addCell(createHeaderCell("Sell Qty"));
                scriptTable.addCell(createHeaderCell("Sell Avg"));
                scriptTable.addCell(createHeaderCell("Pos Qty"));
                scriptTable.addCell(createHeaderCell("LTP"));
                scriptTable.addCell(createHeaderCell("M2M"));

                // Add rows for each script
                for (AllScript script : requestDto.getScripts()) {
                    scriptTable.addCell(createDataCell(script.getScriptName()));
                    scriptTable.addCell(createDataCell(script.getBuyQty()));
                    scriptTable.addCell(createDataCell(script.getBuyAvg()));
                    scriptTable.addCell(createDataCell(script.getSellQty()));
                    scriptTable.addCell(createDataCell(script.getSellAvg()));


                    String posQtyValue = script.getPosQty();
                    try {
                        String cleanedPosQtyValue = posQtyValue.replaceAll("\\(.*\\)", "").trim();
                        if (cleanedPosQtyValue.equals("0")) {
                            scriptTable.addCell(createDataCell(posQtyValue));
                        } else {
                            double posQty = Double.parseDouble(cleanedPosQtyValue);
                            if (posQty < 0) {
                                scriptTable.addCell(createColoredDataCell(posQtyValue, ColorConstants.RED));
                            } else {
                                scriptTable.addCell(createColoredDataCell(posQtyValue, ColorConstants.BLUE));
                            }
                        }
                    } catch (NumberFormatException e) {
                        scriptTable.addCell(createDataCell(posQtyValue));
                    }

                    scriptTable.addCell(createDataCell(script.getLtp()));


                    String m2mValue = script.getM2m();
                    try {
                        double m2m = Double.parseDouble(m2mValue);
                        if (m2m < 0) {
                            scriptTable.addCell(createColoredDataCell(m2mValue, ColorConstants.RED));
                        } else {
                            scriptTable.addCell(createColoredDataCell(m2mValue, ColorConstants.BLUE));
                        }
                    } catch (NumberFormatException e) {
                        scriptTable.addCell(createDataCell(m2mValue));
                    }

                }

                document.add(scriptTable);

                // Add the second set of boxes immediately below the table
                Table bottomBoxTable = createEmptyBoxTable();

                document.add(bottomBoxTable);

                // Add spacing only between different request DTOs
                if (requestDto != requestDtos.get(requestDtos.size() - 1)) {
                    document.add(new Paragraph("\n")); // Adds space only if it's not the last requestDto
                }
            }
            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private Table createEmptyBoxTable() {
        float[] boxWidths = {60f, 40f};
        Table boxTable = new Table(boxWidths);
        boxTable.setWidth(UnitValue.createPercentValue(100));
        boxTable.setMargin(0).setPadding(0);
        boxTable.setFixedLayout();
        // Add two empty boxes
        boxTable.addCell(createBottomLeftBoxCell("Hello")); // Empty Box 1
        boxTable.addCell(createBottomRightBoxCell("Hello")); // Empty Box 2

        return boxTable;
    }
    private Cell createBottomLeftBoxCell(String content) {
        Color customColor = new DeviceRgb(255, 255, 153);

        return new Cell()
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell createBottomRightBoxCell(String content) {
        return new Cell()
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell createTopLeftBoxCell(String content) {
        Color customColor = new DeviceRgb(102, 178, 255);

        return new Cell()
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }


    private Cell createBoxTextSeperate(String label, String value) {
        Color customColor = new DeviceRgb(102, 178, 255);
        Paragraph paragraph = new Paragraph()
                .add(label)  // Add the label text
                .add(new Tab())  // Add a tab space to push the value to the right
                .add(value)  // Add the value
                .addTabStops(new TabStop(1000, TabAlignment.RIGHT));  // Ensure right alignment

        return new Cell()
                .add(paragraph.setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell createHeaderCell(String content) {
        return new Cell().add(new Paragraph(content).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createDataCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createColoredDataCell(String content, Color color) {
        return new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(color); // Dynamically set color
    }


    public void savePdfToFile(byte[] pdfBytes, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
            System.out.println("PDF saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving PDF: " + e.getMessage());
        }
    }
}*/
