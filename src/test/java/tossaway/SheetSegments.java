package tossaway;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;

public class SheetSegments {
    final Rectangle rect;
    final Rectangle unitSize;
    final float offsetX;
    final float offsetY;
    final float dx;
    final float dy;
    PdfTemplate template;
    int rotation;

    public SheetSegments(Rectangle rect, Rectangle unitSize, float dx, float dy) {
        this.rect = rect;
        this.unitSize = unitSize;
        this.offsetX = this.rect.getLeft() + (unitSize.getWidth() - dx) / 2f;
        this.offsetY = this.rect.getBottom() + (unitSize.getHeight() - dy) / 2f;
        this.dx = dx;
        this.dy = dy;
    }

    public void close(PdfWriter writer) throws IOException {
        try {
            if (template != null) {
                writer.releaseTemplate(template);
                template = null;
            }
        } finally {
            template = null;
        }
    }
}
