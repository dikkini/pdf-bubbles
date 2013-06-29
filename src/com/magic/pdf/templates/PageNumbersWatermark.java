package com.magic.pdf.templates;

/**
 * Created with IntelliJ IDEA.
 * User: haribo
 * Date: 6/24/13
 * Time: 9:56 PM
 */

import android.graphics.Bitmap;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import harmony.java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PageNumbersWatermark extends PdfPageEventHelper {
    /**
     * An Image that goes in the header.
     */
    public Image headerImage;

    public PageNumbersWatermark(Bitmap logo) {
        ByteArrayOutputStream portraitOutStream = new ByteArrayOutputStream();
        logo.compress(Bitmap.CompressFormat.JPEG, 100, portraitOutStream);
        try {
            headerImage = Image.getInstance(portraitOutStream.toByteArray());
        } catch (BadElementException | IOException e) {
            e.printStackTrace();
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfContentByte cb = writer.getDirectContent();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb.beginText();
            cb.setColorFill(Color.red);
            cb.setFontAndSize(bf, 12);
            cb.showTextAligned(Element.ALIGN_CENTER, "LOOK MY WAY", document.getPageSize().getWidth() / 2,
                    document.getPageSize().getHeight() / 2, 45);
            cb.endText();
            cb.addImage(headerImage, headerImage.getWidth(), 0, 0, headerImage.getHeight(), 440, 80);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}
