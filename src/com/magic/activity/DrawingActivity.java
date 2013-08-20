package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.CustomImageViewCurveLines;
import com.magic.views.CustomImageViewLastic;

import java.io.File;

/**
 * Created by haribo on 8/6/13.
 */
public class DrawingActivity extends Activity {

    private static final String TAG = "DrawingActivity";
    private CustomImageViewCurveLines customImageView;
    int maxX, minX, maxY, minY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        customImageView = (CustomImageViewCurveLines) findViewById(R.id.curve_lines_imageview);
        Button cutBtn = (Button) findViewById(R.id.curve_lines_cut_area);

        String imgPath = "/storage/sdcard0/Pictures/Instagram/IMG_20130629_145630.jpg";
        File file = new File(imgPath);
        Bitmap bitmap = BitmapUtils.decodeFile(file, 1024, 1024, false);

        customImageView.setImageBitmap(bitmap);

        cutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.clipPath();
            }
        });
    }
}
