package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.BubbleView;

import java.io.File;

/**
 * Created by haribo on 6/17/13.
 */
public class BubbleActivity extends Activity {

    private BubbleView bubbleView;
    private BubbleView bubbleView2;

    private RelativeLayout views;
    private SeekBar seekAlpha;
    private SeekBar seekColor;

    private Animation animFadeIn;
    private Animation animBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        views = (RelativeLayout) findViewById(R.id.bubbleview_relative_with_bubbles);

        ImageView imageView = (ImageView) findViewById(R.id.bubbleview_imageview);
        bubbleView = (BubbleView) findViewById(R.id.bubbleview_bubble);
        bubbleView2 = (BubbleView) findViewById(R.id.bubbleview_bubble2);

        seekAlpha = (SeekBar) findViewById(R.id.seekAlpha);
        seekAlpha.setOnSeekBarChangeListener(new BubbleSetAlphaSeekListener(bubbleView));

        seekColor = (SeekBar) findViewById(R.id.seekColor);
        seekColor.setOnSeekBarChangeListener(new BubbleSetColorSeekListener(bubbleView));

        Button alphaColorBtn = (Button) findViewById(R.id.bubbleview_alpha_color_button);
        Button blinkAnimBtn = (Button) findViewById(R.id.bubbleview_blink_animation_button);
        Button addNewBubbleBtn = (Button) findViewById(R.id.bubbleview_addnewbubble);
        Button choosePrevBubble = (Button) findViewById(R.id.bubbleview_choose_previous_bubble_button);
        Button chooseNextBubble = (Button) findViewById(R.id.bubbleview_choose_next_bubble_buttn);

        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animBlink = AnimationUtils.loadAnimation(this, R.anim.blinking);

        choosePrevBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleView.bringToFront();
            }
        });
        chooseNextBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleView2.bringToFront();
            }
        });

        addNewBubbleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleView2.setBubbleDrawable(R.drawable.custom_info_bubble);
                bubbleView2.setVisibility(View.VISIBLE);
                bubbleView2.startAnimation(animFadeIn);
            }
        });

        blinkAnimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleView.startAnimation(animBlink);
            }
        });

        String photoPath = "/storage/sdcard0/DCIM/Camera/ContactPhoto-IMG_20130417_102638.jpg";

        File photoFile = new File(photoPath);
        Bitmap photo = BitmapUtils.decodeFile(photoFile, 1024, 1024, false);
        imageView.setImageBitmap(photo);

        registerForContextMenu(imageView);

        alphaColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekAlpha.setVisibility(View.VISIBLE);
                seekColor.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bubble_actions_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.add_bubble:
                bubbleView.setBubbleDrawable(R.drawable.custom_info_bubble);
                bubbleView.setVisibility(View.VISIBLE);
                bubbleView.startAnimation(animFadeIn);
                break;
        }
        return true;
    }
}
