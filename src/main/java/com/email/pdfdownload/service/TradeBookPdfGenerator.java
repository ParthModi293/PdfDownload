package com.email.pdfdownload.service;

import com.email.pdfdownload.dto.CustomerRms.CustomerRmsRequestDto;
import com.email.pdfdownload.dto.TradeBook.TradeBookAllScript;
import com.email.pdfdownload.dto.TradeBook.TradeBookRequestDto;
import com.email.pdfdownload.dto.TradeBook.TradeClient;
import com.email.pdfdownload.dto.TradeBook.TradeWithDates;
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
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class TradeBookPdfGenerator {

    public byte[] generateTablePdf(TradeBookRequestDto tradeBookRequestDto) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            PageSize customPageSize = new PageSize(PageSize.A4.getWidth(), PageSize.A4.getHeight()); // Increase height by 200 points
            Document document = new Document(pdfDoc, customPageSize.rotate());
            document.setMargins(50, 15, 50, 15);



            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderFooterPageEvent("leftHeaderText", "rightHeaderText", false, true, false));
            float[] topLine = {1f,1f,1f};

            Table topBoxTable = new Table(topLine);
            topBoxTable.setWidth(UnitValue.createPercentValue(100));
            topBoxTable.setMargin(0).setPadding(0);
            topBoxTable.setFixedLayout();

            String tradeSymbol = tradeBookRequestDto.getTradeSymbol();
            String month = tradeBookRequestDto.getMonth();
            String fromDate = tradeBookRequestDto.getFromDate();
            String toDate = tradeBookRequestDto.getToDate();

            Cell leftCell = new Cell()
                    .add(new Paragraph("Trade Book")
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                            .setFontSize(14))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT);

            Cell middleCell = new Cell()
                    .add(new Paragraph(tradeSymbol + " " + month)
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                            .setFontSize(14))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);



            Cell rightCell = new Cell()
                    .add(new Paragraph("From: " + fromDate + "  To: " + toDate)
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                            .setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);

            topBoxTable.addCell(leftCell);
            topBoxTable.addCell(middleCell);
            topBoxTable.addCell(rightCell);
            document.add(topBoxTable);

            for(TradeClient tc: tradeBookRequestDto.getClientList()){

                if (tradeBookRequestDto.getClientList().indexOf(tc) != 0) {
                    document.add(new AreaBreak());  // Adds a page break
                }

                float[] boxWidths = {5f,3.5f,3.5f,4f,4f,4f};
                Table boxTable = new Table(boxWidths);
                boxTable.setWidth(UnitValue.createPercentValue(100));
                boxTable.setMargin(0).setPadding(0);
                boxTable.setFixedLayout();

                boxTable.addCell(createClientNameCell(tc.getClientName()));


                for(TradeWithDates td: tc.getTradeWithDates()){

                    boxTable.addCell(createDateCell(td.getDate()));

                    boxTable.addCell(createHeaderCell1("Script"));
                    boxTable.addCell(createHeaderCell("Time"));
                    boxTable.addCell(createHeaderCell("Lot"));
                    boxTable.addCell(createHeaderCell("Buy Qty"));
                    boxTable.addCell(createHeaderCell("Sell Qty"));
                    boxTable.addCell(createHeaderCell("Rate"));

                    for(TradeBookAllScript ts: td.getScripts()){

                        boxTable.addCell(createDataCell1(ts.getScriptName()));
                        boxTable.addCell(createDataCell(ts.getTime()));
                        boxTable.addCell(createDataCell(formatDouble(ts.getLot())));
                        boxTable.addCell(createBlueFontDataCell(formatDouble(ts.getBuyQty())));
                        boxTable.addCell(createRedFontDataCell(formatDouble(ts.getSellQty())));
                        boxTable.addCell(createDataCell(formatDouble(ts.getRate())));

                    }

                }

                document.add(boxTable);
            }



            document.close();
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }


    }
    public String formatDouble(Double value) {
        if (value == null || value == 0) {
            return "";
        }
        return String.format("%.2f", value);
    }


    public Cell createClientNameCell(String content) {

        return new Cell(1,6)
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5)
                .setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100));

    }
    public Cell createDateCell(String content) {
        Color customColor = new DeviceRgb(203, 195  , 227);
        return new Cell(1,6)
                .add(new Paragraph(content).setBold())
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(customColor)
                .setPadding(5)
                .setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.BLACK,1))
                .setKeepTogether(true)
                .setWidth(UnitValue.createPercentValue(100));

    }
    public Cell createHeaderCell1(String content) {
        Color customColor = new DeviceRgb(99,99,99);
        return new Cell().add(new Paragraph(content).setBold())
                .setBackgroundColor(customColor)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.WHITE);
    }

    public Cell createHeaderCell(String content) {
        Color customColor = new DeviceRgb(99,99,99);
        return new Cell().add(new Paragraph(content).setBold())
                .setBackgroundColor(customColor)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(ColorConstants.WHITE);
    }

    public Cell createDataCell1(String content) {
        return new Cell().add(new Paragraph(content))
                .setTextAlignment(TextAlignment.LEFT);
    }
    public Cell createDataCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setTextAlignment(TextAlignment.RIGHT);
    }
    public Cell createRedFontDataCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setFontColor(ColorConstants.RED)
                .setTextAlignment(TextAlignment.RIGHT);
    }
    public Cell createBlueFontDataCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setFontColor(ColorConstants.BLUE)
                .setTextAlignment(TextAlignment.RIGHT);
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
