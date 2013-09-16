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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.magic.models.FigurePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haribo on 8/7/13.
 */
public class CustomViewFigures extends ImageView {

    private static final String TAG = "CustomViewFigures";
    private DisplayMetrics displayMetrics = new DisplayMetrics();

    private Bitmap mBitmap;
    private Bitmap srcBitmap;
    private Paint mBitmapPaint;
    private Paint mPathPaint;
    private Paint mPointsPaint;
    private Path mPath;
    private List<FigurePoint> pointList = new ArrayList<>();

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private final static float stdDist = 10f;

    // режимы дейстий
    private static final int NONE = 0;
    private static final int POLYGON_POINT_MOVING = 1;
    private static final int POLYGON_DRAWING = 2;
    private static final int MOVING_STATIC_FIGURE = 3;
    private static final int MOVING_STATIC_POINT = 4;
    private static final int RESIZING_FIGURE = 5;
    private static final int MOVING_CIRCLE = 6;

    private static int ACTION = NONE;

    // фигуры
    private static final String NON = "";
    private static final String STATIC = "STATIC";
    private static final String CIRCLE = "CIRCLE";
    private static final String POLYGON = "POLYGON";
    // часть полигона. фигура при отрисовки пользователем полигона
    private static final String PART_OF_POLYGON = "PART_OF_POLYGON";

    private static String FIGURE = NON;

    private Integer movingPointId, plogyPointsCount;
    private float movingX, movingY, prevMovingX, prevMovingY;
    private float movingDist;
    private FigurePoint circlePoint;
    private int circleRadius = 100, minimalRadius = 10;
    private int minimumX = 0, minimumY = 600;
    private int scaleCircle = 1;
    private int displayWidth, displayHeigth;

