package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.CustomImageViewCurveLines;

import java.io.File;

/**
 * Created by haribo on 8/6/13.
 */
public class DrawingActivity extends Activity {

    private static final String TAG = "DrawingActivity";
    private CustomImageViewCurveLines customImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        customImageView = (CustomImageViewCurveLines) findViewById(R.id.curve_lines_imageview);
        Button drawTracery = (Button) findViewById(R.id.drawing_draw_tracery);
        Button cutBtn = (Button) findViewById(R.id.drawing_cut_photo);

        String imgPath = "/storage/emulated/0/DCIM/Camera/IMG_20130629_145351.jpg";
        File file = new File(imgPath);
        Bitmap bitmap = BitmapUtils.decodeFile(file, 1024, 1024, false);

        customImageView.setImageBitmap(bitmap);

        drawTracery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.drawTracery();
            }
        });
        cutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.clipArea();
            }
        });
    }
}
