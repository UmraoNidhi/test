package com.iparksimple.app.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.iparksimple.app.Activities.HomeActivity;
import com.iparksimple.app.Adapters.CardAdapter;
import com.iparksimple.app.Adapters.VehicleAdapter;
import com.iparksimple.app.R;
import com.iparksimple.app.ApiEndPoints.ApiClient;
import com.iparksimple.app.ApiEndPoints.ApiInterface;
import com.iparksimple.app.ApiEndPoints.UpdateProfileResult;
import com.iparksimple.app.ApiEndPoints.AddVehicleResult;
import com.iparksimple.app.ApiEndPoints.ProfileResult;
import com.iparksimple.app.utils.GlobalUtils;
import com.iparksimple.app.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    TextView textView,title;
    RecyclerView vehicle_details,Card_details;
    LinearLayoutManager VehicleLayoutManager,CardlayoutManager;
    CardAdapter cardAdapter;
    VehicleAdapter vehicleAdapter;
    ArrayList<String>CardList;
    ArrayList<String>VehicleList;
    TextInputEditText Username, Email,Phone,Car_Model,Car_Color,PlateNo,OldPassword,NewPassword,Confirm;
    String username,user_email,User_phone,Token,Model,Color,PlateNumber;
    HashMap<String,String>request_map = new HashMap<>();
    ArrayList<ProfileResult.Data.Vehicle>Vehicle_List = new ArrayList<>();
    HashMap<String,String>Vehicle_map = new HashMap<>();
    Button save;
    TextView AddVehicle_bt;
    Dialog dialog,dialog_password;
    ProgressBar progressBar,profile_progress;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
