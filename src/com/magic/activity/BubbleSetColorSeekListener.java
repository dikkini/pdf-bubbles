package com.magic.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.SeekBar;

import com.magic.views.BubbleView;

/**
 * Created by haribo on 7/1/13.
 */
public class BubbleSetColorSeekListener implements SeekBar.OnSeekBarChangeListener {

    private BubbleView bubbleView;

    private final int[] mColors =
            {Color.BLUE, Color.GREEN, Color.RED, Color.LTGRAY, Color.MAGENTA, Color.CYAN,
                    Color.YELLOW, Color.WHITE, Color.BLACK, Color.DKGRAY, Color.GRAY, Color.TRANSPARENT};

    public BubbleSetColorSeekListener(BubbleView bubble) {
        this.bubbleView = bubble;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int mColor = (int) Math.floor(Math.random() * mColors.length);
        bubbleView.setColorFilter(mColors[mColor], PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
