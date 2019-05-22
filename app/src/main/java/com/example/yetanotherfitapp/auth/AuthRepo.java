package com.example.yetanotherfitapp.auth;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.yetanotherfitapp.YafaApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthRepo {

    private final FirebaseAuth mAuth;

    public AuthRepo(Context context) {
        mAuth = YafaApplication.from(context).getAuth();
    }

    public void signIn(String email, String password, final AuthProgress progress) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress.onSuccess();
                        } else {
                            progress.onFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    public void createAccount(String email, String password, final AuthProgress progress) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress.onSuccess();
                        } else {
                            progress.onFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    public interface AuthProgress {
        void onSuccess();

        void onFailed(String errorMsg);
    }

}
