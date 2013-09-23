package com.magic.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.magic.R;
import com.magic.views.SVGBubbleView;

/**
 * Created by haribo on 16.09.13.
 */
public class SVGActivity extends Activity {
    private static final String TAG = "CutPhoto";

    private SVGBubbleView customImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svgbubbles);

        customImageView = (SVGBubbleView) findViewById(R.id.svg_imageview);
    }
}
