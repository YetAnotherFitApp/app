package com.example.yetanotherfitapp.user_account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.YafaApplication;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseUser mUser;
    AppCompatActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUser = YafaApplication.from(context).getAuth().getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().hide();
//        activity.setSupportActionBar(bar);
//        ActionBar actionBar = activity.getSupportActionBar();
//        actionBar.setTitle("TEsting");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView userNameView = view.findViewById(R.id.UserName);
        TextView userMail = view.findViewById(R.id.UserMail);

        userNameView.setText(mUser.getDisplayName());
        userMail.setText(mUser.getEmail());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.getSupportActionBar().show();
    }

}
