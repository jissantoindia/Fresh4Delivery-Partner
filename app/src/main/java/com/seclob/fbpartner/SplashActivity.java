package com.seclob.fbpartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String userName="",upass="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("SECGRO",MODE_PRIVATE);
        userName=sharedPreferences.getString("uName","");
        upass=sharedPreferences.getString("uPass","");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(userName.length()>=3 && upass.length()>=3)
                {
                    Intent intents = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intents);
                    finish();
                }
                else
                {
                    Intent intents = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intents);
                    finish();
                }

            }
        }, 3000);

    }
}