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
import android.widget.Button;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.YafaApplication;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileFragment extends Fragment {

    HashMap<String, String> mapOfEx = new HashMap<>();
    Set<Map.Entry<String, Object>> map;

    private FirebaseUser mUser;
    private ExerciseListFragment.OnExListStateChangedListener mOnExListStateChangedListener;
    private FirebaseFirestore mStore;
    private String favouriteEx = "";
    private String title = "";
    private TextView textView;
    private Button mWatchBtn;
    AppCompatActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnExListStateChangedListener = (ExerciseListFragment.OnExListStateChangedListener) context;
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
                                            mWatchBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    mOnExListStateChangedListener.goToStatistic();
                                                }
                                            });
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
        mWatchBtn = view.findViewById(R.id.watch);

        userNameView.setText(mUser.getDisplayName());
        userMail.setText(mUser.getEmail());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.getSupportActionBar().show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnExListStateChangedListener = null;
    }
}
