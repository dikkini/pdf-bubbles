package com.magic.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.magic.BitmapUtils;
import com.magic.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haribo on 8/7/13.
 */
public class CustomImageViewCurveLines extends ImageView {

    private static final String TAG = "CustomImageViewCurveLines";

    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private List<Point> points = new ArrayList<>();

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    public CustomImageViewCurveLines(Context context) {
        super(context);
    }

    public CustomImageViewCurveLines(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);
    }

    public CustomImageViewCurveLines(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xFFAAAAAA);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        points.add(new Point((int) x, (int) y));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        mBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    public void clipArea() {
        Bitmap output = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Path polyPath = new Path();
        polyPath.setFillType(Path.FillType.EVEN_ODD);

        boolean first = true;
        for(int i = 0; i < points.size(); i += 2){
            Point point = points.get(i);
            if(first){
                first = false;
                polyPath.moveTo(point.x, point.y);
            }

            else if(i < points.size() - 1){
                Point next = points.get(i + 1);
                polyPath.quadTo(point.x, point.y, next.x, next.y);
            }
            else{
                polyPath.lineTo(point.x, point.y);
            }
        }

        polyPath.lineTo(points.get(0).x, points.get(0).y);
        polyPath.close();

        Paint p = new Paint();
        p.setColor(Color.BLACK);

        canvas.drawPaint(p);
        canvas.clipPath(polyPath);

        // change the parameters accordin to your needs.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, rect, rect, paint);
        mBitmap = output;
        invalidate();
    }

    public void drawTracery() {

        Path polyPath = new Path();
        polyPath.setFillType(Path.FillType.EVEN_ODD);

        for (Point point : points) {
            polyPath.lineTo(point.x, point.y);
        }
        polyPath.lineTo(points.get(0).x, points.get(0).y);
        polyPath.close();

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        mCanvas.drawPath(polyPath, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mCanvas.drawBitmap(mBitmap, 0, 0, paint);

        invalidate();
    }
}
