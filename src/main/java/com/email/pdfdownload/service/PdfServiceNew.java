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
public class PdfServiceNew {

    public byte[] generateTablePdf(List<RequestDto> requestDtos) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            PageSize customPageSize = new PageSize(PageSize.A4.getWidth(), PageSize.A4.getHeight()); // Increase height by 200 points
            Document document = new Document(pdfDoc, customPageSize.rotate());
            document.setMargins(50, 15, 50, 15);

            String leftHeaderText = "Customer RMS: ";  // Left Corner
            String rightHeaderText = "From: " + requestDtos.get(0).getFromDate() + " To: " + requestDtos.get(0).getToDate();  // Right Corner

            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderFooterPageEvent(leftHeaderText, rightHeaderText));

 /* String leftHeaderText = "Customer RMS: ";  // Left Corner
                String rightHeaderText = "From: " + requestDto.getFromDate() + " To: " + requestDto.getToDate();  // Right Corner

                // Attach header/footer handler
                pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderFooterPageEvent(leftHeaderText, rightHeaderText));

*/

            for (RequestDto requestDto : requestDtos) {

                // Create a 2-column table for the client name and ledger balance
                float[] boxWidths = {4f,2f,2f,2f,2f,2f,2f,2f,3f};
                Table boxTable = new Table(boxWidths);
                boxTable.setWidth(UnitValue.createPercentValue(100));
                boxTable.setMargin(0).setPadding(0);
                boxTable.setFixedLayout();

                double totalBuyQty = 0.0;
                double totalM2M = 0.0;

                // Add client name and ledger balance in separate boxes
                boxTable.addCell(createTopLeftBoxCell(requestDto.getClientName()));
                boxTable.addCell(createBoxTextSeperate("Ledger Balance:", requestDto.getLedgerBalance()));


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
                for (AllScript script : requestDto.getScripts()) {
                    boxTable.addCell(createDataCell1(script.getScriptName()));
                    boxTable.addCell(createDataCell(script.getBuyQty()));
                    boxTable.addCell(createDataCell(script.getBuyAvg()));
                    boxTable.addCell(createDataCell(script.getSellQty()));
                    boxTable.addCell(createDataCell(script.getSellAvg()));
                    boxTable.addCell(createDataCell(script.getBeAvg()));



                    String posQtyValue = script.getPosQty();
                    try {
                        String cleanedPosQtyValue = posQtyValue.replaceAll("\\(.*\\)", "").trim();
                        if (cleanedPosQtyValue.equals("0")) {
                            boxTable.addCell(createDataCell(posQtyValue));
                        } else {
                            double posQty = Double.parseDouble(cleanedPosQtyValue);
                            if (posQty < 0) {
                                boxTable.addCell(createColoredDataCell(posQtyValue, ColorConstants.RED));
                            } else {
                                boxTable.addCell(createColoredDataCell(posQtyValue, ColorConstants.BLUE));
                            }
                        }
                    } catch (NumberFormatException e) {
                        boxTable.addCell(createDataCell(posQtyValue));
                    }

                    boxTable.addCell(createDataCell(script.getLtp()));


                    String m2mValue = script.getM2m();
                    try {
                        double m2m = Double.parseDouble(m2mValue);
                        if (m2m < 0) {
                            boxTable.addCell(createColoredDataCell(m2mValue, ColorConstants.RED));
                        } else {
                            boxTable.addCell(createColoredDataCell(m2mValue, ColorConstants.BLUE));
                        }
                    } catch (NumberFormatException e) {
                        boxTable.addCell(createDataCell(m2mValue));
                    }

                    double buyQty = Double.parseDouble(script.getBuyQty());
                    totalBuyQty += buyQty;
                    double m2mVal = Double.parseDouble(script.getM2m());
                    totalM2M +=m2mVal;

                }



                // Add the second set of boxes immediately below the table

                boxTable.addCell(createBottomLeftBoxCell(String.valueOf(totalBuyQty)));
                boxTable.addCell(createBottomRightBoxCell(String.valueOf(totalM2M)));

                document.add(boxTable);

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

    public Cell createBottomLeftBoxCell(String content) {
        Color customColor = new DeviceRgb(255, 255, 153);

        return new Cell(1,5)
                .add(new Paragraph(content).setBold())
                .setPadding(5)
                .setMargin(0)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    public Cell createBottomRightBoxCell(String content) {
        Color customColor = new DeviceRgb(99,99,99);
        return new Cell(1,4)
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5)
                .setMargin(0)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(customColor)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100))  // Ensure it fills the cell space
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    public Cell createTopLeftBoxCell(String content) {
        Color customColor = new DeviceRgb(203, 195  , 227);

        return new Cell(1,5)
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


    public Cell createBoxTextSeperate(String label, String value) {
        Color customColor = new DeviceRgb(203, 195  , 227);
        Paragraph paragraph = new Paragraph()
                .add(label)  // Add the label text
                .add(new Tab())  // Add a tab space to push the value to the right
                .add(value)  // Add the value
                .addTabStops(new TabStop(1000, TabAlignment.RIGHT));  // Ensure right alignment

        return new Cell(1,4)
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

    public Cell createHeaderCell(String content) {
        Color customColor = new DeviceRgb(99,99,99);
        return new Cell().add(new Paragraph(content).setBold())
                .setBackgroundColor(customColor)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(ColorConstants.WHITE);
    }
    public Cell createHeaderCell1(String content) {
        Color customColor = new DeviceRgb(99,99,99);
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