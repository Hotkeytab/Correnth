package com.dna.plank.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dna.plank.DB.DBscore;
import com.dna.plank.R;
import com.dna.plank.Adapters.ScoreAdapter;

public class HistoriqueFrag  extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DBscore dbHelper;
    private ScoreAdapter adapter;
    String filter="";

    View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment



        rootView = inflater.inflate(R.layout.liste_histo, parent, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.listehisto);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        populaterecyclerView(filter);


        //populate recyclerview
        return rootView;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);


    }

    private void populaterecyclerView(String filter){

        dbHelper = new DBscore(getActivity());
        adapter = new ScoreAdapter(dbHelper.peopleList(filter), getActivity(), mRecyclerView);
        mRecyclerView.setAdapter(adapter);

    }
}

