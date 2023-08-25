package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView Neednewaccountlink, getHelpTextView, forgotPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private String email;
    private Boolean emailAddressChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Neednewaccountlink = (TextView) findViewById(R.id.login_textview);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_button);
        getHelpTextView = (TextView) findViewById(R.id.get_help_textview);
        forgotPassword = (TextView) findViewById(R.id.forgot_textview);


        loadingbar = new ProgressDialog(this);


        Neednewaccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToSignupActivity();
            }
        });

        getHelpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendToGetHelpActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserTOLogin();
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToForgotPasswordActivity();
            }
        });




    }

    private void sendToForgotPasswordActivity()
    {
        Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotPasswordIntent);
    }


    private void SendToGetHelpActivity()
    {
        Intent getHelpIntent = new Intent(this, GetHelpActivity.class);
        startActivity(getHelpIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
        {
            SendUserToMainActivity();
        }
    }

    private void AllowUserTOLogin()
    {
         email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingbar.setTitle("Logging In");
            loadingbar.setMessage("Please wait while we are Logging in your account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);


            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                verifyEmailAddress();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error " + " " + message, Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();
                            }

                        }
                    });
        }
    }

    private void SendUserToMainActivity()
    {

        Intent mainactivityIntent = new Intent(LoginActivity.this,MainActivity.class);
//        mainactivityIntent.putExtra("key", email );
        mainactivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainactivityIntent);
        finish();
    }



    private void verifyEmailAddress()
    {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        emailAddressChecker = currentUser.isEmailVerified();
        
        if (emailAddressChecker)
        {
            SendUserToMainActivity();
            Toast.makeText(LoginActivity.this,"You are Logged in Successfully",Toast.LENGTH_SHORT).show();
        }
        else 
        {
            sendEmailVerificationmessage();
        }

    }

    private void sendEmailVerificationmessage()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {

            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "We have send an email to verify your account\nPlease check your email", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                    else
                    {
                        String error = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error " + error, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }

                }
            });
        }
    }

    private void SendUserToSignupActivity()
    {

        Intent SignupIntent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(SignupIntent);
    }


}