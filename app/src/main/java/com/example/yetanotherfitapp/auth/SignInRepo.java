package com.example.yetanotherfitapp.auth;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.yetanotherfitapp.YafaApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInRepo {

    private final FirebaseAuth mAuth;

    public SignInRepo(Context context) {
        mAuth = YafaApplication.from(context).getAuth();
    }

    public void signIn(String email, String password, final SignInProgress progress) {
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

    public interface SignInProgress {
        void onSuccess();

        void onFailed(String errorMsg);
    }

}
