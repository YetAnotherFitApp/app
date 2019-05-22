package com.example.yetanotherfitapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yetanotherfitapp.R;

public class EntryFragment extends Fragment implements View.OnClickListener {

    private OnAuthStateChangeListener mOnAuthStateChangeListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnAuthStateChangeListener = (OnAuthStateChangeListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.entry_reg_btn).setOnClickListener(this);
        view.findViewById(R.id.entry_sign_in_btn).setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAuthStateChangeListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entry_reg_btn:
                mOnAuthStateChangeListener.needReg();
                break;
            case R.id.entry_sign_in_btn:
                mOnAuthStateChangeListener.needSignIn();
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
