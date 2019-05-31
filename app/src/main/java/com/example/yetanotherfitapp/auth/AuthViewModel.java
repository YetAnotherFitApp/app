package com.example.yetanotherfitapp.auth;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

public class AuthViewModel extends AndroidViewModel {

    public enum AuthState {
        NONE,
        ERROR_EMAIL,
        ERROR_PASSWORD,
        ERROR_NICKNAME,
        PROGRESS,
        SUCCESS,
        FAILED
    }

    private MutableLiveData<AuthState> mSignInState;
    private MutableLiveData<AuthState> mRegState;
    private MutableLiveData<String> mErrorMessage;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mSignInState = new MutableLiveData<>();
        mRegState = new MutableLiveData<>();
        mErrorMessage = new MutableLiveData<>();
        mSignInState.setValue(AuthState.NONE);
        mRegState.setValue(AuthState.NONE);
        mErrorMessage.setValue("");
    }

    LiveData<AuthState> getSignInState() {
        return mSignInState;
    }

    LiveData<AuthState> getRegState() {
        return mRegState;
    }

    LiveData<String> getErrorMessage() {
        return mErrorMessage;
    }

    void signIn(String email, String password) {
        if (isNotValidEmail(email)) {
            mSignInState.postValue(AuthState.ERROR_EMAIL);
        } else if (isNotValidPassword(password)) {
            mSignInState.postValue(AuthState.ERROR_PASSWORD);
        } else {
            requestSignIn(email, password);
        }
    }

    private void requestSignIn(String email, String password) {
        mSignInState.postValue(AuthState.PROGRESS);
        new AuthRepo(getApplication()).signIn(email, password, new AuthRepo.AuthProgress() {
            @Override
            public void onSuccess() {
                mSignInState.postValue(AuthState.SUCCESS);
            }

            @Override
            public void onFailed(String errorMsg) {
                mErrorMessage.setValue(errorMsg);
                mSignInState.postValue(AuthState.FAILED);
            }
        });
    }

    void createAccount(String email, String password, String nickname) {
        if (isNotValidNickname(nickname)) {
            mRegState.postValue(AuthState.ERROR_NICKNAME);
        } else if (isNotValidEmail(email)) {
            mRegState.postValue(AuthState.ERROR_EMAIL);
        } else if (isNotValidPassword(password)) {
            mRegState.postValue(AuthState.ERROR_PASSWORD);
        } else {
            requestCreateAccount(email, password, nickname);
        }
    }

    private void requestCreateAccount(String email, String password, String nickname) {
        mRegState.postValue(AuthState.PROGRESS);
        new AuthRepo(getApplication()).createAccount(email, password, nickname, new AuthRepo.AuthProgress() {
            @Override
            public void onSuccess() {
                mRegState.postValue(AuthState.SUCCESS);
            }

            @Override
            public void onFailed(String errorMsg) {
                mErrorMessage.setValue(errorMsg);
                mRegState.postValue(AuthState.FAILED);
            }
        });
    }

    private boolean isNotValidEmail(String email) {
        return TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNotValidPassword(String password) {
        return TextUtils.isEmpty(password);
    }

    private boolean isNotValidNickname(String nickname) {
        return TextUtils.isEmpty(nickname);
    }

}
