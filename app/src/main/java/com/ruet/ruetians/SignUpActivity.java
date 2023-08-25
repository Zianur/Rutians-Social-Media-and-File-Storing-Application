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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreatAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        UserEmail = (EditText) findViewById(R.id.signup_email);
        UserPassword = (EditText) findViewById(R.id.signup_password);
        UserConfirmPassword = (EditText) findViewById(R.id.signup_confirmpassword);
        CreatAccountButton = (Button) findViewById(R.id.signup_button);

       loadingbar = new ProgressDialog(this);


       CreatAccountButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {

               CreateNewAccount();
           }
       });


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

    private void SendUserToMainActivity()
    {

        Intent mainactivityIntent = new Intent(SignUpActivity.this,MainActivity.class);
        mainactivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainactivityIntent);
        finish();
    }

    private void CreateNewAccount()
    {
        email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmpassword = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmpassword))
        {
            Toast.makeText(this,"Please confirm your password",Toast.LENGTH_SHORT).show();
        }

        else if(!password.equals(confirmpassword))
        {
            Toast.makeText(this,"Password Mismatched",Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingbar.setTitle("Creating Account");
            loadingbar.setMessage("Please wait while we are creating your account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);


            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {

                                sendEmailVerificationmessage();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(SignUpActivity.this, "Error Occured" + " " + message, Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();
                            }

                        }
                    });
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
                        Toast.makeText(SignUpActivity.this, "We have send an email to verify your account\nPlease check your email", Toast.LENGTH_LONG).show();
                        SendUserToLoginActivity();
                        mAuth.signOut();
                    }
                    else
                    {
                        String error = task.getException().getMessage();
                        Toast.makeText(SignUpActivity.this, "Error " + error, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }

                }
            });
        }
    }

    private void SendUserToLoginActivity()
    {

        Intent loginIntent = new Intent(SignUpActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }
}