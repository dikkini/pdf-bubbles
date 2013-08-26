package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.CustomViewFigures;

import java.io.File;

/**
 * Created by haribo on 8/6/13.
 */
public class FiguresActivity extends Activity {

    private static final String TAG = "CutPhoto";
    private CustomViewFigures customImageView;
    int maxX, minX, maxY, minY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figures);

        customImageView = (CustomViewFigures) findViewById(R.id.cut_photo_imageview);

        String imgPath = "/storage/sdcard0/Pictures/Instagram/IMG_20130629_145630.jpg";
        File file = new File(imgPath);
        Bitmap bitmap = BitmapUtils.decodeFile(file, 1024, 1024, false);

        customImageView.setImageBitmap(bitmap);
    }
}
