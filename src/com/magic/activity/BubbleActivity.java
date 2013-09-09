package com.magic.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Random;

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
    private List<BubbleView> bubbles;

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

        seekColor.setProgress(1);
        seekColor.setMax(BubbleSetColorSeekListener.max);

        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animBlink = AnimationUtils.loadAnimation(this, R.anim.blinking);

        Button blinkAnimBtn = (Button) findViewById(R.id.bubbleview_blink_animation_button);
        Button addNewBubbleBtn = (Button) findViewById(R.id.bubbleview_add_new_bubble_button);
        Button setTextBtn = (Button) findViewById(R.id.bubbleview_set_text_button);
        Button draw1Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view1_button);
        Button draw2Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view2_button);
        Button textSizeBtn = (Button) findViewById(R.id.bubbleview_set_text_font_button);
        Button textFontBtn = (Button) findViewById(R.id.bubbleview_set_text_size_button);

        textSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                TextView textView = activeBubble.getTextView();
                if (textView == null) {
                    Toast.makeText(BubbleActivity.this, "Add some text to bubble before", Toast.LENGTH_SHORT).show();
                    return;
                }
                Random generator = new Random();
                int i = generator.nextInt(15) + 1;
                textView.setTextSize(i);
            }
        });

        textFontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                TextView textView = activeBubble.getTextView();
                if (textView == null) {
                    Toast.makeText(BubbleActivity.this, "Add some text to bubble before", Toast.LENGTH_SHORT).show();
                    return;
                }
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            }
        });

        draw1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.custom_info_bubble);
            }
        });

        draw2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.speech_bubble);
            }
        });

        setTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                final EditText input = new EditText(BubbleActivity.this);
                new AlertDialog.Builder(BubbleActivity.this)
                        .setTitle("Some text for bubble")
                        .setMessage("Text:")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();
                                if (value != null) {
                                    activeBubble = activeBubble.getActiveBubble();
                                    activeBubble.setText(value.toString());
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });

        blinkAnimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.startAnimation(animBlink);
            }
        });

        addNewBubbleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbles = new ArrayList<>();
                if (activeBubble != null) {
                    bubbles = activeBubble.getBubbles();
                }
                bubbleId = bubbleId + 1;
                BubbleView bubble = activeBubble = new BubbleView(BubbleActivity.this, imageView,
                        bubbleId, bubbles, mainRelativeLayout, seekColor, seekAlpha);

                bubble.drawStroke();
                bubbles.add(bubble);

                bubble.showActiveBubble(activeBubble);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.
                        getLayoutParams();

                bubble.setLayoutParams(params);
                mainRelativeLayout.addView(bubble, bubbleId);
                bubble.bringToFront();
                bubble.setBubbleDrawable(R.drawable.custom_info_bubble);
                bubble.setAnimation(animFadeIn);

                seekAlpha.setOnSeekBarChangeListener(new BubbleSetAlphaSeekListener(bubble));
                seekColor.setOnSeekBarChangeListener(new BubbleSetColorSeekListener(bubble));
            }
        });

        String photoPath = "/storage/emulated/0/DCIM/Camera/IMG_20130629_145351.jpg";
        setPhoto(photoPath);
    }

    private void setPhoto(String path) {
        File photoFile = new File(path);
        Bitmap photo = BitmapUtils.decodeFile(photoFile, 1024, 1024, false);
        imageView.setImageBitmap(photo);
    }
}
