package com.iparksimple.app.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.iparksimple.app.Fragments.ReportFragment;

import java.util.ArrayList;
import java.util.List;

public class Pager extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public Pager(FragmentManager childFragmentManager) {
        super(childFragmentManager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ReportFragment DailyFragment = new ReportFragment();
                return DailyFragment;
            case 1:
                ReportFragment monthlyFragment = new ReportFragment();
                return monthlyFragment;
            case 2:
                ReportFragment AirportFragment = new ReportFragment();
                return AirportFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size() ;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
}
