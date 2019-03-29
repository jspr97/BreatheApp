package com.example.breatheapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private EditText editTaskName;
    private TextView textDate, textTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_AddTask_Title_Collapsed);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_AddTask_Title_Expanded);
        collapsingToolbar.setTitle(" ");

        appBar = findViewById(R.id.appBar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scroll = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scroll == -1) {
                    scroll = appBarLayout.getTotalScrollRange();
                }
                if (scroll + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Add Activity");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save task details
                String name = editTaskName.getText().toString();
                if (name.isEmpty()) {
                    Snackbar.make(view, "Task name is required", Snackbar.LENGTH_SHORT).show();
                } else {
                    String date = textDate.getText().toString();
                    String time = textTime.getText().toString();

                    // check if date or time is not set
                    if (date.equals("-")) {
                        date = null;
                    }
                    if (time.equals("-")) {
                        time = null;
                    }

                    Intent intent = new Intent();
                    intent.putExtra("name", name);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        editTaskName = findViewById(R.id.taskName);
        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);
    }

    public void onClickDate(View v) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // display date picker popup
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                textDate.setText(sdf.format(calendar.getTime()));
            }
        }, year, month, day);

        if (!dialog.isShowing())
            dialog.show();
    }

    public void onClickTime(View v) {
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR);
        final int min = calendar.get(Calendar.MINUTE);

        // display time picker popup
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String period;
                if (hourOfDay < 12 || hourOfDay == 0)
                    period = "AM";
                else
                    period = "PM";
                if (hourOfDay == 0)
                    hourOfDay = 12;
                else
                    hourOfDay %= 12;
                textTime.setText(hourOfDay + ":" + minute + " " + period);
            }
        }, hour, min, false);

        if (!dialog.isShowing())
            dialog.show();
    }
}
