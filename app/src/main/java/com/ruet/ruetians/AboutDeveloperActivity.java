package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class AboutDeveloperActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageView callImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);

        callImageButton = (ImageView) findViewById(R.id.about_developer_call_button);
        mtoolbar = (Toolbar) findViewById(R.id.about_developer_home_bar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("About Developer");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + "01673461416"));
                startActivity(callIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home)
        {

            onBackPressed();

        }
        return super.onOptionsItemSelected(item);


    }
}