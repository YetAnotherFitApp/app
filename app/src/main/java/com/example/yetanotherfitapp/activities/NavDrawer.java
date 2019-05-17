package com.example.yetanotherfitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.fragments.AboutFragment;
import com.example.yetanotherfitapp.fragments.ExerciseFragment;
import com.example.yetanotherfitapp.fragments.ExerciseListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ExerciseListFragment.OnExListChangedListener {

    ExerciseListFragment exerciseListFragment;
    ExerciseFragment exerciseFragment;
    AboutFragment aboutFragment;
    String userName;
    String userMail;

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

        exerciseListFragment = new ExerciseListFragment();
        exerciseFragment = new ExerciseFragment();
        aboutFragment = new AboutFragment();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userMail = user.getEmail();

        Log.d("testtt", userMail);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameView = (TextView) headerView.findViewById(R.id.userName);
        TextView userMailView = (TextView) headerView.findViewById(R.id.userMail);

        userNameView.setText(userMail.substring(0, userMail.indexOf('@')));
        userMailView.setText(userMail);


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, exerciseListFragment);
        fragmentTransaction.commit();

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

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(NavDrawer.this, EntryActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToExercise() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, new ExerciseFragment()).
                addToBackStack(null).
                commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.exerciseList) {
            fragmentTransaction.replace(R.id.container, exerciseListFragment);

        } else if (id == R.id.exercise) {
            fragmentTransaction.replace(R.id.container, exerciseFragment);

        } else if (id == R.id.about) {
            fragmentTransaction.replace(R.id.container, aboutFragment);
        } else if (id == R.id.signOut) {
            signOut();
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
