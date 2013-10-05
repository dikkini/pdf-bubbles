
package com.magic.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.PictureDrawable;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.magic.R;
import com.magic.activity.BubbleSetAlphaSeekListener;
import com.magic.activity.BubbleSetColorSeekListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

// TODO GLOBAL refactor
// TODO refactor draw/remove Strokes methods
public final class BubbleView extends ImageView {
    private final static String TAG = "BubbleView";
    private final static float stdDist = 10f;
    // режимы дейстий
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int TAIL = 3;

    private static int MODE = NONE;

    private Paint drawablePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private Rect drawableRect = new Rect();
    private Paint textPaint = new Paint();
    private Paint tailPaint = new Paint();

    private Bitmap image;
    private Bitmap sourceImage;
    private Context mContext;
    private int mScreenHeight, mScreenWidth, prevY, prevX, mImageWidth, mImageHeight, mTouchSlop,
            mScaledImageWidth, mScaledImageHeight, prevTailX, prevTailY;
    private Rect mImagePosition;
    private Region mImageRegion;
    private boolean canImageMove, isOnClick;
    private float mDownX;
    private float mDownY;

    private float movingDist;

    private BitmapFactory.Options options;

    private Integer bubbleId;
    private Integer drawableId;
    private List<BubbleView> bubbles;
    private RelativeLayout container;
    private SeekBar seekColor;
    private SeekBar seekAlpha;
    private TextView textView;

    private boolean active;
    private float tailLowXPoint = 0, tailLowYPoint = 0;
    private boolean drawCircleTail = true;

