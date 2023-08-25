package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements AdapterCommonRoomPost.OnCommonRoomPostClickListener {

    private DrawerLayout drawerLayout;
    private ImageButton addPostButton;
    private RecyclerView commonRoomPostRecylerView;
    private ArrayList<CommonRoomPostModule>commonRoomPostModuleArrayList;
    private AdapterCommonRoomPost adapterCommonRoomPost;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private Toolbar mtoolbar;
    private String CurrentUserid, postKey, description,email;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,postRef;
    private FirebaseStorage mStorage;
    private int MY_REQUEST_CODE =11;

   // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserid = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("CommonRoomPost");
        mStorage = FirebaseStorage.getInstance();


        drawerLayout =(DrawerLayout) findViewById(R.id.drawable_layout);
        addPostButton = (ImageButton) findViewById(R.id.commonroom_upper_add_post_button);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mtoolbar = (Toolbar) findViewById(R.id.mainpage_bar);

        commonRoomPostRecylerView = (RecyclerView) findViewById(R.id.common_room_post_list);
        commonRoomPostRecylerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commonRoomPostRecylerView.setLayoutManager(linearLayoutManager);

        commonRoomPostModuleArrayList = new ArrayList<>();


    // adding navigation header
        View navview = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navview.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navview.findViewById(R.id.nav_user_fullname);



        UsersRef.child(CurrentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if(snapshot.exists())
                {

                    if(snapshot.hasChild("userfullname"))
                    {
                        String fullname = snapshot.child("userfullname").getValue().toString();//getting the name from the database
                        //setting the full name
                        NavProfileUserName.setText(fullname);
                    }

                    if(snapshot.hasChild("profileimage"))
                    {
                        String image = snapshot.child("profileimage").getValue().toString();//getting the image from the database

                        //loading the image with picasso library into imageview
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile does not exist", Toast.LENGTH_LONG).show();
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//to use action bar toggle first we need to set actionbar
        //else we will get null pointer exception
        setSupportActionBar(mtoolbar); //setting actionbar
        getSupportActionBar().setTitle("Common Room");//setting actionbar name


        //to use action bar toggle first we need to set actionbar
        //else we will get null pointer exception
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                
                Usermenuselector(item);
                return false;
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserTOAddPostActivity();
            }
        });


        DisplayAllCommoRoomPost();


        autoUpdateChecker();







    }

    private void autoUpdateChecker()
    {
        // Creates instance of the manager.
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();


        appUpdateInfoTask.addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE));

                try {
                    appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this,MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    
//updating the app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE)
        {
            Toast.makeText(this, "Started Downloading....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Sorry there was an error\nPlease go to playstore and update the app :)", Toast.LENGTH_SHORT).show();
        }
    }

    private void DisplayAllCommoRoomPost()
    {

        ClearAll();

        Query query = postRef.limitToLast(50);

       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               for (DataSnapshot snapshot : dataSnapshot.getChildren())
               {
                   CommonRoomPostModule commonRoomPostModule = new CommonRoomPostModule();

                   commonRoomPostModule.setDate(snapshot.child("date").getValue().toString());
                   commonRoomPostModule.setTime(snapshot.child("time").getValue().toString());
                   commonRoomPostModule.setUserfullname(snapshot.child("userfullname").getValue().toString());
                   commonRoomPostModule.setEmail(snapshot.child("email").getValue().toString());
                   commonRoomPostModule.setDescription(snapshot.child("description").getValue().toString());
                   commonRoomPostModule.setPostimage(snapshot.child("postimage").getValue().toString());
                   commonRoomPostModule.setProfileimage(snapshot.child("profileimage").getValue().toString());

                   commonRoomPostModule.setPostKey(snapshot.getKey());

                   commonRoomPostModule.setUid(snapshot.child("uid").getValue().toString());

                   commonRoomPostModuleArrayList.add(commonRoomPostModule);
               }

               adapterCommonRoomPost = new AdapterCommonRoomPost(MainActivity.this,commonRoomPostModuleArrayList);
               commonRoomPostRecylerView.setAdapter(adapterCommonRoomPost);
               adapterCommonRoomPost.setOnCommonRoomPostClickListener(MainActivity.this);
               adapterCommonRoomPost.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

               Toast.makeText(MainActivity.this, "Sorry there was an error", Toast.LENGTH_SHORT).show();

           }
       });
    }

    private void ClearAll()
    {
        if (commonRoomPostModuleArrayList != null)
        {
            commonRoomPostModuleArrayList.clear();

            if (adapterCommonRoomPost != null)
            {
                adapterCommonRoomPost.notifyDataSetChanged();
            }
        }


        commonRoomPostModuleArrayList = new ArrayList<>();
    }


    private void SendUserTOAddPostActivity()
    {
        Intent commonRoomPostIntent = new Intent(this,CommonPostActivity.class);
        startActivity(commonRoomPostIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null)
        {
            SendUserToLoginActivity();//checking user authentication
        }
        else
        {
            CheckUserExistence();//checking profile is created or not
        }
    }

    private void CheckUserExistence()
    {
        final String current_User_id = mAuth.getCurrentUser().getUid();
        //if user has created profile then authenticated unique code wil be stored under user node in database

        //getting the email from the main activity
//        Intent intent = getIntent();
//        email = intent.getExtras().getString("key");

        UsersRef.child(current_User_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild("profileimage"))//the user is authenticated but does not have a profile
                {
                    SendUserToSetupActivity();//so send to setup profile
                    Toast.makeText(MainActivity.this, "Please Complete your Profile... :)", Toast.LENGTH_LONG).show();
                }
                if(!dataSnapshot.hasChild("userfullname"))//the user is authenticated but does not have a profile
                {
                    SendUserToSetupActivity();//so send to setup profile
                    Toast.makeText(MainActivity.this, "Please Complete your Profile... :)", Toast.LENGTH_LONG).show();
                }
                if(!dataSnapshot.hasChild("roll"))//the user is authenticated but does not have a profile
                {
                    SendUserToSetupActivity();//so send to setup profile
                    Toast.makeText(MainActivity.this, "Please Complete your Profile... :)", Toast.LENGTH_LONG).show();
                }
                if(!dataSnapshot.hasChild("series"))//the user is authenticated but does not have a profile
                {
                    SendUserToSetupActivity();//so send to setup profile
                    Toast.makeText(MainActivity.this, "Please Complete your Profile... :)", Toast.LENGTH_LONG).show();
                }
                if(!dataSnapshot.hasChild("phonenumber"))//the user is authenticated but does not have a profile
                {
                    SendUserToSetupActivity();//so send to setup profile
                    Toast.makeText(MainActivity.this, "Please Complete your Profile... :)", Toast.LENGTH_LONG).show();
                }
                if(!dataSnapshot.hasChild("aboutuser"))//the user is authenticated but does not have a profile
                {
                    SendUserToSetupActivity();//so send to setup profile
                    Toast.makeText(MainActivity.this, "Please Complete your Profile... :)", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SendUserToSetupActivity()//method to send user to setupactivity
    {
        Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
//        setupIntent.putExtra("key", email );
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }


    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void Usermenuselector(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.meme_home:

                SendUserTOMemeActvity();

                break;

            case R.id.confession_home:

                SendUserTOConfessionActvity();

                break;

            case R.id.log_out:

                mAuth.signOut();
                SendUserToLoginActivity();

                break;

            case R.id.edit_profile:


                SendUserToEditProfileActivity();

//                try{
//                    SendUserToEditProfileActivity();
//                }
//                catch (Exception e)
//                {
//                    Toast.makeText(this, "You havent set your profile correctly contact developer", Toast.LENGTH_LONG).show();
//                }

                break;

            case R.id.search_profile:

                   SendUserToSearchActivity();

                break;

            case R.id.chotha:

                SendUserToChothaActivity();

                break;

            case R.id.station:

                SendUserToGetHelpActivity();

                break;

            case R.id.about_developer:

                SendUserToAboutDeveloperActivity();

                break;

            case R.id.ruet_website:

                SentTORuetWeb();

                break;

            case R.id.ruetians_website:

                SentTORuetiansWeb();

                break;

        }

    }

    private void SentTORuetiansWeb()
    {
        Intent ruetiansWebIntent = new Intent(this, RuetiansWebActivity.class);
        startActivity(ruetiansWebIntent);
    }

    private void SentTORuetWeb()
    {
        Intent ruetWebIntent = new Intent(this, RuetWebActivity.class);
        startActivity(ruetWebIntent);
    }

    private void SendUserToAboutDeveloperActivity()
    {
        Intent aboutDeveloperIntent = new Intent(this, AboutDeveloperActivity.class);
        startActivity(aboutDeveloperIntent);

    }

    private void SendUserToGetHelpActivity()
    {
        Intent getHelpIntent = new Intent(this,GetHelpActivity.class);
        startActivity(getHelpIntent);
    }

    private void SendUserToChothaActivity()
    {
        Intent chothaIntent = new Intent(this,ChothaActivity.class);
        startActivity(chothaIntent);
    }

    private void SendUserToSearchActivity()
    {
        Intent searchIntent = new Intent(MainActivity.this,SearchActivity.class);
        startActivity(searchIntent);
    }

    private void SendUserToEditProfileActivity()
    {
        Intent ConfessionIntent = new Intent(MainActivity.this,EditProfile.class);
        startActivity(ConfessionIntent);
    }

    private void SendUserTOConfessionActvity()
    {
        Intent ConfessionIntent = new Intent(MainActivity.this,ConfessionActivity.class);
        startActivity(ConfessionIntent);
    }

    private void SendUserTOMemeActvity()
    {
        Intent MemeIntent = new Intent(MainActivity.this,MemeActivity.class);
        startActivity(MemeIntent);
    }

    @Override
    public void OnCommonRoomPostClick(int position) {

        CommonRoomPostModule selected_post = commonRoomPostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

        postKey = selected_post.getPostKey(); //using the module and getter method just like in adapter

        Intent likesCommentsIntent = new Intent(this,LikesAndComments.class);
        likesCommentsIntent.putExtra("key",postKey);
        startActivity(likesCommentsIntent);

    }

    @Override
    public void OnCommonRoomPostEditClick(int position) {

        CommonRoomPostModule selected_post = commonRoomPostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

        postKey = selected_post.getPostKey(); //using the module and getter method just like in adapter

        final String postUid = selected_post.getUid(); //using the module and getter method just like in adapter

        description = selected_post.getDescription();

        if (postUid.equals(CurrentUserid))
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit your post");

        final EditText inputField = new EditText(MainActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                postRef.child(postKey).child("description").setValue(inputField.getText().toString());

                Intent selfIntent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(selfIntent);

                Toast.makeText(MainActivity.this, "Your post is updated", Toast.LENGTH_LONG).show();

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
    public void OnCommonRoomPostDeleteClick(int position) {

        CommonRoomPostModule selected_post = commonRoomPostModuleArrayList.get(position); //setting the position in selected meme same as in adapter

        postKey = selected_post.getPostKey(); //using the module and getter method just like in adapter

        final String postUid = selected_post.getUid(); //using the module and getter method just like in adapter

        if (postUid.equals(CurrentUserid))
        {
            StorageReference imageRef = mStorage.getReferenceFromUrl(selected_post.getPostimage());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    postRef.child(postKey).removeValue();

                    Intent selfIntent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(selfIntent);

                    Toast.makeText(MainActivity.this, "Post is deleted", Toast.LENGTH_LONG).show();

                }
            });

        }

        else
        {
            Toast.makeText(this, "You can not delete this post", Toast.LENGTH_LONG).show();
        }

    }
}