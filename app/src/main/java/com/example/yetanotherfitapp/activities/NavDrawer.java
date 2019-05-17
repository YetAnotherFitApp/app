package com.example.yetanotherfitapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.fragments.AboutFragment;
import com.example.yetanotherfitapp.fragments.ExerciseFragment;
import com.example.yetanotherfitapp.fragments.ExerciseListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FilenameFilter;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ExerciseListFragment.OnExListStateChangedListener {

    private String mUserName;
    private String mUserMail;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUserMail = user.getEmail();

        Log.d("testtt", mUserMail);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameView = headerView.findViewById(R.id.userName);
        TextView userMailView = headerView.findViewById(R.id.userMail);

        userNameView.setText(mUserMail.substring(0, mUserMail.indexOf('@')));
        userMailView.setText(mUserMail);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.container, new ExerciseListFragment()).commit();
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
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        if (id == R.id.exerciseList) {
            fragmentTransaction.replace(R.id.container, new ExerciseListFragment());
        } else if (id == R.id.exercise) {
            //Пока из этого места некуда перейти
        } else if (id == R.id.about) {
            fragmentTransaction.replace(R.id.container, new AboutFragment()).addToBackStack(null);
        } else if (id == R.id.signOut) {
            signOut();
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(NavDrawer.this, EntryActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToExercise(Exercise exercise) {
        mFragmentManager.beginTransaction().
                replace(R.id.container,
                        ExerciseFragment.newInstance(exercise.title, exercise.imageName, exercise.description)).
                addToBackStack(null).
                commit();
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public File getFileByName(final String name) {
        File[] files = getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String n) {
                return n.equals(name);
            }
        });
        return files.length == 0 ? null : files[0];
    }

    @Override
    public void deleteFileByName(String name) {
        deleteFile(name);
    }

    @Override
    public void showFail(String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
    }

}
