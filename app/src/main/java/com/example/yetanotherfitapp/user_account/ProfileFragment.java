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
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileFragment extends Fragment {

    ArrayList<PieEntry> entries;
    ArrayList<PieEntry> entriesMod = new ArrayList<>();
    HashMap<String, String> mapOfEx = new HashMap<>();
    Set<Map.Entry<String, Object>> map;

    private FirebaseUser mUser;
    private FirebaseFirestore mStore;
    private String favouriteEx = "";
    private String title = "";
    private TextView textView;
    AppCompatActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUser = YafaApplication.from(context).getAuth().getCurrentUser();
        mStore = YafaApplication.from(context).getFirestore();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().hide();
        textView = view.findViewById(R.id.favouriteEx);
        getFirestoreList();

//        activity.setSupportActionBar(bar);
//        ActionBar actionBar = activity.getSupportActionBar();
//        actionBar.setTitle("TEsting");
        return view;
    }

    private void getFirestoreList() {
        mStore
                .collection("exercises")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> arrayList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : arrayList) {
                            mapOfEx.put((String) doc.get("image"), (String) doc.get("title"));
                        }
                    }
                });
        mStore
                .collection("users")
                .document(mUser.getUid())
                .collection("exercisesInfo")
                .document("NumOfDone")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        map = documentSnapshot.getData().entrySet();
                        long max = 0;
                        for (Map.Entry<String, Object> i : map) {
                            if ((long) i.getValue() > max) {
                                max = (long) i.getValue();
                                favouriteEx = i.getKey();
                            }
                        }
                        if (!favouriteEx.equals("")) {
                            mStore
                                    .collection("exercises")
                                    .document(favouriteEx)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            title = (String) documentSnapshot.get("title");
                                            textView.setText(title);
                                        }
                                    });
                        }
                    }
                });
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
