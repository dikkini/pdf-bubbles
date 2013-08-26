package com.magic.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.magic.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haribo on 8/7/13.
 */
public class CustomImageView extends ImageView {

    private static final String TAG = "CustomImageView";
    private Bitmap bitmap;
    private Bitmap sourceBitmap;
    private List<Point> pointsList = new ArrayList<>();
    private static Paint paint = new Paint();

    public Bitmap getBitmap() {
        return bitmap;
    }

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile("/storage/sdcard0/Pictures/Instagram/IMG_20130629_145630.jpg");
        }
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    @Override
    public void setImageBitmap(Bitmap bmp) {
        // первый вызов этого метода засетить исходное изображение
        if (sourceBitmap == null) {
            sourceBitmap = bmp;
        }

        bitmap = bmp;
        invalidate();
    }

    public void clearBitmap(int maxX, int minX, int maxY, int minY) {
        if (sourceBitmap != null) {
            if (maxX == 0 && minX == 0 && maxY == 0 && minY == 0) {
                bitmap = sourceBitmap;
            } else {
                bitmap = Bitmap.createBitmap(sourceBitmap, minX, minY, maxX-minX, maxY-minY);
            }
        }
        pointsList.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int positionX = (int) event.getRawX();
        int positionY = (int) event.getRawY() - 80;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    // Add current touch position to the list of points
                    pointsList.add(new Point(positionX, positionY));
                    Log.d(TAG, " positionX: " + positionX + " positionY: " + positionY);

                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setStrokeWidth(3);

                    // Iterate on the list
                    for (int i = 0; i < pointsList.size(); i++) {
                        Point current = pointsList.get(i);

                        // Draw points
                        canvas.drawCircle(current.x, current.y, 10, paint);
                        Log.d(TAG, " startCurrentX: " + current.x + " startCurrentY: " + current.y);

                        // Draw line with next point (if it exists)
                        if (i + 1 < pointsList.size()) {
                            Point next = pointsList.get(i + 1);
                            canvas.drawLine(current.x, current.y, next.x, next.y, paint);
                            Log.d(TAG, " currentX: " + current.x + " currentY: " + current.y + " nextX: " + next.x + " nextY: " + next.y);
                        }
                    }
                }

                setImageBitmap(bitmap);
                break;
        }
        return true;
    }

    public List<Point> getPointsList() {
        return pointsList;
    }
}
