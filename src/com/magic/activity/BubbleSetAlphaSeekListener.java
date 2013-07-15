package com.magic.activity;

import android.util.Log;
import android.widget.SeekBar;

import com.magic.views.BubbleView;

/**
 * Created by haribo on 7/1/13.
 */
public class BubbleSetAlphaSeekListener implements SeekBar.OnSeekBarChangeListener {

    private BubbleView bubbleView;

    public BubbleSetAlphaSeekListener(BubbleView bubble) {
        this.bubbleView = bubble;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("Seek", "Alpha: " + progress);
        bubbleView.setAlpha(255-progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
