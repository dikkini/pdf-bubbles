package com.magic.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.widget.ImageView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.magic.R;
import com.magic.activity.SVGActivity;

import java.util.Set;

/**
 * Created by haribo on 16.09.13.
 */
public class SVGBubbleView extends ImageView {
    private final static String TAG = "SVGBubbleView";
    private static Context mContext;

    private final static float stdDist = 10f;
    // режимы дейстий
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int TAIL = 3;

    private static int MODE = NONE;

    private Picture pic;
    private float movingDist;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public SVGBubbleView(Context context) {
        super(context);
        mContext = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public SVGBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public SVGBubbleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SVG svg = SVGParser.getSVGFromResource(mContext.getResources(), R.raw.acid1_embedcss);
        pic = svg.getPicture();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
//        int positionX = (int) event.getRawX();
//        int positionY = (int) event.getRawY();
//
//        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                movingDist = spacing(event);
//                if (movingDist > stdDist) {
//                    MODE = ZOOM;
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
        return true;
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


    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }
}
