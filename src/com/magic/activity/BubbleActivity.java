package com.magic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.magic.BitmapUtils;
import com.magic.R;
import com.magic.views.BubbleView;

import java.io.File;

/**
 * Created by haribo on 6/17/13.
 */
public class BubbleActivity extends Activity {

    private ImageView imageView;
    private BubbleView bubbleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        imageView = (ImageView) findViewById(R.id.bubbleview_imageview);
        bubbleView = (BubbleView) findViewById(R.id.bubbleview_bubble);

        String photoPath = "/storage/emulated/0/DCIM/Camera/IMG_20130423_165339.jpg";

        File photoFile = new File(photoPath);
        Bitmap photo = BitmapUtils.decodeFile(photoFile, 1024, 1024, false);
        imageView.setImageBitmap(photo);

        registerForContextMenu(imageView);
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
                break;
        }
        return true;
    }
}
