package com.example.breatheapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("pref", 0);

        // if already not logged in
        if (pref.getBoolean("login", false)) {
            startActivityForResult(new Intent(this, MainActivity.class), REQUEST_CODE);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
