package com.magic.pdf;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.lowagie.text.Document;

import java.util.List;

/**
 * Created by haribo on 6/21/13.
 */
public class BuildAsyncPDF extends AsyncTask<Void, Void, String> {

    private Context context;

    private List<String> listImages;
    private ProgressDialog progressDialog;
    private String coverImage;
    private String shortStory;

    BuildAsyncPDF(Context context, List<String> imagePaths, String coverImage, String shortStory) {
        this.listImages = imagePaths;
        this.context = context;
        this.coverImage = coverImage;
        this.shortStory = shortStory;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Создание PDF документа. Подождите пожалуйста...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.hide();
    }
}