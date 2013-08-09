package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.magic.R;
import com.magic.views.CustomImageView;

import java.util.List;

/**
 * Created by haribo on 8/6/13.
 */
public class CutPhotoActivity extends Activity {

    private static final String TAG = "CutPhoto";
    private CustomImageView customImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_photo);

        customImageView = (CustomImageView) findViewById(R.id.cut_photo_imageview);
        Button button = (Button) findViewById(R.id.cut_photo_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Point> pointsList = customImageView.getPointsList();
                Bitmap bitmap = customImageView.getBitmap();

                int minX, minY, maxX = 0, maxY = 0;
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

                Bitmap newBitmap = Bitmap.createBitmap(bitmap, minX, minY, maxX-minX, maxY-minY);
                Canvas canvas = new Canvas(newBitmap);

                canvas.drawBitmap(newBitmap, 0, 0, paint);

                customImageView.setImageBitmap(newBitmap);
            }
        });
    }
}
