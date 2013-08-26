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

import java.io.File;
import java.util.List;

/**
 * Created by haribo on 8/6/13.
 */
public class CutPhotoActivity extends Activity {

    private static final String TAG = "CutPhoto";
    private CustomImageView customImageView;
    int maxX, minX, maxY, minY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_photo);

        customImageView = (CustomImageView) findViewById(R.id.cut_photo_imageview);

        String imgPath = "/storage/sdcard0/Pictures/Instagram/IMG_20130629_145630.jpg";
        File file = new File(imgPath);
        Bitmap bitmap = BitmapUtils.decodeFile(file, 1024, 1024, false);

        customImageView.setImageBitmap(bitmap);
        Button cutPhotoBtn = (Button) findViewById(R.id.cut_photo_button);
        Button clearPhotoBtn = (Button) findViewById(R.id.clear_image_button);

        cutPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Point> pointsList = customImageView.getPointsList();
                if (pointsList.size() == 0) {
                    Toast.makeText(CutPhotoActivity.this, "tap on the screen", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = customImageView.getBitmap();

                minX = pointsList.get(0).x;
                minY = pointsList.get(0).y;
                for (Point current : pointsList) {

                    if (current.x > maxX) {
                        maxX = current.x;
                    }

                    if (current.x < minX) {
                        minX = current.x;
                    }

                    if (current.y > maxY) {
                        maxY = current.y;
                    }

                    if (current.y < minY) {
                        minY = current.y;
                    }
                }

                Paint paint = new Paint();

                Bitmap newBitmap;
                try {
                    newBitmap = Bitmap.createBitmap(bitmap, minX, minY, maxX - minX, maxY - minY);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "" + e.toString());
                    newBitmap = bitmap;
                }
                Canvas canvas = new Canvas(newBitmap);

                canvas.drawBitmap(newBitmap, 0, 0, paint);

                customImageView.setImageBitmap(newBitmap);
            }
        });

        clearPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImageView.clearBitmap(maxX, minX, maxY, minY);
                maxX = minX = maxY = minY = 0;
            }
        });
    }
}