    public TextView getTextView() {
        return textView;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getBubbleId() {
        return bubbleId;
    }

    public Rect getmImagePosition() {
        return mImagePosition;
    }

    public List<BubbleView> getBubbles() {
        return bubbles;
    }

    public BubbleView(Context context, ImageView imageView, Integer id, List<BubbleView> bubbles,
                      RelativeLayout container, SeekBar seekColor, SeekBar seekAlpha) {
        // TODO refactor
        super(context);
        mContext = context;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        setAdjustViewBounds(true);
        mScreenHeight = imageView.getHeight();
        mScreenWidth = imageView.getWidth();
        canImageMove = false;

//        drawablePaint.setAntiAlias(true);
//        drawablePaint.setFilterBitmap(false);
//        drawablePaint.setDither(true);

        drawableRect.setEmpty();

        if (bubbles != null) {
            this.bubbles = bubbles;
        } else {
            this.bubbles = new ArrayList<>();
        }

        this.container = container;
        this.bubbleId = id;
        this.seekAlpha = seekAlpha;
        this.seekColor = seekColor;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawableRect.set(0, 0, mImageWidth, mImageHeight);
        // рисуем рамку и пипку для управления хвостиком
        if (active) {
            drawStroke();
        }
        drawTail(canvas);

        canvas.drawBitmap(image, drawableRect, mImagePosition, drawablePaint);

        drawResizeButton(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int positionX = (int) event.getRawX();
        int positionY = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                canImageMove = true;
                prevX = positionX;
                prevY = positionY;
                // for listen on click tap
                mDownX = event.getX();
                mDownY = event.getY();
                // tail coordinates
                prevTailX = positionX;
                prevTailY = positionY;

                float deltaXDownTail = mDownX - tailLowXPoint;
                float deltaYDownTail = mDownY - tailLowYPoint;

//                Log.d(TAG, "mDownX - tailLowXPoint = " + positionX + " - " + tailLowXPoint + " = " + deltaXDownTail);
//                Log.d(TAG, "mDownY - tailLowYPoint = " + positionY + " - " + tailLowYPoint + " = " + deltaYDownTail);

                float isZoomX = mDownX - mImagePosition.right;
                float isZoomY = mDownY - mImagePosition.top;

//                Log.d(TAG, "mDownX - mImagePosition.right = " + mDownX + " - " + mImagePosition.right + " = " + isZoomX);
//                Log.d(TAG, "mDownY - mImagePosition.top = " + mDownX + " - " + mImagePosition.top + " = " + isZoomY);

                // 20 20 -20 -20 - квадрат который означает что пользователь начала передвигать хвостик
                if ((deltaXDownTail < 20 && deltaYDownTail < 20) && (deltaXDownTail > -20 && deltaYDownTail > -20)) {
                    MODE = TAIL;
                    Log.d(TAG, "MODE: TAIL");
                    break;
                } else if (mImagePosition != null && mImagePosition.contains((int) mDownX, (int) mDownY)) {
                    MODE = DRAG;
                    Log.d(TAG, "MODE: DRAG");
                    break;
                } else if (mImagePosition != null &&
                        (isZoomX < 40 && isZoomY < 40) && (isZoomX > -40 && isZoomY > -40)) {
                    MODE = ZOOM;
                    Log.d(TAG, "MODE: ZOOM");
                } else {
                    isOnClick = true;
                    canImageMove = false;
                    Log.d(TAG, "isOnClick = true");
                    Log.d(TAG, "canImageMove = false");
                    break;
                }

            case MotionEvent.ACTION_MOVE:
                float SCROLL_THRESHOLD = 10;
                if (canImageMove && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD
                        || Math.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {
                    isOnClick = false;
                    // Check if we have moved far enough that it looks more like a
                    // scroll than a tap
                    final int distY = Math.abs(positionY - prevY);
                    final int distX = Math.abs(positionX - prevX);

                    if (MODE == DRAG && (distX > mTouchSlop || distY > mTouchSlop)) {
                        int deltaX = positionX - prevX;
                        int deltaY = positionY - prevY;
                        // invalidate current position as we are moving...
                        mImagePosition.left = mImagePosition.left + deltaX;
                        mImagePosition.top = mImagePosition.top + deltaY;
                        mImagePosition.right = mImagePosition.right + deltaX;
                        mImagePosition.bottom = mImagePosition.bottom + deltaY;

                        prevX = positionX;
                        prevY = positionY;

                        changeImagePosition();
                        Log.d(TAG, "MODE_DRAG: working");
                    } else if (MODE == ZOOM) {
                        int deltaX = positionX - prevX;
                        int deltaY = positionY - prevY;

                        mImagePosition.top = mImagePosition.top + deltaY;
                        mImagePosition.right = mImagePosition.right + deltaX;

                        prevX = positionX;
                        prevY = positionY;

                        changeImagePosition();
                        Log.d(TAG, "MODE_ZOOM: working");
                    } else if (MODE == TAIL) {
                        int deltaX = positionX - prevTailX;
                        int deltaY = positionY - prevTailY;
                        tailLowXPoint = tailLowXPoint + deltaX;
                        tailLowYPoint = tailLowYPoint + deltaY;
                        prevTailX = positionX;
                        prevTailY = positionY;

                        invalidate();
                        Log.d(TAG, "MODE_TAIL: working");
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                canImageMove = false;
                MODE = NONE;
                if (isOnClick) {
                    Log.d(TAG, "onClick working");
                    for (BubbleView bubble : bubbles) {
                        if (getBubbleId().equals(bubble.getBubbleId())) {
                            continue;
                        }

                        if (bubble.getmImagePosition().contains((int) mDownX, (int) mDownY)) {
                            // index of bubble which placed in x,y coordinates
                            int focusedBubbleIndex = container.indexOfChild(bubble);
                            BubbleView focusedBubble = (BubbleView) container
                                    .getChildAt(focusedBubbleIndex);
                            if (focusedBubble != null) {
                                focusedBubble.bringToFront();
                                seekAlpha.setOnSeekBarChangeListener(new BubbleSetAlphaSeekListener(focusedBubble));
                                seekColor.setOnSeekBarChangeListener(new BubbleSetColorSeekListener(focusedBubble));

                                focusedBubble.drawStroke();
                                showActiveBubble(focusedBubble.getBubbleId());
                                return false;
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void setText(String text) {
        if (textView == null) {
            textView = new TextView(mContext);
            container.addView(textView);
        }

        textView.setText(text);
        textView.postInvalidate();

        changeImagePosition();
    }

    public void setBubbleDrawable(int drawableId) {
        this.drawableId = drawableId;
        options = new BitmapFactory.Options();
        options.inScaled = false;
        // освобождаем память
        if (image != null) {
            image.recycle();
        }
        image = BitmapFactory.decodeResource(mContext.getResources(), drawableId, options);
        mImageHeight = image.getHeight();
        mImageWidth = image.getWidth();
        if (mImagePosition == null) {
            // начальные координаты изображения
            mImagePosition = new Rect(0, 0, 0, 0);
            mImageRegion = new Region();
        }

        mImagePosition.right = mImageWidth/4;
        mImagePosition.bottom = mImageHeight/4;
        mScaledImageHeight = mImageHeight;
        mScaledImageWidth = mImageWidth;

        mImageRegion.set(mImagePosition);

        Log.d(TAG, "setBubbleDrawable");

        updateTextView();
        invalidate();
    }

    private void drawTail(Canvas canvas) {
        float left = mImagePosition.left;
        float right = mImagePosition.right;
        float bottom = mImagePosition.bottom;
        float top = mImagePosition.top;

        tailPaint.setStyle(Paint.Style.FILL);
        tailPaint.setStrokeWidth(5);

        if (mScaledImageHeight == 0) {
            mScaledImageHeight = mImageHeight;
            mScaledImageWidth = mImageWidth;
        }

        int centerX = mScaledImageWidth/2;

        // TODO 50 - constant
        if ((tailLowXPoint == left+centerX || tailLowXPoint == 0) &&
                (tailLowYPoint == bottom+50 || tailLowYPoint == 0)) {
            tailLowXPoint = left+centerX;
            tailLowYPoint = bottom+50;
        }

        float startX1 = left+50;
        float startY1 = bottom-5;
        float startX2 = right-50;
        float startY2 = bottom-5;

//        Log.d(TAG, "------------------------------------------------------------------------------");
//
//        Log.d(TAG, "left: " + left);
//        Log.d(TAG, "right: " + right);
//        Log.d(TAG, "top: " + top);
//        Log.d(TAG, "bottom: " + bottom);
//        Log.d(TAG, "tailLowXPoint: " + tailLowXPoint);
//        Log.d(TAG, "tailLowYPoint: " + tailLowYPoint);
//
//        Log.d(TAG, "------------------------------------------------------------------------------");

        // down side
        if (tailLowXPoint < right && tailLowYPoint > bottom+40) {
            startX1 = left+50;
            startY1 = bottom-5;
            startX2 = right-50;
            startY2 = bottom-5;
//            Log.d(TAG, "IT IS DOWN SIDE: tailLowXPoint < right && tailLowYPoint > bottom+40");
        }

        // left side
        if (tailLowYPoint < bottom+40 &&  tailLowXPoint < left && tailLowXPoint < right) {
            startX1 = left;
            startY1 = top+30;
            startX2 = left;
            startY2 = bottom-30;
//            Log.d(TAG, "IT IS LEFT SIDE: tailLowYPoint < bottom+40 &&  tailLowXPoint < left && tailLowXPoint < right");
        }

        // up sidde
        if (tailLowXPoint < right && tailLowYPoint < bottom+40 && tailLowXPoint > left) {
            startX1 = left+50;
            startY1 = top+10;
            startX2 = right-50;
            startY2 = top+10;
//            Log.d(TAG, "IT IS UP SIDE: tailLowXPoint < right && tailLowYPoint < bottom+40 &&  tailLowXPoint > left");
        }

        // right side
        if (tailLowXPoint > right && tailLowYPoint < bottom) {
            startX1 = right;
            startY1 = top+30;
            startX2 = right;
            startY2 = bottom-30;
//            Log.d(TAG, "IT IS RIGHT SIDE: tailLowXPoint > right && tailLowYPoint > top-70 && tailLowXPoint > bottom");
        }

        Path fillPath = new Path();
        fillPath.moveTo(startX1, startY1);
        fillPath.lineTo(tailLowXPoint, tailLowYPoint);
        fillPath.lineTo(startX2, startY2);

        canvas.drawPath(fillPath, tailPaint);

        if (drawCircleTail) {
            canvas.drawCircle(tailLowXPoint, tailLowYPoint, 10, tailPaint);
        }
        // хвост как пряммые линии
//        canvas.drawLine(startX1, startY1, tailLowXPoint, tailLowYPoint, paint);
//        canvas.drawLine(startX2, startY2, tailLowXPoint, tailLowYPoint, paint);
        Log.d(TAG, "drawTail");
    }

    private void drawResizeButton(Canvas canvas) {
        Paint resizeBtnPaint = new Paint();
        resizeBtnPaint.setColor(Color.RED);
        canvas.drawCircle(mImagePosition.right, mImagePosition.top, 40, resizeBtnPaint);
    }

    private void updateTextView() {
        if (textView != null) {
            int textW = mScaledImageWidth;
            int textH = mScaledImageHeight;
            if (textH == 0 && textW == 0) {
                textW = image.getWidth();
                textH = image.getHeight();
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(textH, textW);
            int mathLine;
            int maxLines;
            // TODO продумать расположение текста
            params.setMargins(getmImagePosition().left + 10, getmImagePosition().top + 10, 0, 0);
            mathLine = mScaledImageHeight / 100;
            maxLines = 1 + mathLine;

            textView.setMaxLines(maxLines);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setLayoutParams(params);
            textView.bringToFront();
            textView.postInvalidate();
        }
    }

    // удаляем обводку у всех баблов кроме того который в фокусе
    public void showActiveBubble(Integer bubbleId) {
        Log.d(TAG, "showActiveBubble");
        for (BubbleView bubble : bubbles) {
            if (!bubble.getBubbleId().equals(bubbleId)) {
                bubble.removeStroke();
            }
        }
    }

    public void scaleImage() {
        // TODO продумать логику максимального и минимального размера бабла
/*        // уменьшение изображения до размеров текста внутри бабла
        int minScaleHeight = 100;
        int minScaleWidth = 150;
        int maxScaleHeigth = 250;
        int maxScaleWidth = 400;
        if (mScaledImageHeight <= minScaleHeight || mScaledImageWidth <= minScaleWidth) {
            mScaledImageHeight = minScaleHeight;
            mScaledImageWidth = minScaleWidth;
        // или очень сильно увеличить
        } else if (mScaledImageHeight > maxScaleHeigth || mScaledImageWidth > maxScaleWidth) {
            mScaledImageHeight = maxScaleHeigth;
            mScaledImageWidth = maxScaleWidth;
        }
        changeImagePosition();
        */
    }

    private void changeImagePosition() {
        if (mImagePosition != null) {
            mImageRegion.set(mImagePosition);
        }

        Log.d(TAG, "changeImagePosition");

        updateTextView();
        invalidate();
    }

    private void drawStroke(Canvas canvas) {
        active = true;
        drawCircleTail = true;
        // раскомментировать если нужна будет рамка
        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK); //set a color
        strokePaint.setStrokeWidth(3); // set your stroke width

        canvas.drawLine(mImagePosition.left-5, mImagePosition.top-5, mImagePosition.right+5, mImagePosition.top-5, strokePaint);
        canvas.drawLine(mImagePosition.right+5, mImagePosition.top-5, mImagePosition.right+5, mImagePosition.bottom-5, strokePaint);
        canvas.drawLine(mImagePosition.left-5, mImagePosition.top-5, mImagePosition.left-5, mImagePosition.bottom-5, strokePaint);
        canvas.drawLine(mImagePosition.left-5, mImagePosition.bottom-5, mImagePosition.right+5, mImagePosition.bottom-5, strokePaint);

        Log.d(TAG, "drawStroke with canvas");
    }

    public void drawStroke() {
        active = true;
        drawCircleTail = true;
        Log.d(TAG, "drawStroke without canvas");
    }

    public BubbleView getActiveBubble() {
        Log.d(TAG, "getActiveBubble");
        for (BubbleView bubble : bubbles) {
            if (bubble.isActive()) {
                Log.d(TAG, "active buble found:" + bubble.getBubbleId());
                return bubble;
            }
        }
        return null;
    }

    public void removeStroke() {
        active = false;
        drawCircleTail = false;
        Log.d(TAG, "removeStroke");
    }

    @Override
    public void setAlpha(int alpha) {
        drawablePaint.setAlpha(alpha);
        tailPaint.setAlpha(alpha);
        postInvalidate();
    }

    public void setColorFilter(ColorFilter cf) {
        drawablePaint.setColorFilter(cf);
        postInvalidate();
    }

    public void setTailColor(int color) {
        tailPaint.setColor(color);
        postInvalidate();
    }

    private float spacing(MotionEvent event) {
        float x = 0;
        float y = 0;
        try {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.d("spacing", "pointerIndex exception");
        }
        return FloatMath.sqrt(x * x + y * y);
    }
}