    private float xBase;
    private float yBase;

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

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(displayMetrics);
        scaleCircle = displayMetrics.densityDpi;
        displayWidth = display.getWidth();
        displayHeigth = display.getHeight();
    }

    public CustomViewFigures(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        switch (FIGURE) {
            case STATIC:
            case POLYGON:
                drawFigure(canvas);
                break;
            case PART_OF_POLYGON:
                drawPolyLine(canvas);
                break;
            case CIRCLE:
                drawCircle(canvas);
                break;
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        srcBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO ограничить выезд за границы экрана

        int positionX = (int) event.getRawX();
        int positionY = (int) event.getRawY();
        float deltaX = positionX - prevMovingX;
        float deltaY = positionY - prevMovingY;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (FIGURE.equals(POLYGON) && ACTION == POLYGON_POINT_MOVING) {
                    for (FigurePoint point : pointList) {
                        deltaX = positionX - point.getX();
                        deltaY = positionY - point.getY();
                        // TODO переделать механизим уточнения области касания экрана для точечного попадания в точку полигона
                        if (deltaX < 20 && (deltaX > -20 && deltaY > -20)) {
                            movingPointId = point.getId();
                            movingX = point.getX();
                            movingY = point.getY();
                        }
                    }
                } else if (FIGURE.equals(STATIC)) {
                    ACTION = MOVING_STATIC_FIGURE;
                } else if (FIGURE.equals(CIRCLE)) {
                    ACTION = MOVING_CIRCLE;
                }

                prevMovingX = positionX;
                prevMovingY = positionY;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (ACTION == POLYGON_POINT_MOVING) {
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
                } else if (ACTION == MOVING_STATIC_FIGURE) {
                    deltaX = positionX - prevMovingX;
                    deltaY = positionY - prevMovingY;
                    prevMovingX = positionX;
                    prevMovingY = positionY;

                    for (FigurePoint point : pointList) {
                        point.setX(point.getX() + deltaX);
                        point.setY(point.getY() + deltaY);
                    }

                    invalidate();
                } else if (ACTION == RESIZING_FIGURE && FIGURE.equals(STATIC)) {
                    float newMovingDist = spacing(event);
                    if (newMovingDist > stdDist) {
                        for (FigurePoint point : pointList) {
                            float scale = (newMovingDist / movingDist);
                            if (scale > 1) {
                                scale = 1.037f;
                            } else if (scale < 1) {
                                scale = 0.98f;
                            }

                            int nX = (int) (pointList.get(0).getX() + (point.getX() - pointList.get(0).getX()) * scale);
                            int nY = (int) (pointList.get(0).getY() + (point.getY() - pointList.get(0).getY()) * scale);

                            point.setX(nX);
                            point.setY(nY);
                        }
                        invalidate();
                    }
                } else if (ACTION == RESIZING_FIGURE && FIGURE.equals(CIRCLE)) {
                    float newMovingDist = spacing(event);
                    if (newMovingDist > stdDist) {
                        float scale = (newMovingDist / movingDist);
                        if (scale > 1) {
                            scale = 1.037f;
                        } else if (scale < 1) {
                            scale = 0.98f;
                        }
                        
                        float nRadius = circleRadius * scale;
                        if (nRadius < minimalRadius) {
                            nRadius = minimalRadius;
                        // TODO вычислять максимальный размер исходя из размеров экрана
                        } else if (nRadius > 600) {
                            nRadius = 600;
                        }
                        circleRadius = (int) nRadius;
                        invalidate();
                    }
                } else if (ACTION == MOVING_CIRCLE) {
                    circlePoint.setX(positionX);
                    circlePoint.setY(positionY);

                    invalidate();
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                movingDist = spacing(event);
                if (movingDist > stdDist) {
                    ACTION = RESIZING_FIGURE;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (ACTION == POLYGON_DRAWING) {
                    FigurePoint touchPoint = new FigurePoint(plogyPointsCount++, positionX, positionY);
                    pointList.add(touchPoint);

                    invalidate();
                }
                break;

        }
        return true;
    }

    /**
     * Отрисовка фигуры
     */
    private void drawFigure(Canvas canvas) {
        mPath = new Path();
        mPath.moveTo(pointList.get(0).getX(), pointList.get(0).getY());
        for (FigurePoint point : pointList) {
            mPath.lineTo(point.getX(), point.getY());

            canvas.drawCircle(point.getX(), point.getY(), 20, mPointsPaint);
        }
        mPath.close();

        canvas.drawPath(mPath, mPathPaint);
    }

    /**
     * Отрисовка линии в режиме построения полигона
     */
    private void drawPolyLine(Canvas canvas) {
        for (int i = 0; i < pointList.size(); i++) {
            canvas.drawCircle(pointList.get(i).getX(), pointList.get(i).getY(), 10, mPointsPaint);
            if (i+1 < pointList.size()) {
                canvas.drawLine(pointList.get(i).getX(), pointList.get(i).getY(),
                        pointList.get(i+1).getX(), pointList.get(i+1).getY(), mPathPaint);
            }
        }
    }

    /**
     * Отрисовка окружности
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(circlePoint.getX(), circlePoint.getY(), circleRadius, mPathPaint);
    }

    /**
     * Инициализация треугольника (статическая фигура)
     */
    public void initTriangle() {
        FIGURE = STATIC;

        pointList = new ArrayList<>();
        pointList.add(new FigurePoint(1, 100, 100));
        pointList.add(new FigurePoint(2, 100, 500));
        pointList.add(new FigurePoint(3, 400, 500));

        invalidate();
    }

    /**
     * Инициализация квадрата (статическая фигура)
     */
    public void initSquare() {
        FIGURE = STATIC;

        pointList = new ArrayList<>();
        pointList.add(new FigurePoint(1, 100, 100));
        pointList.add(new FigurePoint(2, 500, 100));
        pointList.add(new FigurePoint(3, 500, 500));
        pointList.add(new FigurePoint(4, 100, 500));

        invalidate();
    }

    /**
     * Инициализация круга (статическая фигура)
     */
    public void initCircle() {
        FIGURE = CIRCLE;

        circlePoint = new FigurePoint(300, 300);
        invalidate();
    }

    /**
     * Инициализация полигона (динамическая фигура с возможностью сдвига точек)
     */
    public void initPolygon(boolean draw) {
        if (draw) {
            FIGURE = POLYGON;
            ACTION = POLYGON_POINT_MOVING;
            // очишение от точек
            clearBitmap();
            invalidate();
        } else {
            FIGURE = PART_OF_POLYGON;
            pointList = new ArrayList<>();
            plogyPointsCount = 0;
            ACTION = POLYGON_DRAWING;
            Toast.makeText(getContext(), "Начните рисовать точки полигона", Toast.LENGTH_LONG).show();
            invalidate();
        }
    }

    /**
     * Метод вырезания из канвы области в виде Path
     */
    public void clipArea() {
        Bitmap output = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        if (FIGURE.equals(CIRCLE)) {
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(circlePoint.getX(), circlePoint.getY(), circleRadius, paint);
        } else {
            canvas.drawColor(Color.BLACK);
            canvas.clipPath(mPath);
        }

        // change the parameters accordin to your needs.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, rect, rect, paint);
        mBitmap = output;

        clearFigure();
    }

    /**
     * Убираем фигуру с экрана
     */
    private void clearFigure() {
        pointList = null;
        mPath = null;
        circlePoint = null;
        // reset circle radius to minimal radius value
        circleRadius = minimalRadius;
        FIGURE = NON;
        ACTION = NONE;
        invalidate();
    }

    /**
     * Убираем все с экрана, очищаем изображение
     */
    private void clearBitmap() {
        mBitmap = srcBitmap;
        invalidate();
    }

    private float spacing(MotionEvent event) {
        float x = 0;
        float y = 0;
        try {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.d(TAG, "pointerIndex exception");
        }
        return FloatMath.sqrt(x * x + y * y);
    }
}
