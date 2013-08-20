package com.magic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.magic.R;
import com.magic.activity.gallery.CustomGallery;
import com.magic.drawgraphic.FingerPaint;
import com.magic.pdf.PdfWizardActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        Button bubbleBtn = (Button) findViewById(R.id.main_bubble_button);
        bubbleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bubleIntent = new Intent(getApplicationContext(), BubbleActivity.class);
                startActivity(bubleIntent);
            }
        });

        Button pdfBtn = (Button) findViewById(R.id.main_pdf_button);
        pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pdfIntent = new Intent(getApplicationContext(), PdfWizardActivity.class);
                startActivity(pdfIntent);
            }
        });

        Button galleryBtn = (Button) findViewById(R.id.main_gallery_button);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(getApplicationContext(), CustomGallery.class);
                startActivity(galleryIntent);
            }
        });

        Button cutPhotoBtn = (Button) findViewById(R.id.main_cut_photo_button);
        cutPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(getApplicationContext(), CutPhotoActivity.class);
                startActivity(galleryIntent);
            }
        });

        Button lasticBtn = (Button) findViewById(R.id.main_lastic_button);
        lasticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(getApplicationContext(), LastikActivity.class);
                startActivity(galleryIntent);
            }
        });

        Button drawingBtn = (Button) findViewById(R.id.main_drawing_button);
        drawingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(getApplicationContext(), DrawingActivity.class);
                startActivity(galleryIntent);
            }
        });

        Button fingerDrawBtn = (Button) findViewById(R.id.main_finger_draw_button);
        fingerDrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(getApplicationContext(), FingerPaint.class);
                startActivity(galleryIntent);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}
