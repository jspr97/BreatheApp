package com.example.breatheapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private EditText editTaskName;
    private TextView textDate, textTime;
    private LinearLayout dateSelect;
    private FirebaseFirestore db;
    private String selectedDate, selectedTime;

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
                String name = editTaskName.getText().toString().trim();
                if (name.isEmpty()) {
                    Snackbar.make(view, "Task name is required", Snackbar.LENGTH_SHORT).show();
                } else {
                    Task newTask = new Task(name, selectedDate, selectedTime, "user", false);
                    CollectionReference ref = FirebaseFirestore.getInstance().collection("tasks");
                    ref.add(newTask)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i(TAG, "Added to database");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "Fail to add");
                                }
                            });
                    finish();
                }
            }
        });

        // initialize fields
        editTaskName = findViewById(R.id.taskName);
        dateSelect = findViewById(R.id.dateSelect);
        textDate = findViewById(R.id.textDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        selectedDate = sdf.format(Calendar.getInstance().getTime()); // default today's date
        textDate.setText(selectedDate);
        textTime = findViewById(R.id.time);

        int requestCode = getIntent().getIntExtra("request_code", -1);
        if (requestCode == CalendarActivity.REQUEST_ADD_TASK){
            textDate.setText(getIntent().getStringExtra("date"));
            dateSelect.setEnabled(false);
        } else if (requestCode == TodoFragment.REQUEST_ADD_TASK) {

        }

        // database
        db = FirebaseFirestore.getInstance();
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
                calendar.set(year, month, dayOfMonth);
                selectedDate = sdf.format(calendar.getTime());
                textDate.setText(selectedDate);
            }
        }, year, month, day);

        if (!dialog.isShowing())
            dialog.show();
    }

    public void onClickTime(View v) {
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR);
        final int min = calendar.get(Calendar.MINUTE);

        // display time picker popup
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                calendar.set(Calendar.HOUR, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                selectedTime = sdf.format(calendar.getTime());

                // display 12hr format to user
                sdf.applyPattern("hh:mm a");
                textTime.setText(sdf.format(calendar.getTime()));
            }
        }, hour, min, false);

        if (!dialog.isShowing())
            dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
