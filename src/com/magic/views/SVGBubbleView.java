package com.magic.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.magic.R;
import com.magic.activity.SVGActivity;

import java.util.Set;

/**
 * Created by haribo on 16.09.13.
 */
public class SVGBubbleView extends ImageView {

    private SVG svg;

    public SVGBubbleView(Context context) {
        super(context);
    }

    public SVGBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SVGBubbleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            svg = SVG.getFromResource(getContext(), R.raw.acid1_embedcss);
            Set<String> viewList = svg.getViewList();
            for (String view : viewList) {
                System.out.println(view);
            }

        } catch (SVGParseException e) {
            e.printStackTrace();
        }

    }

    public void buildSVGImage(SVG svg) {
        this.svg = svg;

        invalidate();
    }
}
