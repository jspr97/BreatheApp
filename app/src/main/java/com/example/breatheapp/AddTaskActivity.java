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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";
    private static final String[] EMAILS = new String[]{
            "lawrenceykj97@gmail.com","alvinlow10288558@gmail.com"};
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private EditText editTaskName;
    private TextView textDate, textTime;
    private LinearLayout dateSelect;
    private FirebaseFirestore db;
    private String selectedDate, selectedTime;
    private int requestCode;
    private String taskId;
    private MultiAutoCompleteTextView textEmails;
    private String users;

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

        // database
        db = FirebaseFirestore.getInstance();

        // initialize fields
        editTaskName = findViewById(R.id.taskName);
        dateSelect = findViewById(R.id.dateSelect);
        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.time);
        textEmails = findViewById(R.id.emailsTextView);
        textEmails.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, EMAILS));
        textEmails.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        textEmails.setThreshold(1);

        // check previous activity
        requestCode = getIntent().getIntExtra("requestCode", -1);
        if (requestCode == CalendarActivity.REQUEST_ADD_TASK){
            // set calendar selected date
            selectedDate = getIntent().getStringExtra("date");
            textDate.setText(selectedDate);
            dateSelect.setEnabled(false);
        } else if (requestCode == TodoFragment.REQUEST_ADD_TASK) {
            // set today's date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            selectedDate = sdf.format(Calendar.getInstance().getTime());
            textDate.setText(selectedDate);
        } else if (requestCode == TodoFragment.REQUEST_EDIT_TASK || requestCode == TodoFragment.REQUEST_EDIT_TASK_SHARED) {
            // put existing task details
            taskId = getIntent().getStringExtra("id");
            String collectionPath = (requestCode == TodoFragment.REQUEST_EDIT_TASK) ? "tasks" : "sharedTasks";
            db.collection(collectionPath).document(taskId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            Task existingTask = documentSnapshot.toObject(Task.class);
                            editTaskName.setText(existingTask.getName());
                            selectedDate = existingTask.getDate();
                            textDate.setText(selectedDate);
                            if (existingTask.getDate() != null) {
                                selectedTime = existingTask.getDate();
                                // parse and convert to 12hr format
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = sdf.parse(selectedTime);
                                    sdf.applyPattern("hh:mm a");
                                    textTime.setText(sdf.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (existingTask.getUsers().size() > 1) {
                                String usersString = "";
                                for (int i=1; i<existingTask.getUsers().size(); i++) {
                                    usersString += existingTask.getUsers().get(i) + ", ";
                                }
                                textEmails.setText(usersString.substring(0,usersString.length()-2));
                            }
                        }
                    }
                }
            });
        }

        users = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // save button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String name = editTaskName.getText().toString().trim();
                String emailsString = textEmails.getText().toString().trim();
                String[] emailArray = new String[]{};

                // check valid email
                if (!emailsString.isEmpty()) {
                    emailArray = emailsString.split("\\s*,\\s*");  // split by comma
                    for (String s : emailArray) {
                        if (!s.matches("[a-z0-9._%+-]+@gmail.com")) {
                            Snackbar.make(view, "Invalid email(s)", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                ArrayList<String> users = new ArrayList<>();
                users.add(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                users.addAll(Arrays.asList(emailArray));

                String collectionPath = users.size()>1 ? "sharedTasks" : "tasks";

                // check task name is empty
                if (name.isEmpty()) {
                    Snackbar.make(view, "Task name is required", Snackbar.LENGTH_SHORT).show();
                } else if (requestCode == TodoFragment.REQUEST_EDIT_TASK) {
                    // update task
                    DocumentReference taskRef = db.collection(collectionPath).document(taskId);
                    taskRef.update("name", name, "date", selectedDate, "time",
                            selectedTime, "users", users)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    // save as new task
                    Task newTask = new Task(name, selectedDate, selectedTime, false, users);
                    CollectionReference ref = FirebaseFirestore.getInstance().collection(collectionPath);
                    ref.add(newTask)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
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
