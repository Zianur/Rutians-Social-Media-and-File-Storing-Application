package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private EditText searchInputEditText;
    private ImageButton searchImageButton;
    private RecyclerView searchListRecyclerView;
    private DatabaseReference userRef;
    private ArrayList<SearchModule>searchModuleArrayList;
    private AdapterSearch adapterSearch;
    private String searchedPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchInputEditText = (EditText) findViewById(R.id.search_box_input);
        searchImageButton = (ImageButton) findViewById(R.id.search_image_button);
        searchListRecyclerView = (RecyclerView) findViewById(R.id.search_result_list);
        searchListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        searchListRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        searchModuleArrayList = new ArrayList<>();

        mtoolbar = (Toolbar) findViewById(R.id.search_home_bar);
        //Setting Actionbar
        setSupportActionBar(mtoolbar);
        //setting Actionbar name
        getSupportActionBar().setTitle("Search People");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//for back button




        searchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    FindSomeOne();


            }
        });
    }

    private void FindSomeOne()
    {
        searchedPerson = searchInputEditText.getText().toString();

        if (TextUtils.isEmpty(searchedPerson))
        {
            Toast.makeText(this, "Please enter a valid full name :)", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Searching......", Toast.LENGTH_SHORT).show();

            Query searchQuery = userRef.orderByChild("userfullname").startAt(searchedPerson).endAt(searchedPerson + "\uf8ff");

            searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        SearchModule searchModule = new SearchModule();

                        searchModule.setUserfullname(snapshot.child("userfullname").getValue().toString());
                        searchModule.setEmail(snapshot.child("email").getValue().toString());
                        searchModule.setPhonenumber(snapshot.child("phonenumber").getValue().toString());
                        searchModule.setRoll(snapshot.child("roll").getValue().toString());
                        searchModule.setSeries(snapshot.child("series").getValue().toString());
                        searchModule.setAboutuser(snapshot.child("aboutuser").getValue().toString());
                        searchModule.setProfileimage(snapshot.child("profileimage").getValue().toString());


                        searchModuleArrayList.add(searchModule);


                    }

                    adapterSearch = new AdapterSearch(SearchActivity.this,searchModuleArrayList);
                    searchListRecyclerView.setAdapter(adapterSearch);

                    searchInputEditText.setText("");//clearing search box

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Intent MainIntent = new Intent(SearchActivity.this,MainActivity.class);
                    startActivity(MainIntent);

                    Toast.makeText(SearchActivity.this, "Sorry there was an error", Toast.LENGTH_LONG).show();


                }
            });
        }

//        Toast.makeText(this, "Searching......", Toast.LENGTH_SHORT).show();
//
//        Query searchQuery = userRef.orderByChild("userfullname").startAt(searchedPerson).endAt(searchedPerson + "\uf8ff");
//
//        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    SearchModule searchModule = new SearchModule();
//
//                    searchModule.setUserfullname(snapshot.child("userfullname").getValue().toString());
//                    searchModule.setEmail(snapshot.child("email").getValue().toString());
//                    searchModule.setPhonenumber(snapshot.child("phonenumber").getValue().toString());
//                    searchModule.setRoll(snapshot.child("roll").getValue().toString());
//                    searchModule.setSeries(snapshot.child("series").getValue().toString());
//                    searchModule.setAboutuser(snapshot.child("aboutuser").getValue().toString());
//                    searchModule.setProfileimage(snapshot.child("profileimage").getValue().toString());
//
//
//                    searchModuleArrayList.add(searchModule);
//
//
//                }
//
//                adapterSearch = new AdapterSearch(SearchActivity.this,searchModuleArrayList);
//                searchListRecyclerView.setAdapter(adapterSearch);
//
//                searchInputEditText.setText("");//clearing search box
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//               Intent MainIntent = new Intent(SearchActivity.this,MainActivity.class);
//              startActivity(MainIntent);
//
//                Toast.makeText(SearchActivity.this, "Sorry there was an error", Toast.LENGTH_LONG).show();
//
//
//            }
//        });

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