package com.example.yetanotherfitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.YafaApplication;
import com.example.yetanotherfitapp.fragments.EntryFragment;
import com.example.yetanotherfitapp.fragments.RegistrationFragment;
import com.example.yetanotherfitapp.fragments.SignInFragment;

//TODO: Add design
//TODO: Add mail check
//TODO: Add multithreading
//TODO: Обработка смены конфигурации!
//TODO: Добавить корректную обработку ошибок!
//TODO: Clean Architecture

public class EntryActivity extends AppCompatActivity implements EntryFragment.OnAuthStateChangeListener {

    private enum AuthState {
        ENTRY,
        SIGNIN,
        REG
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        if (savedInstanceState == null) {
            if (YafaApplication.from(this).getAuth().getCurrentUser() != null) {
                success(getString(R.string.welcome));
            } else {
                showAuthFragment(AuthState.ENTRY);
            }
        }
    }

    private void showAuthFragment(AuthState state) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
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

        transaction.replace(R.id.fragment_login_container, fragment);

        if (state != AuthState.ENTRY) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void needReg() {
        showAuthFragment(AuthState.REG);
    }

    @Override
    public void needSignIn() {
        showAuthFragment(AuthState.SIGNIN);
    }

    @Override
    public void success(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        startActivity(new Intent(EntryActivity.this, NavDrawer.class));
        finish();
    }

    @Override
    public void fail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

}
