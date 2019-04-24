package com.example.breatheapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TodoFragment.OnListFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener{

    private FirebaseFirestore db;
    private CollectionReference ref;
    private TextView textEmail, textUsername;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        // set up user details
        textEmail = header.findViewById(R.id.userEmail);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        textEmail.setText(user.getEmail());
        textUsername = header.findViewById(R.id.username);
        textUsername.setText(user.getDisplayName());
        profileImage = header.findViewById(R.id.userImage);
        String photoUrl = user.getPhotoUrl().toString();
        photoUrl = photoUrl.replace("s96-c", "s150-c");
        new DownloadImageTask(profileImage).execute(photoUrl);

        // home fragment on start
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragmentFrame, fragment).commit();

        setTitle("Home");

        // initialize firestore db
        initFirestore();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingPage=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingPage);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setTitle("Home");
            Fragment fragment = HomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentFrame, fragment).commit();
        } else if (id == R.id.nav_todo) {
            setTitle("To Do List");
            Fragment fragment = TodoFragment.newInstance(TodoFragment.MODE_SOLO);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentFrame, fragment).commit();
        } else if (id == R.id.nav_calendar) {
            Intent calendar = new Intent(this, CalendarActivity.class);
            startActivity(calendar);
        } else if (id == R.id.nav_shared) {
            setTitle("Shared Tasks");
            Fragment fragment = TodoFragment.newInstance(TodoFragment.MODE_SHARED);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentFrame, fragment).commit();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
        return true;
    }

    @Override
    public void onListFragmentInteraction(Task task) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initFirestore() {
        db = FirebaseFirestore.getInstance();
        ref = db.collection("tasks");
    }

    // set profile image
    private class DownloadImageTask extends AsyncTask<String,Void, Bitmap> {
        CircleImageView imageView;

        public DownloadImageTask(CircleImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(url).openStream();
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
