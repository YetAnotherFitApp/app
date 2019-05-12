package com.example.yetanotherfitapp.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.fragments.EntryFragment;
import com.example.yetanotherfitapp.fragments.ExerciseFragment;
import com.example.yetanotherfitapp.fragments.RegistrationFragment;
import com.example.yetanotherfitapp.fragments.SignInFragment;
import com.google.firebase.auth.FirebaseAuth;

public class ExerciseListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        findViewById(R.id.signOutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        findViewById(R.id.goExBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExercise();
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ExerciseListActivity.this, EntryActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToExercise() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_exercise_container, new ExerciseFragment()).
                addToBackStack(null).
                commit();
    }
}
