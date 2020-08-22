package com.iparksimple.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.iparksimple.app.R;
import com.iparksimple.app.ApiEndPoints.ApiClient;
import com.iparksimple.app.ApiEndPoints.ApiInterface;
import com.iparksimple.app.ApiEndPoints.ForgotPasswordResult;
import com.iparksimple.app.utils.GlobalUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {
    TextInputEditText editText;
    String Username;
    ImageView back;
    Button Continue;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        editText = findViewById(R.id.et_details);
        back = findViewById(R.id.Image_back);
        progressBar = findViewById(R.id.Progress_bar);
        Continue = findViewById(R.id.Button_continue);
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = editText.getText().toString();
                if (Username.isEmpty()){
                    GlobalUtils.showToast(ForgotActivity.this,"Enter your registered mobile number/email id!");
                }else if(checkIfPhoneNo(Username) && Username.length()>10){
                    GlobalUtils.showToast(ForgotActivity.this,"Enter a valid mobile number!");
                }else if(checkIfPhoneNo(Username) && Username.length()<10){
                    GlobalUtils.showToast(ForgotActivity.this,"Enter a valid mobile number!");
                }else if(!checkIfPhoneNo(Username) && !isValidEmail(Username)){
                    GlobalUtils.showToast(ForgotActivity.this,"Enter a valid email id!");
                }else{
                    if(GlobalUtils.isNetworkAvailable(ForgotActivity.this)){
                        GlobalUtils.showDialog(ForgotActivity.this,"Submitting",false );
                        ForgotPassword();
                    }else{
                        GlobalUtils.showToast(ForgotActivity.this,getResources().getString(R.string.no_internet_msg));
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private boolean checkIfPhoneNo(String email) {
        boolean numeric = true;
        try {
            Double num = Double.parseDouble(email);
        } catch (NumberFormatException e) {
            numeric = false;
        }
        return numeric;
    }
    private void ForgotPassword(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ForgotPasswordResult>call = apiInterface.ForgotPassword(Username);
        call.enqueue(new Callback<ForgotPasswordResult>() {
            @Override
            public void onResponse(Call<ForgotPasswordResult> call, Response<ForgotPasswordResult> response) {
                GlobalUtils.hidedialog();
                try {
                    if(response.isSuccessful()) {
                        boolean status = response.body().getStatus();
                        String message = response.body().getMessage();
                        if(status) {
                            String Username = response.body().getData().getUsername();
                            if (Username != null) {
                                Intent intent = new Intent(ForgotActivity.this, ResetActivity.class);
                                startActivity(intent);
                            }
                        }else{
                            GlobalUtils.showToast(ForgotActivity.this, message);
                        }
                    }else{
                        GlobalUtils.showToast(ForgotActivity.this, response.message());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    GlobalUtils.showToast(ForgotActivity.this, getResources().getString(R.string.something_wrong));
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResult> call, Throwable t) {
                GlobalUtils.hidedialog();
                GlobalUtils.showToast(ForgotActivity.this, getResources().getString(R.string.something_wrong));
                Log.e("exception",":"+t);

            }
        });
    }
}
