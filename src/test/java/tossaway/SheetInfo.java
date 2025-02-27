package tossaway;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sheet information
 *
 * @todo handle landscape
 */
public class SheetInfo {
    private final Rectangle sheetSize;
    private final Rectangle pageSize;

    // this is used with 'landscape'
    // private final Rectangle newSize;

    boolean duplex;
    int pagesPerSheet;
    float margin;

    Rectangle newSize;
    Rectangle unitSize;

    float dx;
    float dy;

    SheetSegments[] details;
    PdfTemplate[] templates;
    List<PageInfo> pages;

    int lookup[];

    /**
     * Constructor
     *
     * @param pageSize destination paper
     */
    public SheetInfo(Rectangle sheetSize, Rectangle pageSize, boolean duplex, Punches punches, float margin, int pagesPerSheet) {
        this.sheetSize = sheetSize;
        this.pageSize = pageSize;
        this.duplex = duplex;
        this.pagesPerSheet = pagesPerSheet;
        this.margin = margin;
        this.templates = new PdfTemplate[duplex ? 2 : 1];

        // @todo generalize
        switch (pagesPerSheet) {
            case 1:
                lookup = (duplex) ? new int[] { 0, 1 } : new int[] { 0 };
                break;

            case 2:
                lookup = (duplex) ? new int[] { 0, 2, 3, 1 } : new int[] { 0, 1 };
                break;

            case 4:
                lookup = (duplex) ? new int[] { 0, 2, 4, 6, 3, 1, 7, 5 } : new int[] { 0, 1, 2, 3 };
                break;

            default:
                System.err.println("unsupported number of pages per sheet!");
                throw new IllegalArgumentException("unsupported number of pages per sheet!");
        }
    }

    public void close(PdfWriter writer) throws IOException {
        for (SheetSegments details : details) {
            details.close(writer);
        }
    }

    /**
     * Determine the crop box for each page
     */
    void crop() {
        // todo: check for landscape mode!
        Rectangle page = new RectangleReadOnly(pageSize.getLeft(), pageSize.getBottom(), pageSize.getRight(), pageSize.getTop());

        this.details = new SheetSegments[pagesPerSheet * (duplex ? 2 : 1)];

        this.unitSize = new Rectangle(0, 0, page.getWidth(), page.getHeight());

        // find midpoint of page
        float midX = pageSize.getWidth() / 2f;
        float midY = pageSize.getHeight() / 2f;

        float factor = Math.min(this.unitSize.getWidth() / (page.getWidth()), this.unitSize.getHeight() / page.getHeight());
        this.dx = page.getWidth() * factor;
        this.dy = page.getHeight() * factor;

        float llx;
        float ury;
        Rectangle r;

        switch (pagesPerSheet) {

            case 1:
                r = new RectangleReadOnly(unitSize);
                details[0] = new SheetSegments(r, this.unitSize, this.dx, this.dy);
                if (duplex) {
                    details[1] = details[0];
                }
                break;

            case 2:
                llx = unitSize.getLeft();
                ury = midY - unitSize.getHeight();
                r = new RectangleReadOnly(llx, ury, llx + unitSize.getWidth(), ury + unitSize.getHeight());
                details[0] = new SheetSegments(r, this.unitSize, this.dx, this.dy);

                llx = unitSize.getLeft();
                ury = midY;
                r = new RectangleReadOnly(llx, ury, llx + unitSize.getWidth(), ury + unitSize.getHeight());
                details[1] = new SheetSegments(r, this.unitSize, this.dx, this.dy);

                if (duplex) {
                    details[2] = details[1];

                    details[1] = details[0];
                    details[3] = details[1];
                }
                break;

            case 4:
                llx = midX - unitSize.getWidth();
                ury = midY;

                r = new RectangleReadOnly(llx, ury, llx + unitSize.getWidth(), ury + unitSize.getHeight());
                details[0] = new SheetSegments(r, this.unitSize, this.dx, this.dy);

                llx = midX;
                ury = midY;
                r = new RectangleReadOnly(llx, ury, llx + unitSize.getWidth(), ury + unitSize.getHeight());
                details[1] = new SheetSegments(r, this.unitSize, this.dx, this.dy);

                llx = midX - unitSize.getWidth();
                ury = midY - unitSize.getHeight();

                r = new RectangleReadOnly(llx, ury, llx + unitSize.getWidth(), ury + unitSize.getHeight());
                details[2] = new SheetSegments(r, this.unitSize, this.dx, this.dy);

                llx = midX;
                ury = midY - unitSize.getHeight();

                r = new RectangleReadOnly(llx, ury, llx + unitSize.getWidth(), ury + unitSize.getHeight());
                details[3] = new SheetSegments(r, this.unitSize, this.dx, this.dy);

                if (duplex) {
                    details[6] = details[3];
                    details[4] = details[2];
                    details[2] = details[1];

                    details[1] = details[2];
                    details[3] = details[0];
                    details[5] = details[6];
                    details[7] = details[4];
                }
        }

        if (duplex) {
            for (int i = 0; i < pagesPerSheet; i++) {
                details[i + pagesPerSheet] = details[i];
            }
        }
    }

    public void createSheetTemplates(PdfWriter pdfWriter) throws IOException {
        // set up some templates
        for (int i = 0; i < pagesPerSheet; i++) {
            final PdfContentByte cb = pdfWriter.getDirectContentUnder();
            details[i].template = cb.createTemplate(sheetSize.getWidth(), sheetSize.getHeight());
        }
    }

    public List<PageInfo> createPageTemplates(PdfWriter pdfWriter, PdfReader reader) {
        PdfContentByte cb = pdfWriter.getDirectContent();

        int pageCount = reader.getNumberOfPages();

        pages = new ArrayList<>();

        for (int pageno = 0; pageno < pageCount; pageno++) {
            // default size
            Rectangle expectedSize = details[pageno % pagesPerSheet].rect;

            // actual page
            Rectangle actualSize = reader.getPageSize(pageno + 1);
            float factor = Math.min((unitSize.getWidth() - margin) / actualSize.getWidth(), unitSize.getHeight() / actualSize.getHeight());
            float ddy = (dy - actualSize.getHeight() * factor) / 2f;

            PageInfo page = new PageInfo(pageno, factor, expectedSize, margin, ddy);
            pages.add(page);

            page.template = cb.createTemplate(page.adjustedPageSize.getWidth(), page.adjustedPageSize.getHeight());
        }

        return pages;
    }

    public void mergeSinglePage(PdfWriter pdfWriter, PdfReader pdfReader, int i) {
        final SheetSegments details = this.details[i];

        int j = lookup[i];
        if (i < pages.size()) {
            final PdfImportedPage importedPage = pdfWriter.getImportedPage(pdfReader, 1 + j);

            final PageInfo page = pages.get(i);
            page.addContent(pdfWriter, details, importedPage);
            page.addBorder(pdfWriter, details);
        }

        // reset crop box
        pdfWriter.setCropBoxSize(sheetSize);
    }
}
