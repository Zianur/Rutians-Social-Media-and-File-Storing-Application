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

public class GetHelpActivity extends AppCompatActivity {

    private ImageView callButton;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);


        callButton = (ImageView) findViewById(R.id.call_button);

        mtoolbar = (Toolbar) findViewById(R.id.get_help_home_bar);

        setSupportActionBar(mtoolbar); //setting actionbar
        getSupportActionBar().setTitle("Help / Report");//setting actionbar name
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        callButton.setOnClickListener(new View.OnClickListener() {
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