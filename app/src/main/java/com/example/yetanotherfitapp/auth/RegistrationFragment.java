package com.example.yetanotherfitapp.auth;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.yetanotherfitapp.R;

public class RegistrationFragment extends Fragment {

    private EntryFragment.OnAuthStateChangeListener mOnAuthStateChangeListener;
    private AuthViewModel mAuthViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnAuthStateChangeListener = (EntryFragment.OnAuthStateChangeListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuthViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);

        final EditText nickname = view.findViewById(R.id.reg_nickname);
        final EditText email = view.findViewById(R.id.reg_email);
        final EditText password = view.findViewById(R.id.reg_password);
        final ProgressBar progressBar = view.findViewById(R.id.reg_progress);
        final Button regBtn = view.findViewById(R.id.reg_btn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthViewModel.createAccount(email.getText().toString(), password.getText().toString(), nickname.getText().toString());
            }
        });

        mAuthViewModel.getRegState().observe(this, new Observer<AuthViewModel.AuthState>() {
            @Override
            public void onChanged(@Nullable AuthViewModel.AuthState authState) {
                switch (authState) {
                    case ERROR_NICKNAME:
                        nickname.setError(getResources().getString(R.string.error_msg));
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        regBtn.setEnabled(true);
                        break;
                    case ERROR_EMAIL:
                        nickname.setError(null);
                        email.setError(getResources().getString(R.string.error_msg));
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        regBtn.setEnabled(true);
                        break;
                    case ERROR_PASSWORD:
                        nickname.setError(null);
                        email.setError(null);
                        password.setError(getResources().getString(R.string.error_msg));
                        progressBar.setVisibility(View.GONE);
                        regBtn.setEnabled(true);
                        break;
                    case PROGRESS:
                        nickname.setError(null);
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.VISIBLE);
                        regBtn.setEnabled(false);
                        break;
                    case SUCCESS:
                        nickname.setError(null);
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        regBtn.setEnabled(false);
                        mOnAuthStateChangeListener.success(getResources().getString(R.string.account_create_success));
                        break;
                    case FAILED:
                        nickname.setError(null);
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        regBtn.setEnabled(true);
                        String errMsg = mAuthViewModel.getErrorMessage().getValue();
                        mOnAuthStateChangeListener.fail(errMsg);
                        break;
                    case NONE:
                        nickname.setError(null);
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        regBtn.setEnabled(true);
                        break;
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAuthStateChangeListener = null;
    }

}
