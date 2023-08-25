package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddFileActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private EditText fileName;
    private Button selectFileButton, uploadFileButton;
    private ProgressDialog loadingbar;
    private StorageReference fileRef;
    private DatabaseReference filePostRef, userRef;
    private FirebaseAuth mAuth;
    private Uri fileUri;

    private String savecurrentdate, savecurrentTime, postrandomName, currentUser_id, UserfullName, UserEmail;

    final static int PICK_PDF_CODE = 2342;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);


        mAuth = FirebaseAuth.getInstance();
        currentUser_id = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        fileRef = FirebaseStorage.getInstance().getReference().child("File");
        filePostRef = FirebaseDatabase.getInstance().getReference().child("File");

        fileName = (EditText) findViewById(R.id.editTextFileName);
        selectFileButton = (Button) findViewById(R.id.buttonselectFile);
        uploadFileButton = (Button) findViewById(R.id.buttonUploadFile);

        loadingbar = new ProgressDialog(this);




        mtoolbar = (Toolbar) findViewById(R.id.chotha_add_file_home_bar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Add File");//setting actionbar name
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPDF();

            }
        });



        userRef.child(currentUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                     UserfullName = dataSnapshot.child("userfullname").getValue().toString();
                     UserEmail = dataSnapshot.child("email").getValue().toString();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateMemeDescription();
            }
        });

    }

    private void ValidateMemeDescription()
    {

        if(fileUri == null)
        {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(fileName.getText().toString()))
        {
            Toast.makeText(this, "Please write file name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Uploading File");
            loadingbar.setMessage("Please wait while we are uploading your file.This will take a few minute");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            uploadFile();
        }

    }

    private void getPDF()
    {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //showing dialogue to get storage permission
            AlertDialog.Builder builder = new AlertDialog.Builder(AddFileActivity.this);
            builder.setTitle("Storage permission needed! :p");

            builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    return;

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
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);

        }

        else
        {
            //creating an intent for file chooser
            Intent intent = new Intent();
            intent.setType("application/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {

                fileUri = data.getData();

                Toast.makeText(AddFileActivity.this, "File is selected", Toast.LENGTH_SHORT).show();
            }
            else {

                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this method is uploading the file
    //the code is same as the previous tutorial
    //so we are not explaining it
    private void uploadFile()
    {


        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        savecurrentTime = currentTime.format(callForTime.getTime());

        postrandomName = savecurrentdate + savecurrentTime;
        StorageReference sRef = fileRef.child( fileName.getText().toString() + postrandomName + ".pdf");
        sRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        Toast.makeText(AddFileActivity.this, "File uploaded to storage", Toast.LENGTH_SHORT).show();


                        AddFileModule upload = new AddFileModule(UserfullName, UserEmail, fileName.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                        filePostRef.child(fileName.getText().toString() + postrandomName).setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {

                                    SendToChothaActivity();

                                    Toast.makeText(AddFileActivity.this, "New File is updated successfully", Toast.LENGTH_SHORT).show();

                                    loadingbar.dismiss();
                                }
                                else
                                {

                                    Toast.makeText(AddFileActivity.this, "Error: Could not update your File", Toast.LENGTH_SHORT).show();

                                    loadingbar.dismiss();
                                }

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void SendToChothaActivity()
    {
        Intent chothaIntent = new Intent(this,ChothaActivity.class);
        startActivity(chothaIntent);
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