package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText FullName, Roll, PhoneNumber, AboutUser, Series;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingbar;


    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    String CurrentUserid, email;

    final static int Gallery_Pick = 1;
    private Boolean checkProfilePic = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();

        CurrentUserid = mAuth.getCurrentUser().getUid();//getting authentication unique id

        //creating a node/child under User with that authentication unique id in database
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserid);

        //creating node in storage
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        loadingbar = new ProgressDialog(this);

        FullName = (EditText) findViewById(R.id.setup_fullname);
        Roll = (EditText) findViewById(R.id.setup_roll);
        PhoneNumber = (EditText) findViewById(R.id.setup_phone_number);
        Series = (EditText) findViewById(R.id.setup_series);
        AboutUser = (EditText) findViewById(R.id.setup_about_user);
        Button saveInformationButton = (Button) findViewById(R.id.setup_save_button);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profileimage);


        //Here we will store profile image in storage and transfer it into database
        //And also store others information in database directly


        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveAccountSetupInformation();
            }
        });


        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //this is just to select image from gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType(("image/*"));
                startActivityForResult(galleryIntent, Gallery_Pick);

            }
        });


        //putting image into setup imageview from database
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    if (snapshot.hasChild("profileimage")) {

                        checkProfilePic = true;//checking if the profile is uploaded or not for later

                        String image = snapshot.child("profileimage").getValue().toString();//getting the image from the database
                        //loading the image with picasso library into imageview
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    } else {
                        Toast.makeText(SetupActivity.this, "Select a profile image first", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


//            //Now we are gonna crop it
//
//            //Redirects to croping
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(this);


            loadingbar.setTitle("Updating Profile Image");
            loadingbar.setMessage("Please wait while we are updating your profile image");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();


            final StorageReference filepath = UserProfileImageRef.child(CurrentUserid + ".jpg"); //creating a node with user name

            //Now putting the image on the storage
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SetupActivity.this, "Picture is added to storage", Toast.LENGTH_SHORT).show();

                        final String downloadUrl = task.getResult().getDownloadUrl().toString();//getting that image link situated in storage

                        //putting that image in database under user node with the name ProfileImage
                        UsersRef.child("profileimage").setValue(downloadUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(SetupActivity.this, "Picture is added", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        } else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(SetupActivity.this, "Error Occured" + " " + message, Toast.LENGTH_LONG).show();
                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                    }
                }
            });
        }

    }

//        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
//
//    {
//        CropImage.ActivityResult result = CropImage.getActivityResult(data);//croping in proccess
//
//        if (resultCode == RESULT_OK) //if it is true then the pic is croped
//        {
//
//            loadingbar.setTitle("Updating Profile Image");
//            loadingbar.setMessage("Please wait while we are updating your profile image");
//            loadingbar.setCanceledOnTouchOutside(true);
//            loadingbar.show();
//
//
//            Uri resultUri = result.getUri(); //getting the croped image
//
//            final StorageReference filepath = UserProfileImageRef.child(CurrentUserid + ".jpg"); //creating a node with user name
//
//            //Now putting the image on the storage
//            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(SetupActivity.this, "Picture is added to storage", Toast.LENGTH_SHORT).show();
//
//                        final String downloadUrl = task.getResult().getDownloadUrl().toString();//getting that image link situated in storage
//
//                        //putting that image in database under user node with the name ProfileImage
//                        UsersRef.child("profileimage").setValue(downloadUrl)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//
//
//                                            Toast.makeText(SetupActivity.this, "Picture is added", Toast.LENGTH_SHORT).show();
//                                            loadingbar.dismiss();
//                                        } else {
//                                            String message = task.getException().getMessage();
//                                            Toast.makeText(SetupActivity.this, "Error Occured" + " " + message, Toast.LENGTH_LONG).show();
//                                            loadingbar.dismiss();
//                                        }
//                                    }
//                                });
//                    }
//                }
//            });
//        }
//    }
//        else
//
//    {
//        Toast.makeText(SetupActivity.this, "Try again!!!! Or Select another photo.", Toast.LENGTH_LONG).show();
//        loadingbar.dismiss();
//    }
//}




//saving other information on database

    private void SaveAccountSetupInformation()
    {

        String username = FullName.getText().toString();
        String roll = Roll.getText().toString();
        String phone_number = PhoneNumber.getText().toString();
        String about_user = AboutUser.getText().toString();
        String series = Series.getText().toString();


//        //getting email from the main activity
//        Intent intent = getIntent();
//        email = intent.getExtras().getString("key");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {

            //geeting user email
            email = currentUser.getEmail();


        }


        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(roll))
        {
            Toast.makeText(this,"Please enter your roll",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone_number))
        {
            Toast.makeText(this,"Please enter your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(about_user))
        {
            Toast.makeText(this,"Please write something about you",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(series))
        {
            Toast.makeText(this,"Please enter your series",Toast.LENGTH_SHORT).show();
        }

        else if(!checkProfilePic)
        {

            Toast.makeText(SetupActivity.this, "Please select a profile picture", Toast.LENGTH_LONG).show();

        }
        else
        {

            loadingbar.setTitle("Creating Profile");
            loadingbar.setMessage("Please wait while we are creating your profile");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);


            HashMap userMap = new HashMap();
            userMap.put("userfullname",username);
            userMap.put("roll",roll);
            userMap.put("series",series);
            userMap.put("phonenumber",phone_number);
            userMap.put("aboutuser",about_user);
            userMap.put("email",email);

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your Profile is created successfully", Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error Occured" + " " + message, Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                }
            });

        }


    }

    private void SendUserToMainActivity()
    {

        Intent mainactivityIntent = new Intent(SetupActivity.this,MainActivity.class);
        mainactivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainactivityIntent);
        finish();
    }
}