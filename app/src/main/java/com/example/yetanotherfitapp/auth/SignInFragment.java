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

public class SignInFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuthViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);

        final EditText email = view.findViewById(R.id.sign_in_email);
        final EditText password = view.findViewById(R.id.sign_in_password);
        final ProgressBar progressBar = view.findViewById(R.id.sign_in_progress);
        final Button signInBtn = view.findViewById(R.id.sign_in_btn);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthViewModel.signIn(email.getText().toString(), password.getText().toString());
            }
        });

        mAuthViewModel.getSignInState().observe(this, new Observer<AuthViewModel.AuthState>() {
            @Override
            public void onChanged(@Nullable AuthViewModel.AuthState authState) {
                switch (authState) {
                    case ERROR_EMAIL:
                        email.setError(getResources().getString(R.string.error_msg));
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setEnabled(true);
                        break;
                    case ERROR_PASSWORD:
                        email.setError(null);
                        password.setError(getResources().getString(R.string.error_msg));
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setEnabled(true);
                        break;
                    case PROGRESS:
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.VISIBLE);
                        signInBtn.setEnabled(false);
                        break;
                    case SUCCESS:
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setEnabled(false);
                        mOnAuthStateChangeListener.success(getResources().getString(R.string.welcome));
                        break;
                    case FAILED:
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setEnabled(true);
                        String errMsg = mAuthViewModel.getErrorMessage().getValue();
                        mOnAuthStateChangeListener.fail(errMsg);
                        break;
                    case NONE:
                        email.setError(null);
                        password.setError(null);
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setEnabled(true);
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
