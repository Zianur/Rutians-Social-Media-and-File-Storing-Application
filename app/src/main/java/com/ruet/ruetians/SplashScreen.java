package com.ruet.ruetians;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    ProgressBar simpleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);


        //        CheckConnection();

        new Thread(new Runnable() {

            public void run() {
                setProgressValue();
                SendToLoginActivity();
                finish();
            }
        }).start();


    }

    //    public void CheckConnection() {
//
//        //using the connectivty manager to get service
//        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            Network activeNetwork = manager.getActiveNetwork();
//
//            if(activeNetwork == null)
//            {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
//                builder.setTitle("Please check your network connection");
//                builder.setMessage("Turn on wifi or data connection and restart the app :p");
//                Dialog dialog = builder.create();
//                dialog.show();
//
//
//            }
//
//            else
//            {
//
//                new Thread(new Runnable() {
//                    public void run() {
//                        setProgressValue();
//                        SendToLoginActivity();
//                        finish();
//                    }
//                }).start();
//
//            }
//        }
//        else
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
//            builder.setTitle("Please check your Android Version");
//            builder.setMessage("You need to upgrade your Android Version to use this app.Sorry :)");
//            Dialog dialog = builder.create();
//            dialog.show();
//        }
//
//    }



    private void setProgressValue() {

        for ( int progress=0; progress<100; progress+=25) {
            try {
                Thread.sleep(1000);
                simpleProgressBar.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void SendToLoginActivity()
    {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }
}