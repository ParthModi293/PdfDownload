
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
    private boolean addHeader;
    private boolean addFooter;
    private boolean headerOnAllPages;


    public HeaderFooterPageEvent(String leftHeaderText, String rightHeaderText, boolean addHeader, boolean addFooter, boolean headerOnAllPages) {
        this.leftHeaderText = leftHeaderText;
        this.rightHeaderText = rightHeaderText;
        this.addHeader = addHeader;
        this.addFooter = addFooter;
        this.headerOnAllPages = headerOnAllPages;  // Initialize new flag
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

// Add Header if addHeader is true AND either headerOnAllPages is true OR it's the first page

            // Header Left (Top Left Corner)
            if (addHeader && (headerOnAllPages || pdfDoc.getPageNumber(page) == 1)) {
                // Left Header
                canvas.beginText()
                        .setFontAndSize(font, fontSize)
                        .moveText(pageSize.getLeft() + 15, pageSize.getTop() - 30)
                        .showText(leftHeaderText)
                        .endText();

                // Right Header
                float rightHeaderWidth = font.getWidth(rightHeaderText, fontSize);
                canvas.beginText()
                        .setFontAndSize(font, fontSize)
                        .moveText(pageSize.getRight() - rightHeaderWidth - 15, pageSize.getTop() - 30)
                        .showText(rightHeaderText)
                        .endText();
            }

            // Draw a line above the footer
            canvas.setLineWidth(0.5f);
            canvas.moveTo(pageSize.getLeft() + 15, pageSize.getBottom() + 40)  // Line starts at left margin
                    .lineTo(pageSize.getRight() - 15, pageSize.getBottom() + 40)  // Line ends at right margin
                    .stroke();

            // Add Footer if addFooter is true
            if (addFooter) {
                // Footer Line
                canvas.setLineWidth(0.5f);
                canvas.moveTo(pageSize.getLeft() + 15, pageSize.getBottom() + 40)
                        .lineTo(pageSize.getRight() - 15, pageSize.getBottom() + 40)
                        .stroke();

                // Left Footer: Page Number
                canvas.beginText()
                        .setFontAndSize(font, fontSize)
                        .moveText(pageSize.getLeft() + 15, pageSize.getBottom() + 20)
                        .showText("Page " + pdfDoc.getPageNumber(page))
                        .endText();

                // Right Footer: Printed Time
                String printedTime = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy h.mm a"));
                String rightFooterText = "Print Date: " + printedTime;
                float printedTimeWidth = font.getWidth(rightFooterText, fontSize);

                canvas.beginText()
                        .setFontAndSize(font, fontSize)
                        .moveText(pageSize.getRight() - printedTimeWidth - 15, pageSize.getBottom() + 20)
                        .showText(rightFooterText)
                        .endText();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            canvas.release();
        }
    }
}
