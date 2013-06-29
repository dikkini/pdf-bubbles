
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
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.magic.BitmapUtils;

public final class BubbleView extends ImageView {
    private Paint drawablePaint = new Paint();
    private Rect drawableRect = new Rect();
    private Paint textPaint = new Paint();
    private Bitmap sourceImage, image;
    private Context mContext;
    private int mScreenHeight, mScreenWidth, prevY, prevX, mImageWidth, mImageHeight, mTouchSlop,
            mScaledImageWidth, mScaledImageHeight;
    private int startX, startY = 10;
    private Rect mImagePosition;
    private Region mImageRegion;
    private boolean canImageMove, isOnClick;
    private float mDownX;
    private float mDownY;
    private final float SCROLL_TRESHOLD = 10;

    public BubbleView(Context context) {
        super(context);
        mContext = context;
    }

    public BubbleView(Context context, AttributeSet attrs) {
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
            mScaledImageWidth = sourceImage.getWidth() + lengthText*2;
            mScaledImageHeight = sourceImage.getHeight() + lengthText;
            Bitmap mutableBitmap = sourceImage.copy(Bitmap.Config.ARGB_8888, true);
            mutableBitmap = BitmapUtils.getScaledBitmap(mutableBitmap, mScaledImageWidth,
                    mScaledImageHeight).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawText(text, 30, mImageHeight/2, textPaint);
            image = mutableBitmap;
            mImagePosition.setEmpty();
            mImagePosition.set(startX, startY, mScaledImageWidth, mScaledImageHeight);
            mImageRegion.setEmpty();
            mImageRegion.set(mImagePosition);
            postInvalidate();
        }
    }

    public void setBubbleDrawable(int drawableId) {
        sourceImage = image = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
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
                isOnClick = true;
                canImageMove = true;
                prevX = positionX;
                prevY = positionY;
                // for listen on click tap
                mDownX = event.getX();
                mDownY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (canImageMove && (Math.abs(mDownX - event.getX()) > SCROLL_TRESHOLD
                    || Math.abs(mDownY - event.getY()) > SCROLL_TRESHOLD)) {
                    isOnClick = false;
                    // Check if we have moved far enough that it looks more like a
                    // scroll than a tap
                    final int distY = Math.abs(positionY - prevY);
                    final int distX = Math.abs(positionX - prevX);

                    if (distX > mTouchSlop || distY > mTouchSlop) {
                        int deltaX = positionX - prevX;
                        int deltaY = positionY - prevY;
                        // Check if delta is added, is the rectangle is within the visible screen
                        if ((mImagePosition.left + deltaX) > 0 && ((mImagePosition.right + deltaX) < mScreenWidth) &&
                                (mImagePosition.top + deltaY) > 0 && ((mImagePosition.bottom + deltaY) < mScreenHeight)) {
                            // invalidate current position as we are moving...
                            mImagePosition.left = mImagePosition.left + deltaX;
                            mImagePosition.top = mImagePosition.top + deltaY;
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
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                canImageMove = false;
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

        drawablePaint.setFilterBitmap(true);
        drawableRect.setEmpty();
        drawableRect.set(0, 0, image.getWidth(), image.getHeight());
        canvas.drawBitmap(image, drawableRect, mImagePosition, drawablePaint);
    }

    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        textPaint.setColorFilter(cf);
    }
}