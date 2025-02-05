package com.email.pdfdownload.service;


import com.email.pdfdownload.dto.AllScript;
import com.email.pdfdownload.dto.RequestDto;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateTablePdf(List<RequestDto> requestDtos) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            /*for (RequestDto requestDto : requestDtos) {
                // Add header section
                Table headerTable = new Table(2);
                headerTable.addCell(new Cell().add(new Paragraph("Client Name: " + requestDto.getClientName()))
                        .setBorder(null).setTextAlignment(TextAlignment.LEFT));
                headerTable.addCell(new Cell().add(new Paragraph("Ledger Balance: " + requestDto.getLedgerBalance()))
                        .setBorder(null).setTextAlignment(TextAlignment.RIGHT));
                document.add(headerTable);

                // Add space
                document.add(new Paragraph("\n"));

                // Add table header
                Table table = new Table(new float[]{3, 2, 2, 2, 2, 2, 2, 2});
                table.setMaxWidth(1000);
                table.addHeaderCell(createHeaderCell("Script Name"));
                table.addHeaderCell(createHeaderCell("Buy Qty"));
                table.addHeaderCell(createHeaderCell("Buy Avg"));
                table.addHeaderCell(createHeaderCell("Sell Avg"));
                table.addHeaderCell(createHeaderCell("Sell Qty"));
                table.addHeaderCell(createHeaderCell("Pos Qty"));
                table.addHeaderCell(createHeaderCell("LTP"));
                table.addHeaderCell(createHeaderCell("M2M"));

                // Add rows dynamically from DTO
                for (AllScript script : requestDto.getScripts()) {
                    table.addCell(createCell(script.getScriptName()));
                    table.addCell(createCell(script.getBuyQty()));
                    table.addCell(createCell(script.getBuyAvg()));
                    table.addCell(createCell(script.getSellAvg()));
                    table.addCell(createCell(script.getSellQty()));
                    table.addCell(createCell(script.getPosQty()));
                    table.addCell(createCell(script.getLtp()));
                    table.addCell(createCell(script.getM2m()));
                }
                document.add(table);

                // Add a page break for each RequestDto
                document.add(new Paragraph("\n"));
            }*/

//            NEW

            for (RequestDto requestDto : requestDtos) {
                // Create a 2-column table for the client name and ledger balance
                float[] boxWidths = {1, 1};
                Table boxTable = new Table(boxWidths);
                boxTable.setWidth(UnitValue.createPercentValue(100)); // Full width

                // Add client name and ledger balance in separate boxes
                boxTable.addCell(createBoxCell("Client Name: " + requestDto.getClientName()));
                boxTable.addCell(createBoxCell("Ledger Balance: " + requestDto.getLedgerBalance()));

                // Add the box table to the document
                document.add(boxTable);

                // Create a table for script details (8 columns)
                float[] columnWidths = {2, 2, 2, 2, 2, 2, 2, 2};
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
                            // Simply add the cell with the default color (no color set)
                            scriptTable.addCell(createDataCell(posQtyValue));
                        } else {
                            double posQty = Double.parseDouble(cleanedPosQtyValue);
                            if (posQty < 0) {
                                // Negative: Red text
                                scriptTable.addCell(createColoredDataCell(posQtyValue, ColorConstants.RED));
                            } else {
                                // Positive or Zero: Blue text
                                scriptTable.addCell(createColoredDataCell(posQtyValue, ColorConstants.BLUE));
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Invalid number format, add uncolored cell
                        scriptTable.addCell(createDataCell(posQtyValue));
                    }

                    scriptTable.addCell(createDataCell(script.getLtp()));


                    String m2mValue = script.getM2m();
                    try {
                        double m2m = Double.parseDouble(m2mValue);
                        if (m2m < 0) {
                            // M2M value is negative: red text
                            scriptTable.addCell(createColoredDataCell(m2mValue, ColorConstants.RED));
                        } else {
                            // M2M value is positive or zero: blue text
                            scriptTable.addCell(createColoredDataCell(m2mValue, ColorConstants.BLUE));
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid number format (add default cell without coloring)
                        scriptTable.addCell(createDataCell(m2mValue));
                    }

                }

                // Add the script table directly below the boxes
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

    /*private Cell createHeaderCell(String content) {
        return new Cell().add(new Paragraph(content).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setTextAlignment(TextAlignment.CENTER);
    }*/

    private Table createEmptyBoxTable() {
        float[] boxWidths = {1, 1};
        Table boxTable = new Table(boxWidths);
        boxTable.setWidth(UnitValue.createPercentValue(100)); // Full width

        // Add two empty boxes
        boxTable.addCell(createBoxCell("Hello")); // Empty Box 1
        boxTable.addCell(createBoxCell("Hello")); // Empty Box 2

        return boxTable;
    }

    private Cell createBoxCell(String content) {
        return new Cell().add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
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

    private Cell createColoredDataCell(String content, com.itextpdf.kernel.colors.Color color) {
        return new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(color); // Dynamically set color
    }
}