package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figures);

        customImageView = (CustomViewFigures) findViewById(R.id.cut_photo_imageview);

        Button clearImageBtn = (Button) findViewById(R.id.figures_clear);
        clearImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.setImageBitmap(getBitmap());
            }
        });

        Button polygonInitBtn = (Button) findViewById(R.id.figures_polygon);
        polygonInitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.initPolygon(false);
            }
        });
        Button polygonFinishBtn = (Button) findViewById(R.id.figures_polygon_finish);
        polygonFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.initPolygon(true);
            }
        });

        Button squareInitBtn = (Button) findViewById(R.id.figures_square);
        squareInitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.initSquare();
            }
        });

        Button circleInitBtn = (Button) findViewById(R.id.figures_circle);
        circleInitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.initCircle();
            }
        });

        Button triangleInitBtn = (Button) findViewById(R.id.figures_triangle);
        triangleInitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.initTriangle();
            }
        });

        Button clipAreBtn = (Button) findViewById(R.id.figures_clip_area);
        clipAreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.clipArea();
            }
        });

        customImageView.setImageBitmap(getBitmap());
    }

    private Bitmap getBitmap() {
        String imgPath = "/storage/emulated/0/DCIM/Camera/IMG_20130629_145351.jpg";
        File file = new File(imgPath);
        return BitmapUtils.decodeFile(file, 1024, 1024, false);
    }
}
