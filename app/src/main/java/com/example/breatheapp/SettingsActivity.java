package com.example.breatheapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat notificationOption,reminderOption;
    boolean notificationState,reminderState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences preferences=getSharedPreferences("PREFS",0);
        notificationState=preferences.getBoolean("notification",TRUE);
        reminderState=preferences.getBoolean("reminder",TRUE);
        notificationOption=findViewById(R.id.option_notification);
        reminderOption=findViewById(R.id.option_reminder);

        notificationOption.setChecked(notificationState);
        reminderOption.setChecked(reminderState);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        notificationOption.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                notificationState=!notificationState;
                notificationOption.setChecked(notificationState);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("notification",notificationState);
                editor.apply();
            }
        });

        reminderOption.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reminderState=!reminderState;
                reminderOption.setChecked(reminderState);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("reminder",reminderState);
                editor.apply();
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
