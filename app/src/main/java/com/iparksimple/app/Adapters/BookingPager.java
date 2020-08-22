package com.iparksimple.app.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.iparksimple.app.Fragments.ActiveBookingFragment;
import com.iparksimple.app.Fragments.BookingHistoryFragment;
import com.iparksimple.app.Fragments.UpcomingBookingFragment;

import java.util.ArrayList;
import java.util.List;

public class BookingPager extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();



    public BookingPager(FragmentManager childFragmentManager) {
        super(childFragmentManager);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                BookingHistoryFragment historyFragment = new BookingHistoryFragment();
                return historyFragment;
            case 1:
                ActiveBookingFragment ActiveFragment = new ActiveBookingFragment();
                return ActiveFragment;
            case 2:
                UpcomingBookingFragment UpcomingFragment = new UpcomingBookingFragment();
                return UpcomingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
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
