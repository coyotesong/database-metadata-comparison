package tossaway;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.List;

/**
 * Create PDF document with crop marks
 *
 * <p>
 * While printers usually support many paper sizes they have limits:
 * <ul>
 *     <li>they may omit a common size (e.g., 'half-letter' on my HP LaserJet)</li>
 *     <li>they may not support duplex printing on smaller paper sizes</li>
 *     <li>they may not support the smallest paper sizes at all</li>
 * </ul>
 * </p>
 * <p>
 * This class provides a solution to some of these problems. It will fit one or more smaller
 * pages onto a standard paper size (typically US Letter or A4) - complete with duplex alignment.
 * This class also supports 3-, 6-, or 7-hole punches.
 * </p>
 */
public class CreatePrinterBackground {
    private static final File TMPDIR = new File(System.getProperty("java.io.tmpdir"));

    private static final float DPI = 72f;
    private static final float POINTS_PER_INCH = 72f;
    private static final float MM_PER_INCH = 25.4f;

    private static final float OFFSET = 2;
    private static final float LENGTH = DPI * .25f;
    private static final float PUNCH_HOLE_RADIUS = 5f;

    // safety margin - 5 mm seems standard.
    private static final float SAFETY_MARGIN = 5f / MM_PER_INCH;

    // private static final BaseColor CROP_MARK_COLOR = new BaseColor(172, 172, 172);

