package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.BubbleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haribo on 6/17/13.
 */
public class BubbleActivity extends Activity {

    private static final String TAG = "BubbleActivity";

    private RelativeLayout mainRelativeLayout;
    private SeekBar seekAlpha;
    private SeekBar seekColor;

    private ImageView imageView;
    private BubbleView activeBubble;

    private Animation animFadeIn;
    private Animation animBlink;

    private Integer bubbleId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        mainRelativeLayout = (RelativeLayout) findViewById(R.id.bubbleview_relative_with_bubbles);

        imageView = (ImageView) findViewById(R.id.bubbleview_imageview);
        seekAlpha = (SeekBar) findViewById(R.id.seekAlpha);
        seekColor = (SeekBar) findViewById(R.id.seekColor);

        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animBlink = AnimationUtils.loadAnimation(this, R.anim.blinking);

        Button blinkAnimBtn = (Button) findViewById(R.id.bubbleview_blink_animation_button);
        Button addNewBubbleBtn = (Button) findViewById(R.id.bubbleview_add_new_bubble_button);

        blinkAnimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble.startAnimation(animBlink);
            }
        });

        addNewBubbleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BubbleView> bubbles = new ArrayList<>();
                if (activeBubble != null) {
                    bubbles = activeBubble.getBubbles();
                }
                bubbleId = bubbleId + 1;
                BubbleView bubble = activeBubble = new BubbleView(BubbleActivity.this, imageView,
                        bubbleId, bubbles);
;
                bubbles.add(bubble);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.
                        getLayoutParams();

                bubble.setLayoutParams(params);

                mainRelativeLayout.addView(bubble, bubbleId);
                bubble.setBubbleDrawable(R.drawable.custom_info_bubble);
                bubble.setAnimation(animFadeIn);
                seekAlpha.setOnSeekBarChangeListener(new BubbleSetAlphaSeekListener(bubble));
                seekColor.setOnSeekBarChangeListener(new BubbleSetColorSeekListener(bubble));
                activeBubble.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        List<BubbleView> bubbles = activeBubble.getBubbles();
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        for (BubbleView bubble : bubbles) {

                            if (activeBubble.getBubbleId().equals(bubble.getBubbleId())) {
                                continue;
                            }

                            if (bubble.getmImagePosition().contains( x, y)) {
                                // index of bubble which placed in x,y coordinates
                                int focusedBubbleIndex = mainRelativeLayout.indexOfChild(bubble);
                                BubbleView focusedBubble = (BubbleView) mainRelativeLayout
                                        .getChildAt(focusedBubbleIndex);
                                if (focusedBubble != null) {
                                    Log.d(TAG, "New Bubble activated: " + focusedBubble.getBubbleId());
                                    BubbleActivity.this.activeBubble = focusedBubble;
                                    focusedBubble.bringToFront();
                                    seekAlpha.setOnSeekBarChangeListener(new BubbleSetAlphaSeekListener(focusedBubble));
                                    seekColor.setOnSeekBarChangeListener(new BubbleSetColorSeekListener(focusedBubble));
                                    return false;
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        });



        String photoPath = "/storage/sdcard0/DCIM/Camera/ContactPhoto-IMG_20130417_102638.jpg";
        setPhoto(photoPath);
    }

    private void setPhoto(String path) {
        File photoFile = new File(path);
        Bitmap photo = BitmapUtils.decodeFile(photoFile, 1024, 1024, false);
        imageView.setImageBitmap(photo);
    }
}
