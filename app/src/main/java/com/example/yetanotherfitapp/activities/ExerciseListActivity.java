package com.example.yetanotherfitapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.fragments.ExerciseFragment;
import com.example.yetanotherfitapp.fragments.ExerciseListFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FilenameFilter;

public class ExerciseListActivity extends AppCompatActivity implements ExerciseListFragment.OnExListChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_exercise_container, new ExerciseListFragment()).
                commit();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ExerciseListActivity.this, EntryActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToExercise(Exercise exercise) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_exercise_container,
                        ExerciseFragment.newInstance(exercise.title, exercise.imageName, exercise.description)).
                addToBackStack(null).
                commit();
    }

    @Override
    public SharedPreferences getPrefs(String prefsName) {
        return getSharedPreferences(prefsName, 0);
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

}
