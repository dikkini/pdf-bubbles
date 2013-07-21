
package com.magic.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.magic.activity.BubbleSetAlphaSeekListener;
import com.magic.activity.BubbleSetColorSeekListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

//TODO GLOBAL refactor
public final class BubbleView extends ImageView {
    private final static String TAG = "BubbleView";

    private Paint drawablePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private Rect drawableRect = new Rect();
    private Paint textPaint = new Paint();
    private Bitmap image;
    private Context mContext;
    private int mScreenHeight, mScreenWidth, prevY, prevX, mImageWidth, mImageHeight, mTouchSlop,
            mScaledImageWidth, mScaledImageHeight;
    // TODO rename it
    private int startXPosition, startYPosition = 10;
    private Rect mImagePosition;
    private Region mImageRegion;
    private boolean canImageMove, isOnClick;
    private float mDownX;
    private float mDownY;

    // TODO ??? what is treshold? why treshold?
    private final float SCROLL_TRESHOLD = 10;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static int MODE = NONE;
    // TODO rename
    float oldDist;

    // TODO more accuracy. rename it
    private float x;
    private float y;

    private BitmapFactory.Options options;

    private Integer bubbleId;
    private List<BubbleView> bubbles;
    private RelativeLayout container;
    private SeekBar seekColor;
    private SeekBar seekAlpha;
    private TextView textView;

    private boolean active;

    public TextView getTextView() {
        return textView;
    }

    public Bitmap getImage() {
        return image;
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

    public void setText(String text) {
        if (textView == null) {
            textView = new TextView(mContext);
            textView.setText(text);
            updateTextView();
            container.addView(textView);
            textView.postInvalidate();
        } else {
            textView.setText(text);
            updateTextView();
            scaleImage();
        }
        postInvalidate();
    }

    public void setBubbleDrawable(int drawableId) {
        options = new BitmapFactory.Options();
        options.inScaled = false;
        image = BitmapFactory.decodeResource(mContext.getResources(), drawableId, options);
        mImageHeight = image.getHeight();
        mImageWidth = image.getWidth();
        mImagePosition = new Rect(startXPosition, startYPosition, mImageWidth, mImageHeight);
        mImageRegion = new Region();
        mImageRegion.set(mImagePosition);
        updateTextView();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int positionX = (int) event.getRawX();
        int positionY = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                MODE = DRAG;
                isOnClick = true;
                canImageMove = true;
                prevX = positionX;
                prevY = positionY;
                // for listen on click tap
                mDownX = event.getX();
                mDownY = event.getY();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);

                // TODO constant
                if (oldDist > 10f) {
                    MODE = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (canImageMove && (Math.abs(mDownX - event.getX()) > SCROLL_TRESHOLD
                    || Math.abs(mDownY - event.getY()) > SCROLL_TRESHOLD)) {
                    isOnClick = false;
                    // Check if we have moved far enough that it looks more like a
                    // scroll than a tap
                    final int distY = Math.abs(positionY - prevY);
                    final int distX = Math.abs(positionX - prevX);

                    if (MODE == DRAG && (distX > mTouchSlop || distY > mTouchSlop)) {
                        int deltaX = positionX - prevX;
                        int deltaY = positionY - prevY;
                        // Check if delta is added, is the rectangle is within the visible screen
                        if ((mImagePosition.left + deltaX) > 0 && ((mImagePosition.right + deltaX) < mScreenWidth) &&
                                (mImagePosition.top + deltaY) > 0 && ((mImagePosition.bottom + deltaY) < mScreenHeight)) {
                            // invalidate current position as we are moving...
                            mImagePosition.left = mImagePosition.left + deltaX;
                            mImagePosition.top = mImagePosition.top + deltaY;
                            changeImagePosition();
                            mImageRegion.set(mImagePosition);
                            prevX = positionX;
                            prevY = positionY;

                            invalidate();
                        }
                    } else if (MODE == ZOOM) {
                        float newDist2 = spacing(event);
                        // TODO constant
                        if (newDist2 > 10f) {
                            mScaledImageHeight = (int) (newDist2 / oldDist * mImageHeight);
                            mScaledImageWidth = (int) (newDist2 / oldDist * mImageWidth);

                            scaleImage();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                canImageMove = false;
                MODE = NONE;
                if (isOnClick) {
                    for (BubbleView bubble : bubbles) {
                        // TODO may be try (how?) to remove activeBubble from list?
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
                                showActiveBubble(focusedBubble);
                                return false;
                            }
                        }
                    }
                }
                break;
        }
        updateTextView();
        return true;
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
            params.setMargins(getmImagePosition().left + 10, getmImagePosition().top + 10, 0, 0);

            Log.d(TAG, "mScaledImageHeight" + mScaledImageHeight);
            int test = mScaledImageHeight / 100;
            Log.d(TAG, "TextView TEST: " + test);
            int maxLines = 1 + test;
            Log.d(TAG, "TextView MAX: " + maxLines);
            textView.setMaxLines(maxLines);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setLayoutParams(params);
            textView.bringToFront();
            textView.postInvalidate();
            postInvalidate();
        }
    }

