package tossaway;

import com.itextpdf.awt.geom.Dimension;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RectangleReadOnly;

/**
 * Standard paper sizes.
 *
 * <p>
 * This enumeration includes the name used by HP printers.
 * </p>
 */
// from https://en.wikipedia.org/wiki/Paper_size,
// https://paper-size.com/c/a-paper-sizes.html, and
// https://support.hp.com/us-en/document/c06024193
public enum PaperSize {
    LEGAL("Legal", PageSize.LEGAL), // 8.5 x 14
    OFICIO("Oficio", 8.5, 13),
    LETTER("Letter", PageSize.LETTER), // 8.5 x 11
    EXECUTIVE("Executive", PageSize.EXECUTIVE), // 7.25 x 10.5
    NOTE(PageSize.NOTE), // 7.5 x 10
    HALF_LETTER(PageSize.HALFLETTER), // 5.5 x 8.5
    MEMO(PageSize.HALFLETTER),
    STATEMENT(PageSize.HALFLETTER),
    MINI(PageSize.HALFLETTER),
    INVOICE(PageSize.HALFLETTER),

    A4("A4", PageSize.A4), // 210 x 297 mm or 8.3 x 11.7 in
    A5("A5", PageSize.A5), // 148 x 210 mm or 4.8 x 8.3 in
    A6("A6", PageSize.A6), // 105 x 148 mm or 4.1 x 5.8 in
    A7(PageSize.A7), // 74 x 105 mm or 2.9 x 4.1 in
    A8(PageSize.A8), // 52 x 74 mm or 2 x 2.9 in
    A9(PageSize.A9),

    B4(PageSize.B4),
    B5("B5", PageSize.B5), // 176 x 250 mm or 6.9 x 9.8 in
    B6("B6", PageSize.B6), // 126 x 166 mm or 4.9 x 6.9 in
    B7(PageSize.B7), // 88 x 125 mm or 3.5 x 4.9 in
    B8(PageSize.B8), // 62 x 88 mm or 2.4 x 3.5 in
    B9(PageSize.B9),

    POSTCARD("Postcard", PageSize.POSTCARD),

    // HP (not including ISOB5, FanFoldGermanLegal, or Roc16k.
    DOUBLE_POSTCARD_ROTATED("DoublePostcardRotated", PageSize.POSTCARD.getHeight(), 2 * PageSize.POSTCARD.getWidth()),
    MM_100x150("100x150mm", mm(100), mm(150)), // 3.9 x 5.9
    MM_184x260("184x260mm", mm(184), mm(260)), // 7.2 x 10.2
    MM_185x270("195x270mm", mm(185), mm(270)), // 7.6 x 10.6
    CARD_3x5(3 , 5), // 76 x 127 mm
    CARD_4x6("4x6", 4, 6), // 102 x 152 mm
    CARD_5x8("5x8", 5, 8), // 127 x 203 mm

    // HP envelopes
    ENV_10("Env10", 4.125, 9.5),  // or 105 x 241 mm
    ENV_B5("EnvB5", mm(176), mm(250)), // 6.9 x 9.8
    ENV_C5("EnvC5", mm(162), mm(229)), // 6.3 x 9.0
    ENV_C6("EnvC6", mm(114), mm(162)), // 4.4 x 6.3
    ENV_DL("EnvDL", mm(110), mm(220)), // 4.3 x 8.6
    ENV_MONARCH("EnvMonarch", 3.875, 7.5),

    // Personal organizers

    FILOFAX_M2(2.5, 4), // 3 holes
    FILOFAX_MINI(2.625, 4.125), // 5 holes
    FILOFAX_PRODUCT(3.166, 4.75), // 6 holes
    FILOFAX_PERSONAL(3.75, 6.75), // 6 holes
    // used by "Zannaki A6 Zipper Binder" (https://www.amazon.com/gp/product/B0CWTTTRV3)
    FILOFAX_SLIMLINE(3.75, 6.75),
    FILOFAX_DESKFAX(PageSize.B5),
    FILOFAX_A5(PageSize.A5), // 6 holes
    FILOFAX_A4(PageSize.A4), //  4 holes

    FRANKLIN_PLANNER_MICRO(2.625, 4.25),
    FRANKLIN_PLANNER_POCKET(3.5, 6),
    FRANKLIN_PLANNER_COMPACT(4.25, 6.75),
    FRANKLIN_PLANNER_CLASSIC(PageSize.HALFLETTER),
    FRANKLIN_PLANNER_MONARCH(PageSize.LETTER),

    // Selected envelopes
    US_ENVELOPE_PERSONAL(3.625, 6.5),

    // local extensions

    // This paper size is used by "CAGIE A7 Small Binder Notebook" (https://www.amazon.com/gp/product/B0BQ2PGW3P)
    // That's incorrect - the included pages are much closer to a 3 x 5 index card and the "4 x 5.7 inches" refers to
    // the size of the binder, not the pages. The pages are also wide enough to make the bundled tabs useless.
    //
    // Therefore for (US-based) convenience I'm going to assume a 3 x 5 index card, but with 1/2 inch trimmed
    // from the left for the punch holes.
    CAGIE_A7(3f, 5f);

    private static final float DPI = 72f;
    private static final float MM_PER_INCH = 25.4f;

    private final Dimension dimension;
    private final String hpName;

     private PaperSize(String hpName, double width, double height) {
         this.hpName = hpName;
         this.dimension = new Dimension(DPI * width, DPI * height);
    }

    private PaperSize(double width, double height) {
        this(null, width, height);
    }

    private PaperSize(String hpName, Rectangle rectangle) {
        this.hpName = hpName;
        this.dimension = new Dimension(rectangle.getWidth(), rectangle.getHeight());
    }

    private PaperSize(Rectangle rectangle) {
        this(null, rectangle);
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Rectangle getRectangle() {
        return new RectangleReadOnly(0, 0, (float) dimension.getWidth(), (float) dimension.getHeight());
    }

    public String getHpName() {
        return hpName;
    }

    private static double mm(double mm) {
        return mm / MM_PER_INCH;
    }
}
