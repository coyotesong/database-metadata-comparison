package tossaway;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DocumentInfo {
    // is there a way to guess whether we should use A4 as the default instead?
    private static PaperSize DEFAULT_SHEET_SIZE = PaperSize.LETTER;

    private final Document document;

    // the 'page' is a single page from the document. There may be more than one pages per sheet
    private final PaperSize pageSize;

    int pagesPerSheet;

    private SheetInfo sheetInfo;
    private PdfTemplate[] cropmarks;
    private PdfTemplate[] watermarks;

    /**
     * Read contents of /etc/papersize, if available.
     */
    static {
        final File file = new File("/etc/papersize)");
        if (file.exists() && file.canRead()) {
            try {
                final String contents = Files.readString(file.toPath()).trim().toUpperCase();
                final PaperSize paperSize = PaperSize.valueOf(contents);
                if (paperSize != null) {
                    DEFAULT_SHEET_SIZE = paperSize;
                }
            } catch (IOException e) {
                // unexpected given 'if' statement but nothing to worry about.
            }
        }
        System.out.printf("Default sheet size: %s\n", DEFAULT_SHEET_SIZE);
    }

    public DocumentInfo(PaperSize pageSize, boolean duplex) {
        this(DEFAULT_SHEET_SIZE, pageSize, duplex, Punches.NONE, 0f);
    }

    public DocumentInfo(PaperSize pageSize, boolean duplex, Punches punches) {
        this(DEFAULT_SHEET_SIZE, pageSize, duplex, punches, 0f);
    }

    public DocumentInfo(PaperSize pageSize, boolean duplex, Punches punches, float margin) {
        this(DEFAULT_SHEET_SIZE, pageSize, duplex, punches, margin);
    }

    public DocumentInfo(PaperSize sheetSize, PaperSize pageSize, boolean duplex) {
        this(sheetSize, pageSize, duplex, Punches.NONE, 0f);
    }

    public DocumentInfo(PaperSize sheetSize, PaperSize pageSize, boolean duplex, Punches punches) {
        this(sheetSize, pageSize, duplex, punches, 0f);
    }

    /**
     * Constructor
     *
     * @param sheetSize size of physical page
     * @param pageSize size of individual page wihin document.
     * @param duplex true if printing on both sides of the sheet
     * @param punches type of hole punches
     * @param margin additional space for hole punches if document doesn't already provide this space
     */
    public DocumentInfo(PaperSize sheetSize, PaperSize pageSize, boolean duplex, Punches punches, float margin) {
        this.document = new Document(sheetSize.getRectangle(), 0, 0, 0, 0);
        document.open();

        this.pageSize = pageSize;

        this.pagesPerSheet = 1;

        this.cropmarks = new PdfTemplate[duplex ? 2 : 1];
        this.watermarks = new PdfTemplate[duplex ? 2 : 1];
        this.sheetInfo = new SheetInfo(sheetSize.getRectangle(), pageSize.getRectangle(), duplex, punches, margin, pagesPerSheet);
    }

    public Document getDocument() {
        return document;
    }

    public PaperSize getPageSize() {
        return pageSize;
    }

    public SheetInfo getSheetInfo() {
        return sheetInfo;
    }

    public int getPagesPerSheet() {
        return pagesPerSheet;
    }

    public void merge(PdfWriter pdfWriter, PdfReader pdfReader) throws IOException {
        sheetInfo.createSheetTemplates(pdfWriter);
        // @todo add cropmarks, watermarks

        sheetInfo.createPageTemplates(pdfWriter, pdfReader);

        int max = pagesPerSheet * ((1 + pdfReader.getNumberOfPages()) / pagesPerSheet);

        int newPageAt = pagesPerSheet / (sheetInfo.duplex ? 2 : 1);
        for (int i = 0; i < max; i++) {
            if (i % newPageAt == 0) {
                document.newPage();
            }

            sheetInfo.mergeSinglePage(pdfWriter, pdfReader, i);
        }

        // make sure there's an even number of pages
        if ((document.getPageNumber() % 2) == 1) {
            document.newPage();
        }

        // merge crop marks, watermarks
    }

    public void close(PdfWriter pdfWriter) throws IOException {
        sheetInfo.close(pdfWriter);
        // pdfWriter.releaseTemplate(cropmarks);
        // pdfWriter.releaseTemplate(watermarks);
        document.close();
    }
}
