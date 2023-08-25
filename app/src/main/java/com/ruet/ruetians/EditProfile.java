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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private Toolbar mtoolbar;
    private EditText phoneNumberEditText, aboutUserEditText;
    private CircleImageView userProfileImage;
    private Button updateProfileButton;
    private ProgressDialog loadingbar;
    private StorageReference UserProfileImageRef;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");


        phoneNumberEditText = (EditText) findViewById(R.id.edit_profile_phone_number);
        aboutUserEditText = (EditText) findViewById(R.id.edit_profile_about);
        updateProfileButton = (Button) findViewById(R.id.update_profile_button);
        userProfileImage = (CircleImageView) findViewById(R.id.edit_profile_image);
        loadingbar = new ProgressDialog(this);


        mtoolbar = (Toolbar) findViewById(R.id.edit_profile_home_bar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                String phoneNumber = dataSnapshot.child("phonenumber").getValue().toString();
                String aboutUser = dataSnapshot.child("aboutuser").getValue().toString();

                phoneNumberEditText.setText(phoneNumber);
                aboutUserEditText.setText(aboutUser);
                Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateProfile();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //this is just to select image from gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType(("image/*"));
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });


    }

    //onActivityResult is depreciated in 29 api
    //By this we are gonna get the image

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();//got image from gallery normally But but but


            //Now we are gonna crop it

//            //Redirects to croping
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(this);


            loadingbar.setTitle("Updating Profile Image");
            loadingbar.setMessage("Please wait while we are updating your profile image");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();


            final StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg"); //creating a node with user name

            //Now putting the image on the storage
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfile.this, "Picture is added to storage", Toast.LENGTH_SHORT).show();

                        final String downloadUrl = task.getResult().getDownloadUrl().toString();//getting that image link situated in storage

                        //putting that image in database under user node with the name ProfileImage
                        userRef.child("profileimage").setValue(downloadUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            Toast.makeText(EditProfile.this, "Picture is added", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        } else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(EditProfile.this, "Error Occured" + " " + message, Toast.LENGTH_LONG).show();
                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                    }
                    else

                    {
                        Toast.makeText(EditProfile.this, "Try again!!!! Or Select another photo.", Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }
    }


//        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
//        {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);//croping in proccess
//
//            if(resultCode == RESULT_OK) //if it is true then the pic is croped
//            {
//
//                loadingbar.setTitle("Updating Profile Image");
//                loadingbar.setMessage("Please wait while we are updating your profile image");
//                loadingbar.setCanceledOnTouchOutside(true);
//                loadingbar.show();
//
//
//
//                Uri resultUri = result.getUri(); //getting the croped image
//
//                final StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg"); //creating a node with user name
//
//                //Now putting the image on the storage
//                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            Toast.makeText(EditProfile.this, "Picture is added to storage", Toast.LENGTH_SHORT).show();
//
//                            final String downloadUrl = task.getResult().getDownloadUrl().toString ();//getting that image link situated in storage
//
//                            //putting that image in database under user node with the name ProfileImage
//                            userRef.child("profileimage").setValue(downloadUrl)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task)
//                                        {
//                                            if (task.isSuccessful())
//                                            {
//
//
//                                                Toast.makeText(EditProfile.this, "Picture is added", Toast.LENGTH_SHORT).show();
//                                                loadingbar.dismiss();
//                                            }
//                                            else
//                                            {
//                                                String message = task.getException().getMessage();
//                                                Toast.makeText(EditProfile.this, "Error Occured" + " " + message, Toast.LENGTH_LONG).show();
//                                                loadingbar.dismiss();
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                });
//            }
//        }
//        else
//        {
//            Toast.makeText(EditProfile.this, "Try again!!!! Or Select another photo.", Toast.LENGTH_LONG).show();
//            loadingbar.dismiss();
//        }
   // }



    private void UpdateProfile()
    {
        String phoneNumber = phoneNumberEditText.getText().toString();
        String aboutUser = aboutUserEditText.getText().toString();

        if (TextUtils.isEmpty(phoneNumber))
        {
            Toast.makeText(this, "Phone number is missing", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(aboutUser))
        {
            Toast.makeText(this, "Please write something about yourself", Toast.LENGTH_SHORT).show();
        }
        else
        {
            userRef.child("phonenumber").setValue(phoneNumber);
            userRef.child("aboutuser").setValue(aboutUser);

             SendToMainActivity();

            Toast.makeText(this, "Your Profile is updated", Toast.LENGTH_LONG).show();

        }
    }

    private void SendToMainActivity()
    {
        Intent mainIntent = new Intent(EditProfile.this,MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}