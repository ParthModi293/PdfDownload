
package com.email.pdfdownload.service;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.IOException;

/*
public class HeaderFooterPageEvent implements IEventHandler {

    private String leftHeaderText;
    private String rightHeaderText;
    private String footerText;

    public HeaderFooterPageEvent(String leftHeaderText, String rightHeaderText, String footerText) {
        this.leftHeaderText = leftHeaderText;
        this.rightHeaderText = rightHeaderText;
        this.footerText = footerText;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfCanvas canvas = new PdfCanvas(docEvent.getPage());

        Rectangle pageSize = docEvent.getPage().getPageSize();

        // Header Left (Top Left Corner)
        try {
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), 12)
                    .moveText(pageSize.getLeft() + 20, pageSize.getTop() - 30)
                    .showText(leftHeaderText)
                    .endText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Header Right (Top Right Corner)
        try {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            float fontSize = 12f;
            float rightHeaderWidth = font.getWidth(rightHeaderText, fontSize);

            float rightHeaderX = pageSize.getRight() - rightHeaderWidth - 15;  // Respecting the right margin of 15
            float rightHeaderY = pageSize.getTop() - 30;  // 30 units down from the top (adjust if needed)

            canvas.beginText()
                    .setFontAndSize(font, fontSize)
                    .moveText(rightHeaderX, rightHeaderY)
                    .showText(rightHeaderText)
                    .endText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Draw a line above the footer
        canvas.setLineWidth(0.5f);  // Set line thickness
        canvas.moveTo(pageSize.getLeft() + 15, pageSize.getBottom() + 40);  // Start point (left margin)
        canvas.lineTo(pageSize.getRight() - 15, pageSize.getBottom() + 40); // End point (right margin)
        canvas.stroke();  // Draw the line

        try {
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 10)
                    .moveText(pageSize.getLeft() + 20, pageSize.getBottom() + 20)  // Bottom Left Position
                    .showText("Page " + pdfDoc.getPageNumber(docEvent.getPage()))
                    .endText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Footer Right: Printed Time
        String printedTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        try {
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 10)
                    .moveText(pageSize.getRight() - 150, pageSize.getBottom() + 20)  // Bottom Right Position
                    .showText("Printed on: " + printedTime)
                    .endText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        canvas.release();
    }
}*/

public class HeaderFooterPageEvent implements IEventHandler {

    private String leftHeaderText;
    private String rightHeaderText;


    public HeaderFooterPageEvent(String leftHeaderText, String rightHeaderText) {
        this.leftHeaderText = leftHeaderText;
        this.rightHeaderText = rightHeaderText;

    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

        Rectangle pageSize = page.getPageSize();

        try {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            float fontSize = 12f;

            // Header Left (Top Left Corner)
            canvas.beginText()
                    .setFontAndSize(font, fontSize)
                    .moveText(pageSize.getLeft() + 15, pageSize.getTop() - 30)  // Adjusted to left margin
                    .showText(leftHeaderText)
                    .endText();

            // Header Right (Top Right Corner)
            float rightHeaderWidth = font.getWidth(rightHeaderText, fontSize);
            canvas.beginText()
                    .setFontAndSize(font, fontSize)
                    .moveText(pageSize.getRight() - rightHeaderWidth - 15, pageSize.getTop() - 30)  // Adjusted to right margin
                    .showText(rightHeaderText)
                    .endText();

            // Draw a line above the footer
            canvas.setLineWidth(0.5f);
            canvas.moveTo(pageSize.getLeft() + 15, pageSize.getBottom() + 40)  // Line starts at left margin
                    .lineTo(pageSize.getRight() - 15, pageSize.getBottom() + 40)  // Line ends at right margin
                    .stroke();

            // Footer Left: Page Number
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 10)
                    .moveText(pageSize.getLeft() + 15, pageSize.getBottom() + 20)  // Adjusted to left margin
                    .showText("Page " + pdfDoc.getPageNumber(page))
                    .endText();

            // Footer Right: Printed Time
            String printedTime = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy h.mm a"));
            float printedTimeWidth = font.getWidth("Print Date: " + printedTime, 10);
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 10)
                    .moveText(pageSize.getRight() - printedTimeWidth - 15, pageSize.getBottom() + 20)  // Adjusted to right margin
                    .showText("Print Date: " + printedTime)
                    .endText();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            canvas.release();
        }
    }
}
