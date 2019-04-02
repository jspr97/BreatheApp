package com.example.breatheapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 9;

    private CalendarView calendarView;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // format selected date to string
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                selectedDate = sdf.format(calendar.getTime());
            }
        });

        FloatingActionButton addTask = findViewById(R.id.btnAddTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, AddTaskActivity.class);
                intent.putExtra("request_code", REQUEST_ADD_TASK);
                intent.putExtra("date", selectedDate);
                startActivityForResult(intent, REQUEST_ADD_TASK);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}