    // private static final float CROP_MARK_GRAY = 0.75f;
    private static final float CROP_MARK_GRAY = 0.65f;
    private static final float FONT_SIZE = 10f;
    // add color doesn't seem to have an effect :-(
    private static final BaseFont HELVETICA = FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE).getBaseFont();

    private final Rectangle sheetSize;

    private DocumentInfo documentInfo;

    public CreatePrinterBackground(PaperSize sheet, PaperSize page, boolean duplex, Punches punches, float margin) {
        this.documentInfo = new DocumentInfo(sheet, page, duplex, punches, margin);

        documentInfo.getSheetInfo().crop();

        this.sheetSize = sheet.getRectangle();
    }

    /*
    void addHolePunches(PdfContentByte cb, Rectangle inner, boolean flip) {
        if (punches == Punches.NONE) {
            return;
        }

        float x;
        float y;
        float delta = DPI * (3f / 4f);
        float offsetX = DPI * 0.3f;

        float offsetY = DPI * 0.3f;
        switch (punches) {
            case A7_6:
                offsetX = DPI * 0.25f;
                offsetY = DPI * (5f / 8f);
                break;

            case A5_6:
            case A6_6:
            case SLIMLINE_6:
                offsetY = DPI * (7f / 8f);
                break;
        }

        cb.newPath();
        // cb.setGrayStroke(CROP_MARK_GRAY);
        cb.setGrayStroke(0.9f);
        switch (punches) {
            // case HALF_LETTER_3:
            case THREE_HOLES:
            case A5_3:
                // add center hole
                x = flip ? (inner.getRight() - DPI * 0.25f) : (inner.getLeft() + DPI * 0.25f);
                y = inner.getBottom() + inner.getHeight() / 2f;
                cb.circle(x, y, PUNCH_HOLE_RADIUS);

                // add other holes...
                break;

            case SIX_HOLES:
            case A5_6:
            case A6_6:
            case A7_6:
            case SLIMLINE_6:
                x = flip ? (inner.getRight() - offsetX) : (inner.getLeft() + offsetX);

                y = inner.getBottom() + offsetY;
                for (int i = 0; i < 3; i++, y += delta) {
                    cb.circle(x, y, PUNCH_HOLE_RADIUS);
                }

                y = inner.getTop() - offsetY;
                for (int i = 0; i < 3; i++, y -= delta) {
                    cb.circle(x, y, PUNCH_HOLE_RADIUS);
                }
                break;
        }
        cb.stroke();

        // cb.setColorStroke(BaseColor.LIGHT_GRAY);
    }
    */

    /*
    SheetInfo NUp(String filename, int pow) throws IOException {
        SheetInfo scaling = documentInfo.getSheetInfo();
        PdfReader reader = new PdfReader(filename);

        Rectangle pageSize = reader.getPageSize(1);
        // System.out.printf("pagesize :  %4.0f  %4.0f\n", pageSize.getWidth(), pageSize.getHeight());

        scaling.newSize = (pow % 2) == 0
                ? new Rectangle(pageSize.getWidth(), pageSize.getHeight())
                : new Rectangle(pageSize.getHeight(), pageSize.getWidth());

        // System.out.printf("new size :  %4.0f  %4.0f\n", scaling.newSize.getWidth(), scaling.newSize.getHeight());

        scaling.unitSize = new Rectangle(pageSize.getWidth(), pageSize.getHeight());
        for (int i = 0; i < pow; i++) {
            scaling.unitSize = new Rectangle(scaling.unitSize.getHeight() / 2, scaling.unitSize.getWidth());
        }
        int n = (int) Math.pow(2, pow);
        int r = (int) Math.pow(2, pow / 2);
        int c = n / r;

        return scaling;
    }
     */

    // from "iText in Action"
    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {

        final PdfReader pdfReader = new PdfReader(src);
        final PdfWriter pdfWriter = PdfWriter.getInstance(documentInfo.getDocument(), new FileOutputStream(dest));

        documentInfo.merge(pdfWriter, pdfReader);

        /*
        // now define the frills
        for (int i = 0; i < scaling.details.length; i++) {
            defineFrills(scaling.details[i].template, i, scaling, 0, 1f);
        }
         */

        documentInfo.close(pdfWriter);
    }

    void defineFrills(PdfContentByte cb, int pageno, SheetInfo scaling, float ddy, float margin) {
        cb.saveState();
        // cb.setColorStroke(BaseColor.BLUE);
        cb.newPath();
        cb.setLineWidth(1f);
        cb.setGrayStroke(0.9f);
        SheetSegments details = scaling.details[pageno % scaling.details.length];
        float offsetX = details.offsetX;
        float offsetY = details.offsetY;
        float dx = scaling.dx;
        float dy = scaling.dy;

        if (pageno % 2 == 0) {
            cb.moveTo(offsetX + margin, offsetY);
            cb.lineTo(offsetX + margin, offsetY + dy);
            cb.stroke();
            // addSixHolePunches(cb, false);
            Rectangle border1 = new Rectangle(offsetX + margin, offsetY + ddy, offsetX + dx, offsetY + dy - ddy);
            cb.setGrayStroke(0.9f);
            addBorder(cb, border1);
        } else {
            cb.moveTo(offsetX + dx - margin, offsetY);
            cb.lineTo(offsetX + dx - margin, offsetY + dy);
            cb.stroke();
            // addSixHolePunches(cb, true);
            Rectangle border1 = new Rectangle(offsetX, offsetY + ddy, offsetX + dx - margin, offsetY + dy - ddy);
            cb.setGrayStroke(0.9f);
            addBorder(cb, border1);
        }

        cb.setGrayStroke(0.75f);
        Rectangle border = new Rectangle(offsetX, offsetY, offsetX + dx, offsetY + dy);
        addBorder(cb, border);
        cb.restoreState();
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

    void writeDict(PrintStream os, PdfDictionary dict) {
        for (PdfName name : dict.getKeys()) {
            if (name.isName()) {
                os.printf("name:   %-16.16s  %s\n", name, dict.getAsName(name));
            } else if (name.isBoolean()) {
                os.printf("bool:   %-16.16s  %s\n", name, dict.getAsBoolean(name));
            } else if (name.isNumber()) {
                os.printf("numb:   %-16.16s  %s\n", name, dict.getAsNumber(name));
            } else if (name.isNull()) {
                os.printf("null:   %-16.16s\n", name);
            } else if (name.isArray()) {
                os.printf("arry:   %-16.16s\n", name);
            } else if (name.isString()) {
                os.printf("strg:   %-16.16s %s\n", name, dict.getAsString(name));
            } else {
                os.printf("othr:   %-16.16s\n", name, dict.getAsString(name));
            }
        }
    }

    void stamp(File file, OutputStream os) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(file.getAbsolutePath());
        PdfStamper stamper = new PdfStamper(reader, os);

        // PdfDictionary catalog = reader.getCatalog();
        // PRAcroForm form = reader.getAcroForm();
        // AcroFields fields = reader.getAcroFields();
        // Map<String, String> info = reader.getInfo();

        writeDict(System.out, reader.getCatalog());
        System.out.println();

                        /*
                        PdfImportedPage[] cropmarks = new PdfImportedPage[2];
                        PdfReader r = new PdfReader("foo");
                        cropmarks[0] = stamper.getImportedPage(r, 1);
                        cropmarks[1] = stamper.getImportedPage(r, 2);
                        r.close();
                         */

        // PdfImportedPage page = stamper.getImportedPage(stationery, 1);
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            if (i == 1) {
                // PdfDictionary catalog = reader.getCatalog();
                writeDict(System.out, reader.getPageN(i));

                Rectangle rect = reader.getPageSize(i);
                System.out.printf("size: %.0f by %.0f\n", rect.getWidth(), rect.getHeight());
            }

            // PdfContentByte content = stamper.getUnderContent(i);
            // content.addTemplate(cropmarks[i % 2], 0, 0);
        }
        stamper.close();
    }

    public static void main(String[] args) throws IOException, DocumentException {
        CreatePrinterBackground app = new CreatePrinterBackground(PaperSize.LETTER, PaperSize.CAGIE_A7, true, Punches.A7_6, DPI * 0.4f);
        File inFile = new File("/tmp/Home.pdf");
        File outFile = new File("/tmp/Home.out.pdf");

        app.manipulatePdf(inFile.getAbsolutePath(), outFile.getAbsolutePath());
    }
}
