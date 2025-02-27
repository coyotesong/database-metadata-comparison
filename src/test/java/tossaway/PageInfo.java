package tossaway;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.geom.AffineTransform;

public class PageInfo {
    final int pageno;
    PdfTemplate template;
    Rectangle adjustedPageSize;
    AffineTransform transform;
    float ddx;
    float ddy;
    int rotation;

    public PageInfo(int pageno, float factor, Rectangle pageSize, float ddx, float ddy) {
        this.pageno = pageno;
        this.ddx = ddx;
        this.ddy = ddy;

        this.adjustedPageSize = new Rectangle(pageSize.getLeft(), pageSize.getBottom() + ddy, pageSize.getRight(), pageSize.getTop() - ddy);

        if (ddx > 0) {
            if (pageno % 2 == 0) {
                adjustedPageSize.setLeft(adjustedPageSize.getLeft() + ddx);
            } else {
                adjustedPageSize.setRight(adjustedPageSize.getRight() - ddx);
            }
        }

        transform = new AffineTransform();
        transform.translate(pageSize.getLeft(), pageSize.getBottom());
        transform.scale(factor, factor);
    }

    void addContent(PdfWriter pdfWriter, SheetSegments details, PdfImportedPage importedPage) {
        AffineTransform af = new AffineTransform();
        if ((pageno % 2) == 0) {
            af.translate(details.offsetX + ddx, details.offsetY + ddy);
        } else {
            af.translate(details.offsetX, details.offsetY + ddy);
        }
        af.scale(transform.getScaleX(), transform.getScaleY());
        PdfContentByte cb = pdfWriter.getDirectContent();

        cb.addTemplate(importedPage, af);  // note: deprecated!
    }

    void addBorder(PdfWriter pdfWriter, SheetSegments details) {
        PdfContentByte ucb = pdfWriter.getDirectContentUnder();
        ucb.setGrayStroke(0.9f);
        Rectangle r;
        if ((pageno % 2) == 0) {
            r = new Rectangle(details.offsetX + ddx, details.offsetY + ddy, details.offsetX + details.dx, details.offsetY + details.dy - ddy);
        } else {
            r = new Rectangle(details.offsetX, details.offsetY + ddy, details.offsetX + details.dx - ddx, details.offsetY + details.dy - ddy);
        }
        pdfWriter.setCropBoxSize(r);
        addBorder(ucb, r);
    }

    void addBorder(PdfContentByte cb, Rectangle rect) {
        // Rectangle border = new Rectangle(rects[i % n]);
        float margin = 0.5f;
        Rectangle border = new Rectangle(rect);
        // wborder.setBorderWidth(2f);
        // border.setBorderColor(BaseColor.BLUE);
        border.setLeft(border.getLeft() + margin);
        border.setBottom(border.getBottom() + margin);
        border.setRight(border.getRight() - margin);
        border.setTop(border.getTop() - margin);

        cb.saveState();
        // cb.setGrayStroke(0.75f);
        // cb.setColorStroke(BaseColor.BLUE);
        cb.newPath();
        cb.setLineWidth(2 * margin);
        // cb.rectangle(border);
        cb.moveTo(border.getLeft(), border.getBottom());
        cb.lineTo(border.getLeft(), border.getTop());
        cb.lineTo(border.getRight(), border.getTop());
        cb.lineTo(border.getRight(), border.getBottom());
        cb.closePath();
        cb.stroke();
        cb.restoreState();
    }
}
