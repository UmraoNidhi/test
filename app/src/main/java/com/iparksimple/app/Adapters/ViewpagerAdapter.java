package com.iparksimple.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.iparksimple.app.Activities.ParkingDetailActivity;
import com.iparksimple.app.R;
import com.iparksimple.app.ApiEndPoints.ParkingLotsListResult;

import java.util.ArrayList;
import java.util.List;

public class ViewpagerAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    List<ParkingLotsListResult.Data.Lot> mImages;
    CardView cardView;

    public ViewpagerAdapter(FragmentActivity activity, ArrayList<ParkingLotsListResult.Data.Lot> parking_list) {


    }

    public ViewpagerAdapter(FragmentActivity activity, List<ParkingLotsListResult.Data.Lot> slist) {
        this.mcontext = activity;
        this.mImages = slist;
        if (activity != null)
            inflater = LayoutInflater.from(activity);
    }


    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View sliderView = inflater.inflate(R.layout.listview,container,false);
        try {
            ((ViewPager) container).addView(sliderView);

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        cardView = sliderView.findViewById(R.id.Car_list);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ParkingDetailActivity.class);
                mcontext.startActivity(intent);
            }
        });

        return sliderView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);    }

}
