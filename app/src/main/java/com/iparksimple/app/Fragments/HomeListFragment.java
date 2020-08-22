package com.iparksimple.app.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iparksimple.app.Adapters.ListAdapter;
import com.iparksimple.app.R;
import com.iparksimple.app.ApiEndPoints.ApiClient;
import com.iparksimple.app.ApiEndPoints.ApiInterface;
import com.iparksimple.app.ApiEndPoints.ParkingLotsListResult;
import com.iparksimple.app.utils.Constants;
import com.iparksimple.app.utils.GlobalUtils;
import com.iparksimple.app.utils.PreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeListFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ParkingLotsListResult.Data.Lot> parkingLocationList= new ArrayList<>();
    String latitude="0.0000", longitude="0.0000";
    String selectedType="daily";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_list, container, false);

        recyclerView = view.findViewById(R.id.rv_recent_nearest);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        requestData();
        return view;
    }

    private void requestData() {
        if(!getArguments().getString(Constants.PreferenceConstants.TYPE).isEmpty()){
            selectedType = getArguments().getString(Constants.PreferenceConstants.TYPE);
        }
        if(!PreferenceUtil.getUserLatitude(getContext()).isEmpty()) {
            latitude = PreferenceUtil.getUserLatitude(getContext());
        }
        if(!PreferenceUtil.getUserLongitude(getContext()).isEmpty()) {
            longitude = PreferenceUtil.getUserLongitude(getContext());
        }

        if (GlobalUtils.isNetworkAvailable(getActivity())){
            GlobalUtils.showDialog(getActivity(),"Loading",true);
            getParkingList();
        }else {
            Toast.makeText(getContext(),getResources().getText(R.string.no_internet_msg),Toast.LENGTH_LONG).show();
        }
    }

    private void getParkingList() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ParkingLotsListResult> call = apiInterface.GetHomeData(latitude,longitude,"airport","5");
        call.enqueue(new Callback<ParkingLotsListResult>() {
            @Override
            public void onResponse(Call<ParkingLotsListResult> call, Response<ParkingLotsListResult> response) {
                GlobalUtils.hidedialog();
                try {
                    if(response != null) {
                        boolean status = response.body().getStatus();
                        String message = response.body().getMessage();
                        if (status) {
                            parkingLocationList = new ArrayList<>();
                            parkingLocationList = response.body().getData().getLots();
                            if (parkingLocationList.size() > 0) {
//                        for (int i=0;i<parkingLocationList.size();i++){
//                            latitude = parkingLocationList.get(i).getLatitude();
//                            longitude = parkingLocationList.get(i).getLongitude();
//                            Address = parkingLocationList.get(i).getAddress();
//                            Name = parkingLocationList.get(i).getName();
//                            Id = parkingLocationList.get(i).getId();
//                        }
                                ListAdapter listAdapter = new ListAdapter(getContext(), parkingLocationList);
                                recyclerView.setAdapter(listAdapter);
                            }
                        } else {
                            GlobalUtils.showToast(getActivity(),message);
                        }
                    }else{
                        GlobalUtils.showToast(getActivity(),getActivity().getResources().getString(R.string.something_wrong));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    GlobalUtils.showToast(getActivity(),getActivity().getResources().getString(R.string.something_wrong));
                }
            }

            @Override
            public void onFailure(Call<ParkingLotsListResult> call, Throwable t) {
                Log.e("exception",":"+t);
                GlobalUtils.hidedialog();
                GlobalUtils.showToast(getActivity(),getActivity().getResources().getString(R.string.something_wrong));
            }
        });
    }
}