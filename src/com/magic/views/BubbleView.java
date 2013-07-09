
package com.magic.views;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.magic.BitmapUtils;

//TODO GLOBAL refactor
public final class BubbleView extends ImageView {
    private Paint drawablePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private Rect drawableRect = new Rect();
    private Paint textPaint = new Paint();
    private Bitmap sourceImage, image;
    private Context mContext;
    private int mScreenHeight, mScreenWidth, prevY, prevX, mImageWidth, mImageHeight, mTouchSlop,
            mScaledImageWidth, mScaledImageHeight;
    // TODO rename it
    private int startX, startY = 10;
    private Rect mImagePosition;
    private Region mImageRegion;
    private boolean canImageMove, isOnClick;
    private float mDownX;
    private float mDownY;

    // TODO ??? what is treshold? why treshold?
    private final float SCROLL_TRESHOLD = 10;

    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    public static int MODE = NONE;
    // TODO rename
    float oldDist;

    float x;
    float y;

    private int drawableId;
    private BitmapFactory.Options options;

    public BubbleView(Context context) {
        // TODO refactor
        super(context);
        mContext = context;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        setAdjustViewBounds(true);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getWidth(); // to get width of the screen
        display.getHeight(); // to get height of the Screen
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();
        canImageMove = false;
    }

    public BubbleView(Context context, AttributeSet attrs) {
        // TODO refactor
        super(context, attrs);
        mContext = context;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        setAdjustViewBounds(true);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getWidth(); // to get width of the screen
        display.getHeight(); // to get height of the Screen
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();
        canImageMove = false;
    }

    public void setText(String text) {
        if (image != null) {
            textPaint = new Paint();
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(20);
            int lengthText = text.length();
            mScaledImageWidth = image.getWidth() + lengthText*2;
            mScaledImageHeight = image.getHeight() + lengthText;
            Bitmap mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true);
            mutableBitmap = BitmapUtils.getScaledBitmap(mutableBitmap, mScaledImageWidth,
                    mScaledImageHeight).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawText(text, 30, mImageHeight/2, textPaint);
            image = mutableBitmap;
            postInvalidate();
        }
    }

    public void setBubbleDrawable(int drawableId) {
        this.drawableId = drawableId;
        options = new BitmapFactory.Options();
        options.inScaled = false;
        sourceImage = image = BitmapFactory.decodeResource(mContext.getResources(), this.drawableId, options);
        mImageHeight = image.getHeight();
        mImageWidth = image.getWidth();
        mImagePosition = new Rect(startX, startY, mImageWidth, mImageHeight);
        mImageRegion = new Region();
        mImageRegion.set(mImagePosition);
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
                            // TODO method
                            if (mScaledImageWidth == 0 && mScaledImageHeight == 0) {
                                mImagePosition.right = mImagePosition.left + mImageWidth;
                                mImagePosition.bottom = mImagePosition.top + mImageHeight;
                            } else {
                                mImagePosition.right = mImagePosition.left + mScaledImageWidth;
                                mImagePosition.bottom = mImagePosition.top + mScaledImageHeight;
                            }
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
                    final EditText input = new EditText(mContext);
                    new AlertDialog.Builder(mContext)
                            .setTitle("Update Status")
                            .setMessage("Eneter the text!")
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Editable value = input.getText();
                                    if (value != null) {
                                        setText(value.toString());
                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Do nothing.
                        }
                    }).show();
                }
                break;
        }
        return true;
    }

    public void scaleImage() {
        // на случай если пользователь захочет уменьшить до нуля размер изображения
        if (mScaledImageHeight <= 0 || mScaledImageWidth <= 0) {
            mScaledImageHeight = startY;
            mScaledImageWidth = startX;
        }
        // TODO constant
        options.inTargetDensity = 0;

        this.image = BitmapFactory.decodeResource(mContext.getResources(), this.drawableId, options);
        // TODO method
        if (mScaledImageWidth == 0 && mScaledImageHeight == 0) {
            mImagePosition.right = mImagePosition.left + mImageWidth;
            mImagePosition.bottom = mImagePosition.top + mImageHeight;
        } else {
            mImagePosition.right = mImagePosition.left + mScaledImageWidth;
            mImagePosition.bottom = mImagePosition.top + mScaledImageHeight;
        }

        mImageRegion.setEmpty();
        mImageRegion.set(mImagePosition);
        invalidate();
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

        drawablePaint.setAntiAlias(false);
        drawablePaint.setFilterBitmap(false);
        drawablePaint.setDither(true);

        drawableRect.setEmpty();
        drawableRect.set(0, 0, image.getWidth(), image.getHeight());

        canvas.drawBitmap(image, drawableRect, mImagePosition, drawablePaint);
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