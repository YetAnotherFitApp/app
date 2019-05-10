package com.example.yetanotherfitapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yetanotherfitapp.R;

public class EntryFragment extends Fragment implements View.OnClickListener {

    private OnAuthStateChangeListener onAuthStateChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAuthStateChangeListener = (OnAuthStateChangeListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        view.findViewById(R.id.entryRegBtn).setOnClickListener(this);
        view.findViewById(R.id.entrySignInBtn).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entryRegBtn:
                onAuthStateChangeListener.needReg();
                break;
            case R.id.entrySignInBtn:
                onAuthStateChangeListener.needSignIn();
                break;
        }
    }

    public interface OnAuthStateChangeListener {
        void needReg();

        void needSignIn();

        void success(String msg);

        void fail(String errorMsg);
    }
}
