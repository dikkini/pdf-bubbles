package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.CustomImageView;
import com.magic.views.CustomImageViewLastic;

import java.io.File;
import java.util.List;

/**
 * Created by haribo on 8/6/13.
 */
public class LastikActivity extends Activity {

    private static final String TAG = "CutPhoto";
    private CustomImageViewLastic customImageView;
    int maxX, minX, maxY, minY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lastic);

        customImageView = (CustomImageViewLastic) findViewById(R.id.cut_photo_imageview);

        String imgPath = "/storage/sdcard0/Pictures/Instagram/IMG_20130629_145630.jpg";
        File file = new File(imgPath);
        Bitmap bitmap = BitmapUtils.decodeFile(file, 1024, 1024, false);

        customImageView.setImageBitmap(bitmap);
    }
}
