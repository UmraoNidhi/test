package com.iparksimple.app.Fragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iparksimple.app.Activities.HomeActivity;
import com.iparksimple.app.Adapters.BookingAdapter;
import com.iparksimple.app.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingBookingFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<String>mUpcoming;
    BookingAdapter bookingAdapter;


    public UpcomingBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_booking, container, false);

        mUpcoming = new ArrayList<>();
        mUpcoming.add("1");
        mUpcoming.add("1");
        mUpcoming.add("1");


        recyclerView = view.findViewById(R.id.recycler_upcoming);
        recyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        bookingAdapter = new BookingAdapter(getContext(),mUpcoming);
        recyclerView.setAdapter(bookingAdapter);



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if(activity instanceof HomeActivity){
            HomeActivity homeActivity = (HomeActivity) activity;
            homeActivity.enableToolBar(false);
        }
    }
}