//        final TextView textView = root.findViewById(R.id.text_gallery);

        CardList = new ArrayList<>();
        CardList.add("1");
        CardList.add("1");
        CardList.add("1");

        VehicleList = new ArrayList<>();
        VehicleList.add("1");
        VehicleList.add("1");
        VehicleList.add("1");
        VehicleList.add("1");
        Token = PreferenceUtil.getAccessTokenFromLogin(getContext());
        User_phone = PreferenceUtil.getUserMobile(getContext());
        user_email = PreferenceUtil.getUserEmail(getContext());
        username = PreferenceUtil.getUserName(getContext());

        textView = root.findViewById(R.id.Change_password);
        Username = root.findViewById(R.id.et_name);
        Email = root.findViewById(R.id.et_email);
        Phone = root.findViewById(R.id.et_mobileNo);
        save = root.findViewById(R.id.Button_save);
        AddVehicle_bt = root.findViewById(R.id.Add_vehicle);
        profile_progress = root.findViewById(R.id.Progress_bar_profile);


        vehicle_details = root.findViewById(R.id.Vehicle_detail);
        vehicle_details.setHasFixedSize(false);
        VehicleLayoutManager = new LinearLayoutManager(getContext());
        vehicle_details.setLayoutManager(VehicleLayoutManager);

        Card_details = root.findViewById(R.id.Card_detail);
        Card_details.setHasFixedSize(false);
        CardlayoutManager = new LinearLayoutManager(getContext());
        Card_details.setLayoutManager(CardlayoutManager);
        cardAdapter = new CardAdapter(getContext(),CardList);
        Card_details.setAdapter(cardAdapter);
        if (User_phone!=null){
            Phone.setText(User_phone);
        }
        if (user_email!=null){
            Email.setText(user_email);
        }
        if (username!=null){
            Username.setText(username);
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ValidateField();

            }
        });

        AddVehicle_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();

            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordPopup();
            }
        });

        if (GlobalUtils.isNetworkAvailable(getActivity())){
            if (!Token.isEmpty()){
                getProfile();
            }
        }else {
            Toast.makeText(getContext(),getResources().getText(R.string.no_internet_msg),Toast.LENGTH_LONG).show();
        }


        return root;
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

    private void openAddDialog(){
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_vehicle);
        ImageView imageView = dialog.findViewById(R.id.Cross);
        Button button = dialog.findViewById(R.id.Button_save);
        Car_Color = dialog.findViewById(R.id.et_Color);
        Car_Model = dialog.findViewById(R.id.et_Model_name);
        PlateNo = dialog.findViewById(R.id.et_Lname);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model = Car_Model.getText().toString();
                PlateNumber = PlateNo.getText().toString();
                Color = Car_Color.getText().toString();
                if (!Model.isEmpty()){
                    if (!PlateNumber.isEmpty()){
                        if (!Color.isEmpty()){
                            AddVehicle();
                            progressBar.setVisibility(View.VISIBLE);
                        }else {
                            Toast.makeText(getContext(),"Please add vehicle color",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getContext(),"Please add vehicle licence number plate",Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getContext(),"Please add vehicle model",Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
            }
        });
        dialog.show();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


    private void ValidateField(){
        username = Username.getText().toString();
        user_email = Email.getText().toString();
        User_phone = Phone.getText().toString();
        if (!username.isEmpty()){
            if (!user_email.isEmpty()){
                if (!User_phone.isEmpty()){
                    if (GlobalUtils.isNetworkAvailable(getActivity())){
                        UpdateProfile();
                        profile_progress.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(getContext(),getResources().getText(R.string.no_internet_msg),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Enter phone ",Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(getContext(),"Enter email",Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getContext(),"Enter name ",Toast.LENGTH_LONG).show();

        }
    }


    private void UpdateProfile(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        request_map.put("name",username);
        request_map.put("phone",User_phone);
        request_map.put("email",user_email);

        Call<UpdateProfileResult> call = apiInterface.Update_profile(Token,request_map);
        call.enqueue(new Callback<UpdateProfileResult>() {
            @Override
            public void onResponse(Call<UpdateProfileResult> call, Response<UpdateProfileResult> response) {
                try {
                    profile_progress.setVisibility(View.GONE);
                    boolean status = response.body().getStatus();
                    String message = response.body().getMessage();

                    if (status == true){
                        String Username = response.body().getData().getName();
                        String Phone = response.body().getData().getPhone();
                        String Email = response.body().getData().getEmail();
                        String id = response.body().getData().getId();
                        if (Username!=null){
                            Intent intent= new Intent(getContext(), HomeActivity.class);
                            startActivity(intent);
                        }

                    }else {
                       Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResult> call, Throwable t) {
                Log.e("exception",":"+t);

            }
        });
    }

    private void getProfile(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ProfileResult>call = apiInterface.Getprofile(Token);
        call.enqueue(new Callback<ProfileResult>() {
            @Override
            public void onResponse(Call<ProfileResult> call, Response<ProfileResult> response) {
                try {
                    boolean status = response.body().getStatus();
                    String Message = response.body().getMessage();
                    if (status== true){
                        String name = response.body().getData().getCustomer().getName();
                        String Email = response.body().getData().getCustomer().getEmail();
                        String Phone = response.body().getData().getCustomer().getPhone();
                        Vehicle_List = response.body().getData().getVehicles();
                        if (Vehicle_List.size()>0){
                            vehicleAdapter = new VehicleAdapter(getContext(),Vehicle_List);
                            vehicle_details.setAdapter(vehicleAdapter);
                        }
                    }else {
                        Toast.makeText(getContext(),Message,Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ProfileResult> call, Throwable t) {

            }
        });


    }



    private void AddVehicle(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Vehicle_map.put("model",Model);
        Vehicle_map.put("color",Color);
        Vehicle_map.put("plate_number",PlateNumber);
        Call<AddVehicleResult>call = apiInterface.Add_vehicle(Token,Vehicle_map);
        call.enqueue(new Callback<AddVehicleResult>() {
            @Override
            public void onResponse(Call<AddVehicleResult> call, Response<AddVehicleResult> response) {
                try {
                    boolean status = response.body().getStatus();
                    String Message = response.body().getMessage();
                    if (status == true){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),Message,Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<AddVehicleResult> call, Throwable t) {

            }
        });
    }


    private void ChangePasswordPopup(){
        dialog_password = new Dialog(getContext());
        dialog_password.setContentView(R.layout.chnage_password);
        ImageView imageView = dialog_password.findViewById(R.id.Cross);
        Button button = dialog_password.findViewById(R.id.Button_save);
        OldPassword = dialog_password.findViewById(R.id.et_old_password);
        NewPassword = dialog_password.findViewById(R.id.et_newPassword);
        Confirm = dialog_password.findViewById(R.id.et_ConfirmPassword);
        title = dialog_password.findViewById(R.id.popup_title);
        title.setText("Change Password ");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_password.dismiss();
            }
        });
        dialog_password.show();


    }

}