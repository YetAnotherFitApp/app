package com.example.yetanotherfitapp.fragments;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

public class SignInViewModel extends AndroidViewModel {

    enum SignInState {
        NONE,
        ERROR_EMAIL,
        ERROR_PASSWORD,
        PROGRESS,
        SUCCESS,
        FAILED
    }

    private MutableLiveData<SignInState> mSignInState;
    private MutableLiveData<String> mErrorMessage;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        mSignInState = new MutableLiveData<>();
        mErrorMessage = new MutableLiveData<>();
        mSignInState.setValue(SignInState.NONE);
        mErrorMessage.setValue("");
    }

    public LiveData<SignInState> getState() {
        return mSignInState;
    }

    public LiveData<String> getErrorMessage() {
        return mErrorMessage;
    }

    public void signIn(String email, String password) {
        if (!isValidEmail(email)) {
            mSignInState.postValue(SignInState.ERROR_EMAIL);
        } else if (!isValidPassword(password)) {
            mSignInState.postValue(SignInState.ERROR_PASSWORD);
        } else {
            requestSignIn(email, password);
        }
    }

    private void requestSignIn(String email, String password) {
        mSignInState.postValue(SignInState.PROGRESS);
        new SignInRepo(getApplication()).signIn(email, password, new SignInRepo.SignInProgress() {
            @Override
            public void onSuccess() {
                mSignInState.postValue(SignInState.SUCCESS);
            }

            @Override
            public void onFailed(String errorMsg) {
                mErrorMessage.setValue(errorMsg);
                mSignInState.postValue(SignInState.FAILED);
            }
        });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password);
    }

}
