package com.example.yetanotherfitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.YafaApplication;
import com.example.yetanotherfitapp.user_account.FavouriteExerciseFragment;
import com.example.yetanotherfitapp.user_account.LoadedExerciseFragment;
import com.example.yetanotherfitapp.user_account.StatisticFragment;
import com.example.yetanotherfitapp.user_account.ExerciseFragment;
import com.example.yetanotherfitapp.user_account.ExerciseListFragment;
import com.example.yetanotherfitapp.user_account.ProfileFragment;
import com.google.firebase.auth.FirebaseUser;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ExerciseListFragment.OnExListStateChangedListener {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        mToolbar = findViewById(R.id.toolbarNavDrawer);
        setSupportActionBar(mToolbar);
        mToolbar.setVisibility(View.VISIBLE);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser user = YafaApplication.from(this).getAuth().getCurrentUser();
        String userMail = user.getEmail();
        String userName = user.getDisplayName();

        View headerView = navigationView.getHeaderView(0);
        TextView userNameView = headerView.findViewById(R.id.userName);
        TextView userMailView = headerView.findViewById(R.id.userMail);

        userNameView.setText(userName);
        userMailView.setText(userMail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.container, new ExerciseListFragment()).commit();
        }

        headerView.findViewById(R.id.goToProfileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new ProfileFragment())
                        .addToBackStack(null)
                        .commit();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                //mToolbar.setVisibility(View.GONE);
            }
        });
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.exerciseList) {
            if (!(currentFragment instanceof ExerciseListFragment)) {
                fragmentTransaction
                        .replace(R.id.container, new ExerciseListFragment())
                        .addToBackStack(null);
            }
        } else if (id == R.id.favourite) {
            if (!(currentFragment instanceof FavouriteExerciseFragment)) {
                fragmentTransaction
                        .replace(R.id.container, new FavouriteExerciseFragment())
                        .addToBackStack(null);
            }
        } else if (id == R.id.loaded) {
            if (!(currentFragment instanceof LoadedExerciseFragment)) {
                fragmentTransaction
                        .replace(R.id.container, new LoadedExerciseFragment())
                        .addToBackStack(null);
            }
        } else if (id == R.id.statistic) {
            fragmentTransaction.replace(R.id.container, new StatisticFragment())
                    .addToBackStack(null);
        } else if (id == R.id.signOut) {
            signOut();
        }
        mToolbar.setVisibility(View.VISIBLE);
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        YafaApplication.from(this).getAuth().signOut();
        Toast.makeText(this, getResources().getString(R.string.user_sign_out), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(NavDrawer.this, EntryActivity.class));
        finish();
    }

    @Override
    public void goToExercise(String exerciseId, String title) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, ExerciseFragment.newInstance(exerciseId, title)).
                addToBackStack(null).
                commit();
    }

    @Override
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void showFail(String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

}
