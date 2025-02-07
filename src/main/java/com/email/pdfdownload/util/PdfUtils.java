package com.email.pdfdownload.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;

public class PdfUtils {

    public static PdfPCell getCell(String data, int... colRowSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase(data));
        cell.setPadding(5);
        if (colRowSpan.length > 0 && colRowSpan[0] != 0) {
            cell.setRowspan(colRowSpan[0]);
        }
        if (colRowSpan.length > 1 && colRowSpan[1] != 0) {
            cell.setColspan(colRowSpan[1]);
        }
        return cell;
    }

    public static PdfPCell getCenterCell(String data, int... colRowSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(data));
        if (colRowSpan.length > 0 && colRowSpan[0] != 0) {
            cell.setRowspan(colRowSpan[0]);
        }
        if (colRowSpan.length > 1 && colRowSpan[1] != 0) {
            cell.setColspan(colRowSpan[1]);
        }
        return cell;
    }

    public static PdfPCell getBoldCenterCell(String data, int... colRowSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(data, FontFactory.getFont("Arial", 12, Font.BOLD, BaseColor.BLACK)));
        cell.setBackgroundColor(new BaseColor(200, 233, 233));
        if (colRowSpan.length > 0 && colRowSpan[0] != 0) {
            cell.setRowspan(colRowSpan[0]);
        }
        if (colRowSpan.length > 1 && colRowSpan[1] != 0) {
            cell.setColspan(colRowSpan[1]);
        }
        return cell;
    }

    public static PdfPCell getCellNoBorder(String data, int... colRowSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPhrase(new Phrase(data, FontFactory.getFont("Arial", 12, Font.BOLD, BaseColor.BLACK)));
        if (colRowSpan.length > 0 && colRowSpan[0] != 0) {
            cell.setRowspan(colRowSpan[0]);
        }
        if (colRowSpan.length > 1 && colRowSpan[1] != 0) {
            cell.setColspan(colRowSpan[1]);
        }
        return cell;
    }

    public static PdfPCell getEmptyCellNoBorder(int... colRowSpan) {
        try {
            PdfPCell cell = new PdfPCell();
            cell.setPadding(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPhrase(new Phrase(" ", new Font(BaseFont.createFont(), 6)));
            if (colRowSpan.length > 0 && colRowSpan[0] != 0) {
                cell.setRowspan(colRowSpan[0]);
            }
            if (colRowSpan.length > 1 && colRowSpan[1] != 0) {
                cell.setColspan(colRowSpan[1]);
            }
            return cell;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static PdfPCell getRightCell(String data, int... colRowSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPhrase(new Phrase(data));
        cell.setPadding(5);
        cell.setRowspan(colRowSpan.length > 0 ? colRowSpan[0] : 0);
        cell.setColspan(colRowSpan.length > 1 ? colRowSpan[1] : 0);
        return cell;
    }

    public static PdfPCell getHeaderCell(String data) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPhrase(new Phrase(data));
        cell.setPadding(5);
        return cell;
    }

    public static PdfPCell getHeaderCenterCell(String data) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPhrase(new Phrase(data));
        cell.setPadding(5);
        return cell;
    }

    public static PdfPCell getHeaderRightCell(String data) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPhrase(new Phrase(data));
        cell.setPadding(5);
        return cell;
    }
}
