package com.magic.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.magic.models.FigurePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haribo on 8/7/13.
 */
public class CustomViewFigures extends ImageView {

    private static final String TAG = "CustomViewFigures";

    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Paint mPathPaint;
    private Paint mPointsPaint;
    private Path mPath;
    private List<FigurePoint> pointList;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    // режимы дейстий
    private static final int NONE = 0;
    private static final int MOVING = 1;
    private static final int NOT_MOVING = 2;

    private static int MODE = NONE;

    private Integer movingPointId;
    private float movingX, movingY, prevMovingX, prevMovingY;

    public CustomViewFigures(Context context) {
        super(context);
    }

    public CustomViewFigures(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(false);
        mBitmapPaint.setDither(true);

        mPointsPaint = new Paint();
        mPointsPaint.setColor(Color.RED);
        mPointsPaint.setStyle(Paint.Style.FILL);

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);
        mPathPaint.setColor(Color.RED);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(8);
    }

    public CustomViewFigures(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        drawTriangle(canvas);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        mBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO ограничить выезд за границы экрана

        int positionX = (int) event.getRawX();
        int positionY = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                float deltaX;
                float deltaY;
                for (FigurePoint point : pointList) {
                    deltaX = positionX - point.getX();
                    deltaY = positionY - point.getY();
                    if (deltaX < 40 && (deltaX > -40 && deltaY > -40)) {
                        movingPointId = point.getId();
                        movingX = point.getX();
                        movingY = point.getY();

                        MODE = MOVING;
                    }
                }

                prevMovingX = positionX;
                prevMovingY = positionY;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (MODE == MOVING) {
                    float deltaX = positionX - prevMovingX;
                    float deltaY = positionY - prevMovingY;
                    movingX = movingX + deltaX;
                    movingY = movingY + deltaY;
                    prevMovingX = positionX;
                    prevMovingY = positionY;

                    for (FigurePoint point : pointList) {
                        if (point.getId().equals(movingPointId)) {
                            point.setX(movingX);
                            point.setY(movingY);
                        }
                    }

                    invalidate();
                }
                break;
            }

            case MotionEvent.ACTION_UP:
                break;

        }
        return true;
    }

    public void drawTriangle(Canvas canvas) {
        mPath = new Path();

        mPath.moveTo(pointList.get(0).getX(), pointList.get(0).getY());
        for (FigurePoint point : pointList) {
            mPath.lineTo(point.getX(), point.getY());
            mPath.lineTo(point.getX(), point.getY());
            mPath.lineTo(point.getX(), point.getY());

            canvas.drawCircle(point.getX(), point.getY(), 20, mPointsPaint);
        }

        mPath.close();

        canvas.drawPath(mPath, mPathPaint);
    }

    public void initTriangle() {
        pointList = new ArrayList<>();
        pointList.add(new FigurePoint(1, 100, 100));
        pointList.add(new FigurePoint(2, 100, 500));
        pointList.add(new FigurePoint(3, 400, 500));

        invalidate();
    }

    public void initSquare() {
        pointList = new ArrayList<>();
        pointList.add(new FigurePoint(1, 100, 100));
        pointList.add(new FigurePoint(2, 500, 100));
        pointList.add(new FigurePoint(3, 500, 500));
        pointList.add(new FigurePoint(4, 100, 500));

        invalidate();
    }

    public void initCircle() {
        // TODO circle
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

        Paint p = new Paint();
        p.setColor(Color.BLACK);

        canvas.drawPaint(p);
        canvas.clipPath(mPath);

        // change the parameters accordin to your needs.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, rect, rect, paint);
        mBitmap = output;
        invalidate();
    }
}
