package com.magic.pdf.templates;

import android.graphics.Bitmap;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by haribo on 6/21/13.
 */
public class StdTemplate extends PdfPageEventHelper {

    public static void createPDF(Bitmap logo, Bitmap portrait, Bitmap cover, List<String> waysImages) {
        Document doc = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(doc,
                    new FileOutputStream(android.os.Environment.getExternalStorageDirectory() + java.io.File.separator
                            + "/LookMyWay/" + java.io.File.separator + "text.pdf"));

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

            writer.setPageEvent(new PageNumbersWatermark(logo));

            // open the document
            doc.open();

            // we grab the ContentByte and do some stuff with it
            PdfContentByte cb = writer.getDirectContent();

            cb.setFontAndSize(bf, 12);
            cb.beginText();

            // we show some text starting on some absolute position with a given
            // alignment
            String bd = "18.03.2008";
            String fio = "Karapetov Artur Grigor'evich";
            String nameWay = "Moscow - Rome";
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, bd, 250, 790, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, fio, 250, 770, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, nameWay, 250, 750, 0);

            cb.endText();

            HeaderFooter headerFooter = new HeaderFooter(new Phrase(""), true);
            headerFooter.setAlignment(HeaderFooter.ALIGN_CENTER);
            doc.setFooter(headerFooter);

            // author portrait
            ByteArrayOutputStream portraitOutStream = new ByteArrayOutputStream();
            portrait.compress(Bitmap.CompressFormat.JPEG, 100, portraitOutStream);
            Image portraitImage = Image.getInstance(portraitOutStream.toByteArray());
            portraitImage.setAlignment(Image.ALIGN_LEFT);
            doc.add(portraitImage);

            // story text
            TextField story = new TextField(writer, new Rectangle(550, 600, 35, 35), "Something about me");
            story.setText("This famous place was a Pulpit Cross, from which sermons might be preached in the open air. Several London churches had their open-air pulpits: notably St. Michael's, Cornhill; St. Mary's Spital, without Bishopsgate—at this Cross a sermon was preached every Easter to the Lord Mayor and aldermen. When Paul's Cross was erected is not known: it probably stood on the site of some scaffold or steps, from which the people were anciently harangued, for this was the place of the folk-mote, or meeting of the people. Here were read aloud, and proclaimed, the King's Laws and Orders: here the people were informed of War and Peace: here Papal Bulls were read. There was a cross standing here in the year 1256—very likely it was already ancient. In the year 1387 it was ruinous and{94} had to be repaired. It was again repaired or rebuilt in 1480. Paul's Cross played a very important part in the Reformation. Here the 'Rood' of Bexley, which was a crucifix where the eyes and lips were made to move and the people were taught that it was miraculous, was exposed and broken to pieces: here the famous images of Walsingham and Ipswich, the object of so many pilgrimages, were brought to be broken to pieces before the eyes of the people. Here Latimer preached, a man of the people who could speak to them in a way to make them understand. Had it not been for the preaching of Latimer and others like him in plain language, the Reformation would have been an attempt, and probably a failure, to enforce upon the people the opinions of certain scholars. Paul's Cross did not perish in the Fire: it was taken down in the year 1643, or thereabouts, in order to be rebuilt; but this was not done, and when the Fire destroyed the Cathedral Paul's Cross was forgotten. Its site may be seen in the churchyard at the N.E. corner of the choir, marked by a flat stone, but it must be remembered that the old church was wider but farther south.  On the south side of Paul's Churchyard we pass in succession the beautiful Chapter House: the Church of St. Gregory and the Deanery. Close to the western gate are residences for the Canons, south of the enclosure are the Cathedral Brewhouse and Bakehouse. Such are some of the buildings in Paul's Churchyard. The Cathedral establishment supported a great army of priests and people. For many of them, perhaps for most, there were residences of some kind either within the enclosure or close beside it. Thus the priests, including Bishop, Dean, Archdeacons and Canons, a hundred and thirty in number: then there were the{95} inferior officers: yet persons of consideration and authority, such as Sacrist, Almoner, Bookbinder, Chief Brewer, Chief Baker, with all their servants: scribes, messengers, bookbinders, illuminators and copyists: singing-men and choir boys, and women to keep the church clean. When we add that the Brewer had to provide 200 gallons of beer a day, it is obvious that there must have been a good many people belonging to the Cathedral who lived in the enclosure called the Churchyard.");
            story.setAlignment(Element.ALIGN_BASELINE);
            story.setOptions(TextField.MULTILINE | TextField.REQUIRED);
            PdfFormField fistoryField = story.getTextField();
            writer.addAnnotation(fistoryField);

            doc.newPage();

            ByteArrayOutputStream coverOutStream = new ByteArrayOutputStream();
            cover.compress(Bitmap.CompressFormat.JPEG, 100, coverOutStream);
            Image coverImage = Image.getInstance(coverOutStream.toByteArray());
            coverImage.setAlignment(Image.ALIGN_LEFT);

            Paragraph paragraph;

            doc.add(coverImage);
            paragraph = new Paragraph("W0W! It's dangerous...");
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(paragraph);
            doc.newPage();

            Image wayImage;
            int count = 0;
            for (String image : waysImages) {
                count++;
                wayImage = Image.getInstance(image);
                wayImage.scaleToFit(512, 512);
                wayImage.setAlignment(Image.ALIGN_CENTER);
                doc.add(wayImage);
                paragraph = new Paragraph("Image Number: " + count);
                paragraph.setAlignment(Paragraph.ALIGN_CENTER);
                doc.add(paragraph);
                doc.newPage();
            }

            // close document
            doc.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
