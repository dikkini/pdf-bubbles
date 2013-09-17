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
        Button removeBubbleBtn = (Button) findViewById(R.id.bubbleview_remove_bubble_button);
        Button setTextBtn = (Button) findViewById(R.id.bubbleview_set_text_button);
        Button draw1Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view1_button);
        Button draw2Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view2_button);
        Button draw3Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view3_button);
        Button draw4Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view4_button);
        Button draw5Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view5_button);
        Button draw6Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view6_button);
        Button draw7Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view7_button);
        Button draw8Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view8_button);
        Button draw9Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view9_button);
        Button draw10Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view10_button);
        Button draw11Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view11_button);
        Button draw12Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view12_button);
        Button draw13Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view13_button);
        Button draw14Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view14_button);
        Button draw15Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view15_button);
        Button draw16Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view16_button);
        Button draw17Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view17_button);
        Button draw18Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view18_button);
        Button draw19Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view19_button);
        Button draw20Btn = (Button) findViewById(R.id.bubbleview_set_bubble_view20_button);
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
                activeBubble.setBubbleDrawable(R.drawable.rectheart);
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
                activeBubble.setBubbleDrawable(R.drawable.thunder);
            }
        });

        draw3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.baloon11);
            }
        });

        draw4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.cloud11);
            }
        });

        draw5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.cloud12);
            }
        });

        draw6Btn.setOnClickListener(new View.OnClickListener() {
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

        draw7Btn.setOnClickListener(new View.OnClickListener() {
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

        draw8Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.arrow11);
            }
        });

        draw9Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.baloon12);
            }
        });

        draw10Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.baloon13);
            }
        });

        draw11Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.bottle11);
            }
        });

        draw12Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.cardsclub);
            }
        });

        draw13Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.cardsdiamond);
            }
        });

        draw14Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.cardsheart);
            }
        });

        draw15Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.cardsspades);
            }
        });

        draw16Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.elipsheart);
            }
        });

        draw17Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.elipsthunder);
            }
        });

        draw18Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.ghost);
            }
        });

        draw19Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleDrawable(R.drawable.ghost12);
            }
        });

        draw20Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                activeBubble.setBubbleSVG(R.raw.android);
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
                bubbleId = bubbles.size() + 1;
                BubbleView bubble = activeBubble = new BubbleView(BubbleActivity.this, imageView,
                        bubbleId, bubbles, mainRelativeLayout, seekColor, seekAlpha);

                bubble.drawStroke();
                bubbles.add(bubble);

                bubble.showActiveBubble(activeBubble.getBubbleId());

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

        removeBubbleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activeBubble == null) {
                    Toast.makeText(BubbleActivity.this, "Add some bubbles before", Toast.LENGTH_SHORT).show();
                    return;
                }
                activeBubble = activeBubble.getActiveBubble();
                Integer bubbleId = activeBubble.getBubbleId();

                // удаляем из списка баблов бабал
                bubbles = activeBubble.getBubbles();
                int index = bubbles.indexOf(activeBubble);
                bubbles.remove(activeBubble);
                if (bubbles.size() == 0) {
                    activeBubble = null;
                } else {
                    int nextIndex;
                    if (index == 0) {
                        nextIndex = ++index;
                    } else {
                        nextIndex = --index;
                    }
                    activeBubble = bubbles.get(nextIndex);
                    activeBubble.showActiveBubble(activeBubble.getBubbleId());
                }
                // удаляем view бабла
                mainRelativeLayout.removeViewAt(bubbleId);
                mainRelativeLayout.invalidate();
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
