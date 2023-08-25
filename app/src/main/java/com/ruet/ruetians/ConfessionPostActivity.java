package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfessionPostActivity extends AppCompatActivity {

    private ImageButton selectImageButton;
    private EditText confessionDescription;
    private Button confessionPostButton;
    private ProgressDialog loadingbar;
    private Toolbar mtoolbar;
    private Uri imageUri;
    String postInfo;
    private StorageReference PostconfessionReference;
    private DatabaseReference userRef, postRef;
    private FirebaseAuth mAuth;
    private String savecurrentdate, savecurrentTime, postrandomName, downloadUrl, currentUser_id;
    private static final int Gallery_Pick=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confession_post);

        mAuth = FirebaseAuth.getInstance();
        currentUser_id = mAuth.getCurrentUser().getUid();
        PostconfessionReference = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("ConfessionPosts");


        selectImageButton = (ImageButton) findViewById(R.id.confession_image_select_button);
        confessionDescription = (EditText) findViewById(R.id.confession_description);
        confessionPostButton = (Button) findViewById(R.id.confession_post_button);
        mtoolbar = (Toolbar) findViewById(R.id.confession_postactivity_bar);
        loadingbar = new ProgressDialog(this);


        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenGallery();
            }
        });

        confessionPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValitadeDescription();
            }
        });

    }

    private void ValitadeDescription()
    {
        postInfo = confessionDescription.getText().toString();

        if(imageUri == null)
        {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(postInfo))
        {
            Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingbar.setTitle("Uploading Post");
            loadingbar.setMessage("Please wait while we are uploading your post");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            StoringImageTOFirebaseStorage();
        }


    }

    private void StoringImageTOFirebaseStorage()
    {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        savecurrentTime = currentTime.format(callForTime.getTime());

        postrandomName = savecurrentdate + savecurrentTime;

        StorageReference FilePath = PostconfessionReference.child("ConfessionImage").child(imageUri.getLastPathSegment() + postrandomName + ".jpg");

        FilePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful())
                {
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    //might need to use getStorage in future
                    Toast.makeText(ConfessionPostActivity.this, "Image uploaded to storage", Toast.LENGTH_SHORT).show();

                    SavingInfoToDatabase();
                }
                else
                {
                    String messege = task.getException().getMessage();
                    Toast.makeText(ConfessionPostActivity.this, "Error" + messege, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void SavingInfoToDatabase()
    {
        userRef.child(currentUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String UserfullName = dataSnapshot.child("userfullname").getValue().toString();
                    String UserEmail = dataSnapshot.child("email").getValue().toString();
                    String UserProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap memepostMap = new HashMap();
                    memepostMap.put("uid", currentUser_id);
                    memepostMap.put("date", savecurrentdate);
                    memepostMap.put("time", savecurrentTime);
                    memepostMap.put("description", postInfo);
                    memepostMap.put("confessionimage", downloadUrl);
                    memepostMap.put("userfullname", UserfullName);
                    memepostMap.put("email", UserEmail);

                    String uniqueKey = postRef.push().getKey(); //getting a unique key

                    postRef.child(postrandomName + UserfullName + uniqueKey).updateChildren(memepostMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful())
                                    {
                                        SendToConfessionActivity();


                                        Toast.makeText(ConfessionPostActivity.this, "New post is updated successfully", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(ConfessionPostActivity.this, "Error: Could not update your post", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                }
                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void SendToConfessionActivity()
    {
        Intent confessionIntent = new Intent(ConfessionPostActivity.this,ConfessionActivity.class);
        startActivity(confessionIntent);

    }

    private void OpenGallery()
    {

        //this is just to select image from gallery
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setType(("image/*"));
        startActivityForResult(galleryIntent,Gallery_Pick);

    }

    @Override
    protected void onActivityResult (int requestCode,
                                     int resultCode,
                                     Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {


            imageUri = data.getData();//got image from gallery

            selectImageButton.setImageURI(imageUri);//setting the image in image button





        }
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