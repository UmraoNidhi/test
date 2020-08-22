package com.iparksimple.app.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.iparksimple.app.Activities.HomeActivity;
import com.iparksimple.app.Adapters.BookingPager;
import com.iparksimple.app.R;


public class BookingFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_booking, container, false);


        viewPager = root.findViewById(R.id.ViewPager);
        tabLayout = root.findViewById(R.id.Tab_layout);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

    private void setupViewPager(ViewPager viewPager) {

        BookingPager adapter = new BookingPager(getChildFragmentManager());
        adapter.addFragment(new BookingHistoryFragment(), "History");
        adapter.addFragment(new ActiveBookingFragment(), "Active");
        adapter.addFragment(new UpcomingBookingFragment(),"Upcoming");
        viewPager.setAdapter(adapter);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity.bottomNavigationView.findViewById(R.id.nav_bottom).setVisibility(View.GONE);
        HomeActivity.menu.findItem(R.id.action_search).setVisible(false);
        Activity activity = getActivity();
        if(activity instanceof HomeActivity){
            HomeActivity homeActivity = (HomeActivity) activity;
            homeActivity.enableToolBar(false);
        }


    }
}