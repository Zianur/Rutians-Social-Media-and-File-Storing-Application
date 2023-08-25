package com.ruet.ruetians;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LikesAndComments extends AppCompatActivity {

    private ImageButton likeImageButton, dislikeImageButton, sendImageButton;
    private EditText commentEditText;
    private TextView likedTextView, dislikedTextView;
    private RecyclerView commentRecylerView;
    private Toolbar mtoolbar;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;
    private DatabaseReference likeRef, dislikeRef, commentRef, userRef;
    private AdapterLikesAndComments adapterLikesAndComments;
    private ArrayList<LikesAndCommentsModule> likesAndCommentsModuleArrayList;
    private String currentUserId, postKey, commentText, savecurrentdate, savecurrentTime, postrandomName;
    private int  likeCounter, disLikeCounter;
    private Boolean likeChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_and_comments);

        //getting the post key from the previous activity
        Intent intent = getIntent();
        postKey = intent.getExtras().getString("key");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        dislikeRef = FirebaseDatabase.getInstance().getReference().child("Dislikes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");




        likeImageButton = (ImageButton) findViewById(R.id.like_image_button);
        dislikeImageButton = (ImageButton) findViewById(R.id.dislike_image_button);
        sendImageButton = (ImageButton) findViewById(R.id.comment_image_button);
        likedTextView = (TextView) findViewById(R.id.liked_count);
        dislikedTextView = (TextView) findViewById(R.id.disliked_count);
        commentEditText = (EditText) findViewById(R.id.comment_box_input);
        commentRecylerView = (RecyclerView) findViewById(R.id.comments_list);
        commentRecylerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentRecylerView.setLayoutManager(linearLayoutManager);

        loadingbar = new ProgressDialog(this);

        likesAndCommentsModuleArrayList = new ArrayList<>();


        mtoolbar = (Toolbar) findViewById(R.id.likes_comments_home_bar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Likes and Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        ShowLikeAndDislikeCount();

        DisplayAllComments();




        likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dislikeRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(currentUserId))
                        {
                            dislikeRef.child(postKey).child(currentUserId).removeValue();

                            likeRef.child(postKey).child(currentUserId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(LikesAndComments.this, "You liked this post", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            likeRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {

                                    if (dataSnapshot.hasChild(currentUserId))
                                    {
                                        likeRef.child(postKey).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                                Toast.makeText(LikesAndComments.this, "Like is removed", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                    else
                                    {
                                        likeRef.child(postKey).child(currentUserId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(LikesAndComments.this, "You liked this post", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    }


                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });


        dislikeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                likeRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.hasChild(currentUserId))
                        {
                            likeRef.child(postKey).child(currentUserId).removeValue();
                            dislikeRef.child(postKey).child(currentUserId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(LikesAndComments.this, "You disliked this post", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            dislikeRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {

                                    if (dataSnapshot.hasChild(currentUserId))
                                    {
                                        dislikeRef.child(postKey).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(LikesAndComments.this, "Dislike is removed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        dislikeRef.child(postKey).child(currentUserId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(LikesAndComments.this, "You disliked this post", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }


                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateComment();

            }
        });



    }

    private void DisplayAllComments()
    {
        ClearAll();

        Query query = commentRef.child(postKey);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    LikesAndCommentsModule likesAndCommentsModule = new LikesAndCommentsModule();

                    likesAndCommentsModule.setUserfullname(snapshot.child("userfullname").getValue().toString());
                    likesAndCommentsModule.setEmail(snapshot.child("email").getValue().toString());
                    likesAndCommentsModule.setComment(snapshot.child("comment").getValue().toString());
                    likesAndCommentsModule.setProfileimage(snapshot.child("profileimage").getValue().toString());

                    likesAndCommentsModuleArrayList.add(likesAndCommentsModule);
                }

                adapterLikesAndComments = new AdapterLikesAndComments(LikesAndComments.this,likesAndCommentsModuleArrayList);
                commentRecylerView.setAdapter(adapterLikesAndComments);
                adapterLikesAndComments.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ClearAll()
    {
        if (likesAndCommentsModuleArrayList != null)
        {
            likesAndCommentsModuleArrayList.clear();

            if (adapterLikesAndComments != null)
            {
                adapterLikesAndComments.notifyDataSetChanged();
            }
        }

    }

    private void ValidateComment()
    {

        commentText = commentEditText.getText().toString();

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        savecurrentTime = currentTime.format(callForTime.getTime());

        postrandomName = savecurrentdate + savecurrentTime;


        if (TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Enter your comment", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingbar.setTitle("Uploading Comment");
            loadingbar.setMessage("Please wait while we are uploading your comment");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        String UserfullName = dataSnapshot.child("userfullname").getValue().toString();
                        String UserEmail = dataSnapshot.child("email").getValue().toString();
                        String UserProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                        HashMap commentMap = new HashMap();
                        commentMap.put("uid", currentUserId);
                        commentMap.put("date", savecurrentdate);
                        commentMap.put("time", savecurrentTime);
                        commentMap.put("comment", commentText);
                        commentMap.put("profileimage", UserProfileImage);
                        commentMap.put("userfullname", UserfullName);
                        commentMap.put("email", UserEmail);

                        String uniqueKey = commentRef.child(postKey).push().getKey(); //getting a unique key

                        commentRef.child(postKey).child(postrandomName + UserfullName + uniqueKey).updateChildren(commentMap)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful())
                                        {
                                            //This for self intent on behalf of recreate method and the only thing woorking for this situation
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);

                                            commentEditText.setText("");//clearing comment box

                                            Toast.makeText(LikesAndComments.this, "Your comment is updated successfully", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        }
                                        else
                                        {
                                            Toast.makeText(LikesAndComments.this, "Error: Could not update your comment", Toast.LENGTH_SHORT).show();
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
    }

    private void ShowLikeAndDislikeCount()
    {
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                likeCounter = (int) dataSnapshot.getChildrenCount();
                likedTextView.setText(likeCounter + " liked");

                if (dataSnapshot.hasChild(currentUserId))
                {
                    likeImageButton.setImageResource(R.drawable.like_black);//Changing the like image
                }
                else
                {
                    likeImageButton.setImageResource(R.drawable.like_white);
                }
               


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //For dislike

        dislikeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                disLikeCounter = (int) dataSnapshot.getChildrenCount();
                dislikedTextView.setText(disLikeCounter + " disliked");

                if (dataSnapshot.hasChild(currentUserId))
                {
                    dislikeImageButton.setImageResource(R.drawable.unlike_black);//Changing the like image
                }
                else
                {
                    dislikeImageButton.setImageResource(R.drawable.unlike_white);//Changing the like image
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {

            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }


}