    // удаляем обводку у всех баблов кроме того который в фокусе
    public void showActiveBubble(BubbleView activeBubble) {
        for (BubbleView bubble : bubbles) {
            if (!bubble.getBubbleId().equals(activeBubble.getBubbleId())) {
                bubble.removeStroke();
            }
        }
    }

    public void scaleImage() {
        // уменьшение изображения до размеров текста внутри бабла
        int minScaleHeight = 10;
        int minScaleWidth = 10;
        int maxScaleHeigth = 250;
        int maxScaleWidth = 300;

        if (mScaledImageHeight <= minScaleHeight || mScaledImageWidth <= minScaleWidth) {
            mScaledImageHeight = minScaleHeight;
            mScaledImageWidth = minScaleWidth;
        // или очень сильно увеличить
        } else if (mScaledImageHeight > maxScaleHeigth || mScaledImageWidth > maxScaleWidth) {
            mScaledImageHeight = maxScaleHeigth;
            mScaledImageWidth = maxScaleWidth;
        }
        // TODO constant
        options.inTargetDensity = 0;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.image.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        this.image = BitmapFactory.decodeStream(bs, null, options);
        changeImagePosition();

        mImageRegion.setEmpty();
        mImageRegion.set(mImagePosition);
        invalidate();
    }

    private void changeImagePosition() {
        if (mScaledImageWidth == 0 && mScaledImageHeight == 0) {
            mImagePosition.right = mImagePosition.left + mImageWidth;
            mImagePosition.bottom = mImagePosition.top + mImageHeight;
        } else {
            mImagePosition.right = mImagePosition.left + mScaledImageWidth;
            mImagePosition.bottom = mImagePosition.top + mScaledImageHeight;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // если изображение не установлено
        if (image == null) {
            drawablePaint.setStyle(Paint.Style.FILL);
            drawablePaint.setColor(Color.WHITE);
            if (mImagePosition == null) {
                canvas.drawRect(10F, 10F, 10F, 10F, drawablePaint);
                return;
            }
            canvas.drawRect(mImagePosition, textPaint);
            return;
        }

        drawablePaint.setAntiAlias(true);
        drawablePaint.setFilterBitmap(false);
        drawablePaint.setDither(true);

        drawableRect.setEmpty();
        drawableRect.set(0, 0, image.getWidth(), image.getHeight());
        // рисуем рамку
        /*
        if (active) {
            drawStroke(canvas);
        }
        */

        canvas.drawBitmap(image, drawableRect, mImagePosition, drawablePaint);
    }

    public void drawStroke() {
        active = true;
        postInvalidate();
    }

    public BubbleView getActiveBubble() {
        for (BubbleView bubble : bubbles) {
            if (bubble.isActive()) {
                return bubble;
            }
        }
        return null;
    }

    private void drawStroke(Canvas canvas) {
        active = true;
        drawablePaint.setColor(Color.BLACK); //set a color
        drawablePaint.setStrokeWidth(3); // set your stroke width

        canvas.drawLine(mImagePosition.left-5, mImagePosition.top-5, mImagePosition.right+5, mImagePosition.top-5, drawablePaint);
        canvas.drawLine(mImagePosition.right+5, mImagePosition.top-5, mImagePosition.right+5, mImagePosition.bottom-5, drawablePaint);
        canvas.drawLine(mImagePosition.left-5, mImagePosition.top-5, mImagePosition.left-5, mImagePosition.bottom-5, drawablePaint);
        canvas.drawLine(mImagePosition.left-5, mImagePosition.bottom-5, mImagePosition.right+5, mImagePosition.bottom-5, drawablePaint);

        postInvalidate();
    }

    public void removeStroke() {
        active = false;
        postInvalidate();
    }

    @Override
    public void setAlpha(int alpha) {
        drawablePaint.setAlpha(alpha);
        postInvalidate();
    }

    public void setColorFilter(ColorFilter cf) {
        drawablePaint.setColorFilter(cf);
        postInvalidate();
    }

    private float spacing(MotionEvent event) {
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