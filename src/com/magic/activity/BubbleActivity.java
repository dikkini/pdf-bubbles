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
import android.widget.TextView;
import android.widget.Toast;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.BubbleView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    private TextView activeText;

    private HashMap<Integer, TextView> bubblesText = new HashMap<>();

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
        Button setTextBtn = (Button) findViewById(R.id.bubbleview_set_text_button);

        setTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeText = new TextView(BubbleActivity.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        activeBubble.getImage().getWidth(), activeBubble.getImage().getHeight());
                activeText.setLayoutParams(params);
                activeText.setX(activeBubble.getmImagePosition().left);
                activeText.setY(activeBubble.getmImagePosition().top);

                bubblesText.put(activeBubble.getBubbleId(), activeText);

                activeText.setText("Привет как дела?");

                mainRelativeLayout.addView(activeText);
            }
        });

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

                bubble.drawStroke();
                bubbles.add(bubble);

                bubble.showActiveBubble(activeBubble);

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
                            // TODO may be try (how?) to remove activeBubble from list?
                            if (activeBubble.getBubbleId().equals(bubble.getBubbleId())) {
                                continue;
                            }

                            if (bubble.getmImagePosition().contains(x, y)) {
                                // index of bubble which placed in x,y coordinates
                                int focusedBubbleIndex = mainRelativeLayout.indexOfChild(bubble);
                                BubbleView focusedBubble = (BubbleView) mainRelativeLayout
                                        .getChildAt(focusedBubbleIndex);
                                if (focusedBubble != null) {
                                    Log.d(TAG, "New Bubble activated: " + focusedBubble.getBubbleId());
                                    activeBubble = focusedBubble;
                                    focusedBubble.bringToFront();
                                    seekAlpha.setOnSeekBarChangeListener(new BubbleSetAlphaSeekListener(focusedBubble));
                                    seekColor.setOnSeekBarChangeListener(new BubbleSetColorSeekListener(focusedBubble));

                                    focusedBubble.drawStroke();
                                    activeBubble.showActiveBubble(focusedBubble);
                                    return false;
                                }
                            }
                        }
                        if (bubblesText.size() > 0) {
                            activeText = bubblesText.get(activeBubble.getBubbleId());
                            if (activeText != null) {
                                activeText.setX(activeBubble.getmImagePosition().left);
                                activeText.setY(activeBubble.getmImagePosition().top);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        activeBubble.getImage().getWidth(), activeBubble.getImage().getHeight());
                                activeText.setLayoutParams(params);
                                activeText.postInvalidate();
                                activeText.bringToFront();
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
