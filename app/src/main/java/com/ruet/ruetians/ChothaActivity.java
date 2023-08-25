package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChothaActivity extends AppCompatActivity {

    private ImageButton addImageButton;
    private Toolbar mtoolbar;
    private AutoCompleteTextView autoCompleteTextView;
    private ListView listView;
    private List<AddFileModule> addFileModuleList;
    private DatabaseReference filePostRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chotha);


        filePostRef = FirebaseDatabase.getInstance().getReference().child("File");
        addImageButton = (ImageButton) findViewById(R.id.chotha_upper_add_post_button);
        listView = (ListView) findViewById(R.id.listView);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_view);


        addFileModuleList = new ArrayList<>();


        mtoolbar = (Toolbar) findViewById(R.id.chotha_home_bar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chotha And Books");//setting actionbar name
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToAddFileActivity();
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //getting the upload
                AddFileModule addFileModule = addFileModuleList.get(position);

                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(addFileModule.getUrl()));
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the upload
                AddFileModule addFileModule = addFileModuleList.get(i);

                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(addFileModule.getUrl()));
                startActivity(intent);
            }
        });


        filePostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    AddFileModule addFileModule = snapshot.getValue(AddFileModule.class);

                    addFileModuleList.add(addFileModule);
                }

                String[] uploads = new String[addFileModuleList.size()];

                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = addFileModuleList.get(i).getName();

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads);
                listView.setAdapter(adapter);
                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void SendToAddFileActivity()
    {

        Intent addFileIntent = new Intent(this,AddFileActivity.class);
        startActivity(addFileIntent);

    }

    private void SendToMainActivity()
    {
        Intent mainIntent = new Intent(ChothaActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home)
        {

            SendToMainActivity();

        }
        return super.onOptionsItemSelected(item);


    }
}