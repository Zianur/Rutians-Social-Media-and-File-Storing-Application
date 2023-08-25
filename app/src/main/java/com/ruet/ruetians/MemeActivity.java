package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;



import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;


public class MemeActivity extends AppCompatActivity implements AdapterMemePost.OnMemePostClickListener {

    private ImageButton Add_Meme_Button;
    private Toolbar mtoolbar;
    private RecyclerView MemePostListRecyclerview;
    private DatabaseReference postRef;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private String  currentUser_id;
    private AdapterMemePost adapterMemePost;
    private ArrayList<MemePostModule> memePostModuleArrayList;
    private String meme_key, description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        mAuth = FirebaseAuth.getInstance();
        currentUser_id = mAuth.getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance();
        postRef = FirebaseDatabase.getInstance().getReference().child("MemePosts");



        Add_Meme_Button = (ImageButton) findViewById(R.id.meme_upper_add_post_button);
        MemePostListRecyclerview = (RecyclerView) findViewById(R.id.meme_post_list);
        MemePostListRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        MemePostListRecyclerview.setLayoutManager(linearLayoutManager);

       memePostModuleArrayList = new ArrayList<>();



        mtoolbar = (Toolbar) findViewById(R.id.meme_home_bar);

        //Setting Actionbar
        setSupportActionBar(mtoolbar);
        //setting Actionbar name
        getSupportActionBar().setTitle("Meme Hub");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//for back button



        Add_Meme_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendToMemePostActivity();
            }
        });



         DisplayAllMemePost();






    }

    private void DisplayAllMemePost()
    {
        ClearAll();

        Query query = postRef.limitToLast(50);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren())
                {
                    MemePostModule memePostModule = new MemePostModule();

                    memePostModule.setDate(Snapshot.child("date").getValue().toString());
                    memePostModule.setTime(Snapshot.child("time").getValue().toString());
                    memePostModule.setUserfullname(Snapshot.child("userfullname").getValue().toString());
                    memePostModule.setEmail(Snapshot.child("email").getValue().toString());
                    memePostModule.setDescription(Snapshot.child("description").getValue().toString());
                    memePostModule.setMemeimage(Snapshot.child("memeimage").getValue().toString());
                    memePostModule.setProfileimage(Snapshot.child("profileimage").getValue().toString());
                    memePostModule.setMemeKey(Snapshot.getKey());
                    memePostModule.setUid(Snapshot.child("uid").getValue().toString());


                    memePostModuleArrayList.add(memePostModule);
                }

                adapterMemePost = new AdapterMemePost(MemeActivity.this,memePostModuleArrayList);
                MemePostListRecyclerview.setAdapter(adapterMemePost);
                adapterMemePost.setOnMemePostClickListener(MemeActivity.this);
                adapterMemePost.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MemeActivity.this, "Sorry there was an error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void ClearAll()
    {
        if (memePostModuleArrayList != null)
        {
            memePostModuleArrayList.clear();

            if (adapterMemePost != null)
            {
                adapterMemePost.notifyDataSetChanged();
            }
        }


        memePostModuleArrayList = new ArrayList<>();

    }

    private void SendToMemePostActivity()
    {
        Intent PostmemeIntent = new Intent(MemeActivity.this,MemePostActivity.class);
        startActivity(PostmemeIntent);
    }

    private void SendToMainActivity()
    {
        Intent mainIntent = new Intent(MemeActivity.this,MainActivity.class);
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


    @Override
    public void OnMemePostClick(int position) {

        MemePostModule selected_meme = memePostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

        meme_key = selected_meme.getMemeKey(); //using the module and getter method just like in adapter

       Intent likesCommentsIntent = new Intent(this,LikesAndComments.class);
       likesCommentsIntent.putExtra("key",meme_key);
       startActivity(likesCommentsIntent);
    }

    @Override
    public void OnMemeEditClick(int position) {

        MemePostModule selected_meme = memePostModuleArrayList.get(position);
        meme_key = selected_meme.getMemeKey();
        final String postUid = selected_meme.getUid();
        description = selected_meme.getDescription();

        if (postUid.equals(currentUser_id))
        {

            EditPost();

        }

        else
        {
            Toast.makeText(this, "You can not edit this post", Toast.LENGTH_LONG).show();
        }

    }

    private void EditPost()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MemeActivity.this);
        builder.setTitle("Edit your post");

        final EditText inputField = new EditText(MemeActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                postRef.child(meme_key).child("description").setValue(inputField.getText().toString());

                Intent selfIntent = new Intent(MemeActivity.this,MemeActivity.class);
                startActivity(selfIntent);

                Toast.makeText(MemeActivity.this, "Your post is updated", Toast.LENGTH_LONG).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_purple);
    }

    @Override
    public void OnMemeDeleteClick(int position) {

        MemePostModule selected_meme = memePostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

         meme_key = selected_meme.getMemeKey(); //using the module and getter method just like in adapter

        final String postUid = selected_meme.getUid(); //using the module and getter method just like in adapter

        if (postUid.equals(currentUser_id))
        {
            StorageReference memeRef = mStorage.getReferenceFromUrl(selected_meme.getMemeimage());
            memeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    postRef.child(meme_key).removeValue();

                    Intent selfIntent = new Intent(MemeActivity.this,MemeActivity.class);
                    startActivity(selfIntent);

                    Toast.makeText(MemeActivity.this, "Post is deleted", Toast.LENGTH_LONG).show();

                }
            });

        }

        else
        {
            Toast.makeText(this, "You can not delete this post", Toast.LENGTH_LONG).show();
        }

    }
}