package com.example.yetanotherfitapp.user_account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.YafaApplication;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseUser mUser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUser = YafaApplication.from(context).getAuth().getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView userNameView = view.findViewById(R.id.UserName);
        TextView userMail = view.findViewById(R.id.UserMail);

        userNameView.setText(mUser.getDisplayName());
        userMail.setText(mUser.getEmail());
    }
}
