package com.example.yetanotherfitapp.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.fragments.EntryFragment;
import com.example.yetanotherfitapp.fragments.RegistrationFragment;
import com.example.yetanotherfitapp.fragments.SignInFragment;
import com.google.firebase.auth.FirebaseAuth;

//TODO: Add design
//TODO: Add mail check
//TODO: Add multithreading
//TODO: Добавить обработку профиля пользователя при успешном входе
//TODO: Обработка смены конфигурации!
//TODO: Clean Architecture

public class EntryActivity extends AppCompatActivity implements EntryFragment.OnAuthStateChangeListener {

    private enum mAuthState {
        ENTRY,
        SIGNIN,
        REG
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            success("Добро пожаловать!");
        } else {
            showAuthFragment(mAuthState.ENTRY);
        }
    }

    private void showAuthFragment(mAuthState state) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_login_container);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (state) {
            case ENTRY:
                fragment = new EntryFragment();
                break;
            case REG:
                fragment = new RegistrationFragment();
                break;
            case SIGNIN:
                fragment = new SignInFragment();
                break;
        }

        if (currentFragment == null) {
            transaction.add(R.id.fragment_login_container, fragment);
        } else {
            transaction.replace(R.id.fragment_login_container, fragment);
        }

        if (state != mAuthState.ENTRY) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void needReg() {
        showAuthFragment(mAuthState.REG);
    }

    @Override
    public void needSignIn() {
        showAuthFragment(mAuthState.SIGNIN);
    }

    @Override
    public void success(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(EntryActivity.this, ExerciseListActivity.class);
        Intent intent = new Intent(EntryActivity.this, NavDrawer.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void fail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

}
