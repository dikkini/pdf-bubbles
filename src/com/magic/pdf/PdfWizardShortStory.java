package com.magic.pdf;

/**
 * Created by haribo on 6/17/13.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magic.R;
import com.magic.utils.Utils;

/**
 * Class for choosy pdf cover
 */
public class PdfWizardShortStory extends FragmentActivity {

    private static final String TAG = "PdfWizardShortStory";

    private EditText shortStoryEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_wizard_set_short_story);

        Button enterShortStory = (Button) findViewById(R.id.pdf_wizard_short_story_button);

        shortStoryEdit = (EditText) findViewById(R.id.pdf_wizard_short_story_edittext);

        enterShortStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Editable text = shortStoryEdit.getText();
                if (text == null) {
                    Toast.makeText(PdfWizardShortStory.this, "Enter some text", Toast.LENGTH_SHORT).show();
                    return;
                }
                i.putExtra(Constants.PDF_SHORT_STORY, text.toString());
                setResult(Constants.PDF_WIZARD_GET_SHORT_STORY, i);
                finish();
            }
        });
    }
}