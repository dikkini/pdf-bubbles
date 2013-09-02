package com.magic.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
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
    private static final int POLYGON_POINT_MOVING = 1;
    private static final int MOVING_FIGURE = 2;
    private static final int RESIZING_FIGURE = 3;

    private static int ACTION = NONE;

    // фигуры
    private static final String NON = "";
    private static final String STATIC = "STATIC";
    private static final String CIRCLE = "CIRCLE";
    private static final String POLYGON = "POLYGON";

    private static String FIGURE = NON;

    private Integer movingPointId;
    private float movingX, movingY, prevMovingX, prevMovingY, deltaX, deltaY;

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

        if (pointList != null) {
            drawFigure(canvas);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        clearFigure();

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pointList == null) {
            return false;
        }

        // TODO ограничить выезд за границы экрана

        int positionX = (int) event.getRawX();
        int positionY = (int) event.getRawY();
        deltaX = positionX - prevMovingX;
        deltaY = positionY - prevMovingY;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (FIGURE.equals(POLYGON)) {
                    for (FigurePoint point : pointList) {
                        deltaX = positionX - point.getX();
                        deltaY = positionY - point.getY();
                        if (deltaX < 40 && (deltaX > -40 && deltaY > -40)) {
                            movingPointId = point.getId();
                            movingX = point.getX();
                            movingY = point.getY();

                            ACTION = POLYGON_POINT_MOVING;
                        }
                    }
                } else if (FIGURE.equals(STATIC)) {
                    ACTION = MOVING_FIGURE;
                }

                prevMovingX = positionX;
                prevMovingY = positionY;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (FIGURE.equals(POLYGON) && ACTION == POLYGON_POINT_MOVING) {
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
                } else if (ACTION == MOVING_FIGURE) {
                    deltaX = positionX - prevMovingX;
                    deltaY = positionY - prevMovingY;
                    prevMovingX = positionX;
                    prevMovingY = positionY;

                    for (FigurePoint point : pointList) {
                        point.setX(point.getX() + deltaX);
                        point.setY(point.getY() + deltaY);
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

    public void drawFigure(Canvas canvas) {
        mPath = new Path();

        mPath.moveTo(pointList.get(0).getX(), pointList.get(0).getY());
        for (FigurePoint point : pointList) {
            mPath.lineTo(point.getX(), point.getY());
            mPath.lineTo(point.getX(), point.getY());
            mPath.lineTo(point.getX(), point.getY());

            // draw points of changing polygon
//            canvas.drawCircle(point.getX(), point.getY(), 20, mPointsPaint);
        }

        mPath.close();

        canvas.drawPath(mPath, mPathPaint);
    }

    private Path getPath(List<FigurePoint> points) {
        Path p = new Path();

        p.moveTo(points.get(0).getX(), points.get(0).getY());
        for (FigurePoint point : points) {
            p.lineTo(point.getX(), point.getY());
            p.lineTo(point.getX(), point.getY());
            p.lineTo(point.getX(), point.getY());
        }
        return p;
    }

    public void initTriangle() {
        FIGURE = STATIC;

        pointList = new ArrayList<>();
        pointList.add(new FigurePoint(1, 100, 100));
        pointList.add(new FigurePoint(2, 100, 500));
        pointList.add(new FigurePoint(3, 400, 500));

        invalidate();
    }

    public void initSquare() {
        FIGURE = STATIC;

        pointList = new ArrayList<>();
        pointList.add(new FigurePoint(1, 100, 100));
        pointList.add(new FigurePoint(2, 500, 100));
        pointList.add(new FigurePoint(3, 500, 500));
        pointList.add(new FigurePoint(4, 100, 500));

        invalidate();
    }

    public void initCircle() {
        FIGURE = CIRCLE;
        // TODO circle
    }

    public void initPolygon() {
        FIGURE = POLYGON;

        pointList = new ArrayList<>();
        pointList.add(new FigurePoint(1, 100, 100));
        pointList.add(new FigurePoint(2, 500, 100));
        pointList.add(new FigurePoint(3, 500, 500));
        pointList.add(new FigurePoint(4, 100, 500));

        invalidate();
    }

    public void clipArea() {
        if (mPath == null) {
            return;
        }
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

        clearFigure();
    }

    private void clearFigure() {
        pointList = null;
        mPath = null;

        invalidate();
    }
}
