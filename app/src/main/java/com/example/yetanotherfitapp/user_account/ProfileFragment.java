package com.example.yetanotherfitapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TextView userNameView = view.findViewById(R.id.UserName);
        TextView userMail = view.findViewById(R.id.UserMail);

        userNameView.setText(user.getDisplayName());
        userMail.setText(user.getEmail());

        return view;

    }

}
