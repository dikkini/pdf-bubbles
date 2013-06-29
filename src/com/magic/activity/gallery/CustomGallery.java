package com.magic.activity.gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.magic.R;
import com.magic.pdf.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomGallery extends Activity {
    private static final String TAG = "CustomGallery";

    public GridView imageGrid;
    public ImageAdapter imageAdapter;
    private Cursor imageCursor;

    public ArrayList<CustomGalleryImageItemModel> images = new ArrayList<>();

    private BuildGallery buildGallery;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customgallery_main);

        imageAdapter = new ImageAdapter();

        images.clear();
        final String[] columns = {MediaStore.Images.Thumbnails._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        imageCursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                null, null, orderBy);
        if (imageCursor != null) {
            int image_column_index = imageCursor
                    .getColumnIndex(MediaStore.Images.Media._ID);
            int count = imageCursor.getCount();
            buildGallery = new BuildGallery(count, image_column_index);
            buildGallery.execute();
        }

        imageGrid = (GridView) findViewById(R.id.customgallery_images_grid);

        imageGrid.setAdapter(imageAdapter);

        final Button selectBtn = (Button) findViewById(R.id.customgallery_select_button);
        selectBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                int cnt = 0;
                String selectImages = "";
                for (CustomGalleryImageItemModel image : images) {
                    if (image.selection) {
                        cnt++;
                        selectImages = selectImages
                                + image.id + ",";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                } else {
                    selectImages = selectImages.substring(0, selectImages.lastIndexOf(","));

                    Intent i = new Intent();
                    i.putExtra(Constants.PDF_PHOTOS, selectImages);
                    setResult(Constants.PDF_WIZARD_GET_PHOTOS, i);
                    finish();
/*                    BuildPDF buildPDF = new BuildPDF(imagePaths);
                    buildPDF.execute();*/
                }

            }
        });
    }

    private class BuildGallery extends AsyncTask<Void, Void, String> {
        private int image_column_index, count, halfCount;

        BuildGallery(int count, int image_column_index) {
            this.count = count;
            this.image_column_index = image_column_index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // TODO исправить этот ночной бред и написать нормальный алгоритм!
            if (count > 31) {
                halfCount = 30;
            }
            for (int i = 0; i < halfCount; i++) {
                imageCursor.moveToPosition(i);
                int id = imageCursor.getInt(image_column_index);
                CustomGalleryImageItemModel imageItem = new CustomGalleryImageItemModel();
                imageItem.id = id;
                imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
                        getApplicationContext().getContentResolver(), id,
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
                images.add(imageItem);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                for (int i = halfCount; i < count; i++) {
                    imageCursor.moveToPosition(i);
                    int id = imageCursor.getInt(image_column_index);
                    CustomGalleryImageItemModel imageItem = new CustomGalleryImageItemModel();
                    imageItem.id = id;
                    imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
                            getApplicationContext().getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    images.add(imageItem);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                Log.e(TAG, "Runtime Exception while loading images to adapter");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageCursor != null) {
            imageCursor.close();
        }
        if (buildGallery != null && buildGallery.getStatus().equals(AsyncTask.Status.RUNNING)) {
            buildGallery.cancel(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (buildGallery != null && buildGallery.getStatus().equals(AsyncTask.Status.RUNNING)) {
            buildGallery.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (imageCursor != null) {
            imageCursor.close();
        }
        if (buildGallery != null && buildGallery.getStatus().equals(AsyncTask.Status.RUNNING)) {
            buildGallery.cancel(true);
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CustomGalleryViewHolder holder;
            if (convertView == null) {
                holder = new CustomGalleryViewHolder();
                convertView = mInflater.inflate(R.layout.customgallery_item_layout, null);
                assert convertView != null;
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.customgallery_item_image);
                holder.checkbox = (CheckBox) convertView
                        .findViewById(R.id.customgallery_item_checkbox);

                convertView.setTag(holder);
            } else {
                holder = (CustomGalleryViewHolder) convertView.getTag();
            }
            CustomGalleryImageItemModel item = images.get(position);
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (images.get(id).selection) {
                        cb.setChecked(false);
                        images.get(id).selection = false;
                    } else {
                        cb.setChecked(true);
                        images.get(id).selection = true;
                    }
                }
            });
            holder.imageview.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    if (buildGallery != null && buildGallery.getStatus().equals(AsyncTask.Status.RUNNING)) {
                        buildGallery.cancel(true);
                    }
                    int id = v.getId();
                    CustomGalleryImageItemModel item = images.get(id);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    final String[] columns = {MediaStore.Images.Media.DATA};
                    imageCursor = managedQuery(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                            MediaStore.Images.Media._ID + " = " + item.id, null, MediaStore.Images.Media._ID);
                    if (imageCursor != null && imageCursor.getCount() > 0) {
                        imageCursor.moveToPosition(0);
                        String path = imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        File file = new File(path);
                        intent.setDataAndType(
                                Uri.fromFile(file),
                                "image/*");
                        startActivityForResult(intent, Constants.VIEW_IMAGE_STDINTENT_RETURN_RESULT);
                    }
                }
            });
            holder.imageview.setImageBitmap(item.img);
            holder.checkbox.setChecked(item.selection);
            return convertView;
        }
    }

    class CustomGalleryViewHolder {
        ImageView imageview;
        CheckBox checkbox;
    }

    private class BuildPDF extends AsyncTask<Void, Void, String> {
        private List<String> listImages;
        private ProgressDialog progressDialog;

        BuildPDF(List<String> imagePaths) {
            listImages = imagePaths;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CustomGallery.this);
            progressDialog.setMessage("Создание PDF документа. Подождите пожалуйста...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            createPDF(listImages);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
        }
    }

    public void createPDF(List<String> imagePaths) {
        Document doc = new Document();


        try {
            String path = Environment.getExternalStorageDirectory() + "/LookMyWay/";

            File dir = new File(path);

            Log.d(TAG, "PDF Path: " + path);

            File file = new File(dir, "sample.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();


            Paragraph p1 = new Paragraph("Hi! I am generating my first PDF using DroidText");
            Font paraFont = new Font(Font.COURIER);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

            Paragraph p2 = new Paragraph("This is an example of a simple paragraph");
            Font paraFont2 = new Font(Font.COURIER, 14.0f, Color.GREEN);
            p2.setAlignment(Paragraph.ALIGN_CENTER);
            p2.setFont(paraFont2);

            doc.add(p2);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.custom_info_bubble);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);

            //add image to document
            doc.add(myImg);

            for (String image : imagePaths) {
                Image img = Image.getInstance(image);
                img.scaleToFit(512, 512);
                doc.add(img);
            }

            //set footer
            Phrase footerText = new Phrase("This is an example of a footer");
            HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
            doc.setFooter(pdfFooter);


        } catch (DocumentException de) {
            Log.e(TAG, "DocumentException:" + de);
        } catch (IOException e) {
            Log.e(TAG, "ioException:" + e);
        } finally {
            doc.close();
        }
    }
}