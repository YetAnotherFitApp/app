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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AuthRepo {

    private final String EXERCISE = "exercise";
    private final String EXERCISE_COLLECTION_NAME = "exercises";
    private final String NICKNAME = "nickname";
    private final String EMAIL = "mail";
    private final String USERS_COLLECTION_NAME = "users";
    private final String EX_INFO_COLLECTION_NAME = "exercisesInfo";
    private final String NUM_OF_DONE = "NumOfDone";

    private final FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Map<String, Object> mObjectMap;

    AuthRepo(Context context) {
        mFirestore = YafaApplication.from(context).getFirestore();
        mAuth = YafaApplication.from(context).getAuth();
        mObjectMap = new HashMap<>();
    }

    void signIn(String email, String password, final AuthProgress progress) {
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

    private void buildExerciseHashMap(final BuildMapProgress buildProgress) {
        mFirestore.collection(EXERCISE_COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Long> hashMap = new HashMap<>();
                            long i = 1L;
                            for (QueryDocumentSnapshot ignored : task.getResult()) {
                                hashMap.put(EXERCISE + i, 0L);
                                i++;
                            }
                            buildProgress.onSuccess(hashMap);
                        } else {
                            buildProgress.onFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    void createAccount(String email, String password, final String nickname, final AuthProgress progress) {
        mObjectMap.put(NICKNAME, nickname);
        mObjectMap.put(EMAIL, email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //mAuth.getCurrentUser().sendEmailVerification();
                            //mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest());
                            mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest
                                    .Builder()
                                    .setDisplayName(nickname)
                                    .build());

                            final DocumentReference documentReference = mFirestore
                                    .collection(USERS_COLLECTION_NAME)
                                    .document(mAuth.getUid());

                            documentReference.set(mObjectMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("smth", "DocumentSnapshot successfully written!");
                                        }
                                    });

                            buildExerciseHashMap(new BuildMapProgress() {
                                @Override
                                public void onSuccess(HashMap<String, Long> exerciseHashMap) {
                                    documentReference.collection(EX_INFO_COLLECTION_NAME)
                                            .document(NUM_OF_DONE)
                                            .set(exerciseHashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("smth", "ExerciseCollectionAdd successful");
                                                }
                                            });
                                }

                                @Override
                                public void onFailed(String errorMsg) {
                                    progress.onFailed(errorMsg);
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

    private interface BuildMapProgress {
        void onSuccess(HashMap<String, Long> exerciseHashMap);

        void onFailed(String errorMsg);
    }

}
