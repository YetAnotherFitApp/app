package com.example.yetanotherfitapp.user_account;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.YafaApplication;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class StatisticFragment extends Fragment {

    private ExerciseListFragment.OnExListStateChangedListener mOnExListStateChangedListener;
    ArrayList<PieEntry> entries;
    ArrayList<PieEntry> entriesMod = new ArrayList<>();
    HashMap<String, String> mapOfEx = new HashMap<>();
    Set<Map.Entry<String, Object>> map;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    PieChart chart;
    ProgressBar progressBar;
    TextView doMoreExText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnExListStateChangedListener = (ExerciseListFragment.OnExListStateChangedListener) context;
        mFirestore = YafaApplication.from(context).getFirestore();
        mAuth = YafaApplication.from(context).getAuth();
    }

    private void getFirestoreList() {
        mFirestore
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
        mFirestore
                .collection("users")
                .document(mAuth.getUid())
                .collection("exercisesInfo")
                .document("NumOfDone")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        map = documentSnapshot.getData().entrySet();
                        entries = new ArrayList<>();
                        for (Map.Entry<String, Object> i : map) {
                            entries.add(new PieEntry((Long) i.getValue(), mapOfEx.get(i.getKey())));
                        }

                        Collections.sort(entries, new Comparator<PieEntry>() {
                            @Override
                            public int compare(PieEntry o1, PieEntry o2) {
                                return Float.compare(o2.getValue(), o1.getValue());
                            }
                        });
                        int i = 0;
                        float temp = 0;
                        for (PieEntry x : entries) {
                            if (i < 4) {
                                entriesMod.add(new PieEntry(x.getValue(), x.getLabel()));
                            } else {
                                temp += x.getValue();
                            }
                            i++;
                        }
                        if (temp == 0) {
                            progressBar.setVisibility(View.GONE);
                            doMoreExText.setVisibility(View.VISIBLE);
                            return;
                        }
                        entriesMod.add(new PieEntry(temp, "Other"));
                        setChart();
                    }
                });
    }

    public void setChart() {
        Log.d("smth", entries.toString());
        PieDataSet pieDataSet = new PieDataSet(entriesMod, "% of Exercises");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        chart.animateY(1000, Easing.EaseInOutCubic);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(8, 10, 8, 8);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(30);
        chart.setTransparentCircleRadius(61f);

        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(8f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.BLUE);

        chart.setData(pieData);
        chart.invalidate();
        chart.setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        mOnExListStateChangedListener.setActionBarTitle("Статистика");
        chart = view.findViewById(R.id.chart);
        doMoreExText = view.findViewById(R.id.openStat);
        progressBar = view.findViewById(R.id.statLoader);

        //progressBar.setVisibility(View.GONE);
        doMoreExText.setVisibility(View.GONE);
        chart.setVisibility(View.GONE);
        getFirestoreList();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnExListStateChangedListener = null;
    }

}
