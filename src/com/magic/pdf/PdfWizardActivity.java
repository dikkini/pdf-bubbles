package com.magic.pdf;

/**
 * Created by haribo on 6/17/13.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.activity.gallery.CustomGallery;
import com.magic.pdf.templates.StdTemplate;
import com.magic.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for choosy pdf cover
 */
public class PdfWizardActivity extends FragmentActivity {

    private static final String TAG = "PdfWizardActivity";
    private String coverPath;
    private String shortStory;
    private List<String> photoPaths;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_wizard_set_cover);

        Button chooseCoverBtn = (Button) findViewById(R.id.pdf_wizard_get_cover_choose_cover_button);
        Button shortStoryBtn = (Button) findViewById(R.id.pdf_wizard_set_short_story_button);
        Button getPhotosBtn = (Button) findViewById(R.id.pdf_wizard_get_photos_button);
        Button createPDFBtn = (Button) findViewById(R.id.pdf_wizard_create_pdf_button);

        chooseCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PDF_WIZARD_GET_COVER_IMAGE);
            }
        });

        shortStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PdfWizardShortStory.class);
                startActivityForResult(intent, Constants.PDF_WIZARD_GET_SHORT_STORY);
            }
        });

        getPhotosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(getApplicationContext(), CustomGallery.class);
                startActivityForResult(galleryIntent, Constants.PDF_WIZARD_GET_PHOTOS);
            }
        });

        createPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap portrait = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.portrait);
                Bitmap scaledPortrait = BitmapUtils.getScaledBitmap(portrait, 200, 200);

                Bitmap cover = BitmapFactory.decodeFile(coverPath);
                Bitmap scaledCover = BitmapUtils.getScaledBitmap(cover, 512, 512);

                Drawable logoDrawable = getResources().getDrawable(R.drawable.std_template_logo);
                Bitmap logoBitmap = BitmapUtils.drawableToBitmap(logoDrawable);

                StdTemplate.createPDF(logoBitmap, scaledPortrait, scaledCover, photoPaths);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constants.PDF_WIZARD_GET_COVER_IMAGE:
                if (data == null) {
                    break;
                }
                Uri uri = data.getData();
                coverPath = Utils.getPathFromURI(getApplicationContext(), uri);
                Log.d(TAG, "imagepath: " + coverPath);
                break;
            case Constants.PDF_WIZARD_GET_SHORT_STORY:
                Log.d(TAG, "imagepath: " + coverPath);
                assert data != null;
                Bundle shortStoryExtras = data.getExtras();
                assert shortStoryExtras != null;
                shortStory = shortStoryExtras.getString(Constants.PDF_SHORT_STORY);
                Log.d(TAG, "shortstory: " + shortStory);
                break;
            case Constants.PDF_WIZARD_GET_PHOTOS:
                Log.d(TAG, "imagepath: " + coverPath + " shortstory: " + shortStory);
                Bundle photosExtras = data.getExtras();
                assert photosExtras != null;
                String photosIds = photosExtras.getString(Constants.PDF_PHOTOS);
                photoPaths = parseImages(photosIds);
                break;
        }
    }

    public List<String> parseImages(String ids) {
        List<String> imagesPath = new ArrayList<>();
        String[] arrIds = ids.split(",");
        for (String item : arrIds) {
            Long media_id = Long.parseLong(item);

            final String[] columns = {MediaStore.Images.Media.DATA};
            final String orderBy = MediaStore.Images.Media._ID;
            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    MediaStore.Images.Media._ID + " = " + media_id + "", null,
                    orderBy);
            int count = imagecursor.getCount();
            for (int i = 0; i < count; i++) {
                imagecursor.moveToPosition(i);
                int dataColumnIndex = imagecursor
                        .getColumnIndex(MediaStore.Images.Media.DATA);
                String path = imagecursor.getString(dataColumnIndex);
                imagesPath.add(path);
            }
        }
        return imagesPath;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}