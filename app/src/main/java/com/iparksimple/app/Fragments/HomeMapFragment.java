package com.iparksimple.app.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iparksimple.app.Activities.HomeActivity;
import com.iparksimple.app.services.LocationTracker;
import com.iparksimple.app.Activities.ParkingDetailActivity;
import com.iparksimple.app.Adapters.ViewpagerAdapter;
import com.iparksimple.app.R;
import com.iparksimple.app.ApiEndPoints.ApiClient;
import com.iparksimple.app.ApiEndPoints.ApiInterface;
import com.iparksimple.app.ApiEndPoints.ParkingLotsListResult;
import com.iparksimple.app.utils.Constants;
import com.iparksimple.app.utils.GlobalUtils;
import com.iparksimple.app.utils.PreferenceUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements LocationListener,  OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    GoogleMap MarkergoogleMap;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    LocationTracker locationTracker;
    ViewPager viewPager;
    ViewpagerAdapter viewpagerAdapter;
    private static int NUM_PAGES = 0;
    private static int currentPage = 0;
    HashMap<String,String>request_map ;
    String currentLat, currentLon,Address,Title,Detail,Name,Id,ParkingType;
    ApiInterface apiInterface;
    ArrayList<ParkingLotsListResult.Data.Lot>Parking_list = new ArrayList<>();
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private ImageView[] dots;
    ProgressBar progressBar;
    String Selected_view;
    CirclePageIndicator pageIndicator;
    String selectedType="daily";
    Boolean isCurrentLoc=false;

    public HomeMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_map, container, false);
        HomeActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        if(this.getArguments() != null && this.getArguments().getString(Constants.PreferenceConstants.TYPE).isEmpty()){
            selectedType =this.getArguments().getString(Constants.PreferenceConstants.TYPE);
        }
        locationTracker = new LocationTracker(getContext());
        currentLat = Double.toString(locationTracker.getLongitude());
        currentLon =Double.toString(locationTracker.getLongitude());
        PreferenceUtil.setUserLatitude(getContext(), currentLat);
        PreferenceUtil.setUserLongitude(getContext(), currentLon);
        viewPager = view.findViewById(R.id.viewPager);
        pageIndicator = view.findViewById(R.id.indicator);
        progressBar = view.findViewById(R.id.Progress_bar);
        Selected_view = Constants.PreferenceConstants.Selected_MAP;
            initMap();
            if (GlobalUtils.isNetworkAvailable(getActivity())){
                    progressBar.setVisibility(View.VISIBLE);
                    GetMapData();
                }else {
                    GlobalUtils.showToast(getActivity(),getResources().getString(R.string.no_internet_msg));
                }

        return view;

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkergoogleMap = googleMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//                googleMap.getUiSettings().setScrollGesturesEnabled(true);

//                googleMap.setLatLngBoundsForCameraTarget(PACIFIC);
//                googleMap.getUiSettings().setRotateGesturesEnabled(false);
//                googleMap.getUiSettings().setTiltGesturesEnabled(false);
//                googleMap.clear();
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(6.5244, -3.3792))
                        .zoom(8)
                        .build();

                CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(googlePlex);
                googleMap.animateCamera(camUpd, 2000, null);
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(FragmentActivity activity, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(activity, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        points.add(latLng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void GetMapData(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        request_map = new HashMap<>();
//        request_map.put("latitude",Latitude);
//        request_map.put("longitude",Longitude);
//        request_map.put("type","monthly");
//        request_map.put("Distance","5");
        Call<ParkingLotsListResult>call = apiInterface.GetHomeData(currentLat, currentLon,selectedType,"5");
        call.enqueue(new Callback<ParkingLotsListResult>() {
            @Override
            public void onResponse(Call<ParkingLotsListResult> call, Response<ParkingLotsListResult> response) {
                progressBar.setVisibility(View.GONE);
//                Log.e("HomeMapData",":"+new GsonBuilder().setPrettyPrinting().create().toJson(response.body()));
                try {
                    if(response !=null) {
                        boolean status = response.body().getStatus();
                        String message = response.body().getMessage();
                        Parking_list = response.body().getData().getLots();
                        ArrayList<ParkingLotsListResult.Data.Lot> tempList= new ArrayList<>();
                        if (response.body().getData().getLots().size() > 0) {

                            for (int i = 0; i < response.body().getData().getLots().size(); i++) {
                                String lat = Parking_list.get(i).getLatitude();
                                String lon = Parking_list.get(i).getLongitude();
                                Address = Parking_list.get(i).getAddress();
                                Name = Parking_list.get(i).getName();
                                Id = Parking_list.get(i).getId();
                                if(i<10){
                                    tempList.add(Parking_list.get(i));
                                }
                                builder.include(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                                AddMarker( new LatLng(Double.parseDouble(lat),Double.parseDouble(lon)), Name, isCurrentLoc);
                            }

//                            builder.include(new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLon)));
//                            AddMarker( new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLon)),"Your Location", true);

                            LatLngBounds bounds = builder.build();
                            MarkergoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Intent intent = new Intent(getContext(), ParkingDetailActivity.class);
                                    intent.putExtra(Constants.PreferenceConstants.ID, Id);
                                    startActivity(intent);
                                }
                            });
                            try {
                                MarkergoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            displaySliderImage(tempList);
                        } else {

                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ParkingLotsListResult> call, Throwable t) {
                Log.e("exception",":"+t);

            }
        });
    }
    public void displaySliderImage(List<ParkingLotsListResult.Data.Lot> slist) {
        viewpagerAdapter = new ViewpagerAdapter(getActivity(), slist);
        viewPager.setAdapter(viewpagerAdapter);
        pageIndicator.setViewPager(viewPager);
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(5 * density);

        NUM_PAGES =slist.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) { currentPage = position;}

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) { }

            @Override
            public void onPageScrollStateChanged(int pos) { }
        });
    }


    private void AddMarker(LatLng latLng, String name, Boolean isCurrentLoc){
    MarkergoogleMap.addMarker(new MarkerOptions()
            .position(latLng)
            .title(name).icon(bitmapDescriptorFromVector(getActivity(), isCurrentLoc? R.drawable.ic_current_loc : R.drawable.map_icon)));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
