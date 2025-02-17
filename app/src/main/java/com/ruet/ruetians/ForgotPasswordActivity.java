package com.ruet.ruetians;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button sendButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        resetEmail = (EditText) findViewById(R.id.reset_password_email);
        sendButton = (Button) findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendingEmail();

            }
        });

    }

    private void sendingEmail()
    {
        String userEmail = resetEmail.getText().toString();

        if (TextUtils.isEmpty(userEmail))
        {
            Toast.makeText(this, "Please Enter your Email address", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Please check your Email.....", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));

                    }
                    else
                    {
                        String error = task.getException().getMessage();
                        Toast.makeText(ForgotPasswordActivity.this, "Error " + error, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }


}