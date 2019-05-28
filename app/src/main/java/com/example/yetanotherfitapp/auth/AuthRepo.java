package com.example.yetanotherfitapp.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.yetanotherfitapp.YafaApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepo {

    private final FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    Map<String, Object> objectMap = new HashMap<>();

    public AuthRepo(Context context) {
        mFirestore = YafaApplication.from(context).getFirestore();
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

    public HashMap<String, Long> buildExecriseHashMap() {
        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("exercise1", 0L);
        hashMap.put("exercise2", 0L);
        hashMap.put("exercise3", 0L);
        hashMap.put("exercise4", 0L);
        hashMap.put("exercise5", 0L);
        hashMap.put("exercise6", 0L);
        hashMap.put("exercise7", 0L);
        hashMap.put("exercise8", 0L);
        hashMap.put("exercise9", 0L);
        return hashMap;
    }

    public void createAccount(String email, String password, final String nickname, final AuthProgress progress) {
        objectMap.put("nickname", nickname);
        objectMap.put("mail", email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //mAuth.getCurrentUser().sendEmailVerification();
                            //mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest());
                            mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nickname).build());
                            DocumentReference documentReference = mFirestore
                                    .collection("users")
                                    .document(mAuth.getUid());

                            documentReference.set(objectMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("smth", "DocumentSnapshot successfully written!");
                                        }
                                    });
                            documentReference.collection("exercisesInfo").document("NumOfDone").set(buildExecriseHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("smth", "ExerciseCollectionAdd successful");
                                }
                            });
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
