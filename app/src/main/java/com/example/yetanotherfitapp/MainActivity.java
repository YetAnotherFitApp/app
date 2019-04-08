package com.example.yetanotherfitapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DBG_TAG = "AuthEmailPassword";

    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        findViewById(R.id.createAccountBtn).setOnClickListener(this);
        findViewById(R.id.signInBtn).setOnClickListener(this);
        findViewById(R.id.signOutBtn).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Log.d(DBG_TAG, "createAccount" + email);
        if (!validateForm()) {
            return;
        }

        //TODO: may be add progressDialog...

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(DBG_TAG, "Create Account: success!");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.d(DBG_TAG, "Create Account: fail! " + task.getException());
                            //TODO: Toast later...
                            updateUI(null);
                        }
                    }
                });

    }

    private void signIn(String email, String password) {

        Log.d(DBG_TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        //TODO: progressDialog...

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(DBG_TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.d(DBG_TAG, "signInWithEmail:fail");
                            //TODO: Toast later...
                            updateUI(null);
                        }
                    }
                });

    }

    private void signOut() {
        Log.d(DBG_TAG, "Sign out " + mAuth.getCurrentUser().getEmail());
        mAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        //TODO: Add greater Validater!!!

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser currentUser) {
        //TODO: progressDialog...
        if (currentUser != null) {
            findViewById(R.id.createAccountBtn).setVisibility(View.INVISIBLE);
            findViewById(R.id.signInBtn).setVisibility(View.INVISIBLE);
            findViewById(R.id.signOutBtn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.createAccountBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.signInBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutBtn).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createAccountBtn:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.signInBtn:
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.signOutBtn:
                signOut();
                break;
        }
    }
}
