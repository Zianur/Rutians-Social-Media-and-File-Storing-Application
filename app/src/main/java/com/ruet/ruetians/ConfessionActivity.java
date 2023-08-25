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
import android.view.ViewGroup;
import android.widget.Button;
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

public class ConfessionActivity extends AppCompatActivity implements AdapterConfessionPost.OnConfessionPostClickListener {

    private Toolbar mtoolbar;
    private ImageButton Add_Confession_Button;
    private FirebaseAuth mAuth;
    private DatabaseReference postRef;
    private String  currentUser_id, postKey, description;
    private RecyclerView confessionRecyclerView;
    private ArrayList<ConfessionPostModule>confessionPostModuleArrayList;
    private AdapterConfessionPost adapterConfessionPost;
    private FirebaseStorage mStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confession);

        mAuth = FirebaseAuth.getInstance();
        currentUser_id = mAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference().child("ConfessionPosts");
        mStorage = FirebaseStorage.getInstance();




        Add_Confession_Button = (ImageButton) findViewById(R.id.confession_upper_add_post_button);
        confessionRecyclerView = (RecyclerView) findViewById(R.id.confession_post);
        confessionRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        confessionRecyclerView.setLayoutManager(linearLayoutManager);

        confessionPostModuleArrayList = new ArrayList<>();



        mtoolbar = (Toolbar) findViewById(R.id.confession_home_bar);
        //Setting Actionbar
        setSupportActionBar(mtoolbar);
        //setting Actionbar name
        getSupportActionBar().setTitle("Confession");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//for back button


        Add_Confession_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendToConfessionPostActivity();
            }
        });


        DisplayAllImage();




    }

    private void DisplayAllImage()
    {
        ClearAll();

        Query query = postRef.limitToFirst(50);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    ConfessionPostModule confessionPostModule = new ConfessionPostModule();

                    confessionPostModule.setDate(snapshot.child("date").getValue().toString());
                    confessionPostModule.setTime(snapshot.child("time").getValue().toString());
                    confessionPostModule.setDescription(snapshot.child("description").getValue().toString());
                    confessionPostModule.setConfessionimage(snapshot.child("confessionimage").getValue().toString());

                    confessionPostModule.setPostKey(snapshot.getKey());

                    confessionPostModule.setUid(snapshot.child("uid").getValue().toString());

                    confessionPostModuleArrayList.add(confessionPostModule);
                }

                adapterConfessionPost = new AdapterConfessionPost(ConfessionActivity.this,confessionPostModuleArrayList);
                confessionRecyclerView.setAdapter(adapterConfessionPost);
                adapterConfessionPost.setOnConfessionPostClickListener(ConfessionActivity.this);
                adapterConfessionPost.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(ConfessionActivity.this, "Sorry there was an error", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void ClearAll()
    {

        if (confessionPostModuleArrayList != null)
        {
            confessionPostModuleArrayList.clear();

            if (adapterConfessionPost != null)
            {
                adapterConfessionPost.notifyDataSetChanged();
            }
        }


        confessionPostModuleArrayList = new ArrayList<>();
    }


    private void SendToConfessionPostActivity()
    {
        Intent confessionPostIntent  = new Intent(ConfessionActivity.this,ConfessionPostActivity.class);
        startActivity(confessionPostIntent);
    }

    private void SendToMainActivity()
    {
        Intent mainIntent = new Intent(ConfessionActivity.this,MainActivity.class);
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
    public void OnConfessionPostClick(int position) {

        ConfessionPostModule selected_post = confessionPostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

        postKey = selected_post.getPostKey(); //using the module and getter method just like in adapter

        Intent likesCommentsIntent = new Intent(this,LikesAndComments.class);
        likesCommentsIntent.putExtra("key",postKey);
        startActivity(likesCommentsIntent);

    }

    @Override
    public void OnConfessionPostEditClick(int position) {

        ConfessionPostModule selected_post = confessionPostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

        postKey = selected_post.getPostKey(); //using the module and getter method just like in adapter

        final String postUid = selected_post.getUid(); //using the module and getter method just like in adapter

        description = selected_post.getDescription();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfessionActivity.this);
        builder.setTitle("Edit your post");

        final EditText inputField = new EditText(ConfessionActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                postRef.child(postKey).child("description").setValue(inputField.getText().toString());

                Intent selfIntent = new Intent(ConfessionActivity.this,ConfessionActivity.class);
                startActivity(selfIntent);

                Toast.makeText(ConfessionActivity.this, "Your post is updated", Toast.LENGTH_LONG).show();

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
    public void OnConfessionPostDeleteClick(int position) {

        ConfessionPostModule selected_post = confessionPostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

        postKey = selected_post.getPostKey(); //using the module and getter method just like in adapter

        final String postUid = selected_post.getUid(); //using the module and getter method just like in adapter

        if (postUid.equals(currentUser_id))
        {
            StorageReference imageRef = mStorage.getReferenceFromUrl(selected_post.getConfessionimage());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    postRef.child(postKey).removeValue();

                    Intent selfIntent = new Intent(ConfessionActivity.this,ConfessionActivity.class);
                    startActivity(selfIntent);

                    Toast.makeText(ConfessionActivity.this, "Post is deleted", Toast.LENGTH_LONG).show();

                }
            });

        }

        else
        {
            Toast.makeText(this, "You can not delete this post", Toast.LENGTH_LONG).show();
        }

    }

}