package com.example.yetanotherfitapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.yetanotherfitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText mNicknameField; //на будущее
    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressBar mProgressBar;
    private EntryFragment.OnAuthStateChangeListener mOnAuthStateChangeListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnAuthStateChangeListener = (EntryFragment.OnAuthStateChangeListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mNicknameField = view.findViewById(R.id.reg_nickname);
        mEmailField = view.findViewById(R.id.reg_email);
        mPasswordField = view.findViewById(R.id.reg_password);
        mProgressBar = view.findViewById(R.id.reg_progress);

        view.findViewById(R.id.reg_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAuthStateChangeListener = null;
    }

    private void createAccount(String email, String password) {
        showProgress();

        if (!validateForm()) {
            hideProgress();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgress();

                        if (task.isSuccessful()) {
                            mOnAuthStateChangeListener.success("Аккаунт успешно создан");
                        } else {
                            mOnAuthStateChangeListener.fail(task.getException().getMessage());
                        }
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Поле заполнено некорректно");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Поле заполнено некорректно");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }
}
