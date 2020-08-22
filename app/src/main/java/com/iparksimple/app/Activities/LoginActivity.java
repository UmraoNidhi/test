package com.iparksimple.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.GsonBuilder;
import com.iparksimple.app.R;
import com.iparksimple.app.ApiEndPoints.ApiClient;
import com.iparksimple.app.ApiEndPoints.ApiInterface;
import com.iparksimple.app.ApiEndPoints.LoginResult;
import com.iparksimple.app.utils.Constants;
import com.iparksimple.app.utils.GlobalUtils;
import com.iparksimple.app.utils.PreferenceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView SignUp, forgot_password;
    Button SignIn;
    String User_Name, Password, Type;
    TextInputEditText Username, User_password;
//    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            Type = getIntent().getStringExtra(Constants.PreferenceConstants.TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SignIn = findViewById(R.id.Button_SignIn);
//        progressBar = findViewById(R.id.Progress_bar);
        SignUp = findViewById(R.id.Sign_up);
        Username = findViewById(R.id.et_email);
        User_password = findViewById(R.id.et_password);
        forgot_password = findViewById(R.id.Forgot_password);
        forgot_password.setText(Html.fromHtml("<u>Forgot Password </u>"));
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

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

    private void ValidateFields() {
        User_Name = Username.getText().toString();
        Password = User_password.getText().toString();
        if (User_Name.isEmpty()) {
            GlobalUtils.showToast(LoginActivity.this, "Please enter your email id/mobile number!");
        } else if (checkIfPhoneNo(User_Name)) {
            if (User_Name.trim().length() < 10) {
                GlobalUtils.showToast(LoginActivity.this, "Please enter a valid mobile number!");
            } else if (User_Name.trim().length() > 10) {
                GlobalUtils.showToast(LoginActivity.this, "Please enter a valid mobile number!");
            } else if (Password.isEmpty()) {
                GlobalUtils.showToast(LoginActivity.this, "Password should not be empty!");
            } else if (Password.length() < 6) {
                GlobalUtils.showToast(LoginActivity.this, "Password should contain minimum 6 characters! ");
            } else if (Password.length() > 14) {
                GlobalUtils.showToast(LoginActivity.this, "password should not exceed 14 characters!");
            } else {
                if (GlobalUtils.isNetworkAvailable(LoginActivity.this)) {
                    GlobalUtils.showDialog(LoginActivity.this, "Authenticating", false);
                    userLogin();
                } else {
                    GlobalUtils.showToast(LoginActivity.this, getResources().getString(R.string.no_internet_msg));
                }
            }
        } else if (!isValidEmail(User_Name)) {
            GlobalUtils.showToast(LoginActivity.this, "Please enter a valid email id!");
        } else if (Password.isEmpty()) {
            GlobalUtils.showToast(LoginActivity.this, "Password should not be empty!");
        } else if (Password.length() < 6) {
            GlobalUtils.showToast(LoginActivity.this, "Password should contain minimum 6 characters!");
        } else if (Password.length() > 14) {
            GlobalUtils.showToast(LoginActivity.this, "password should not exceed 14 characters!");
        } else {
            if (GlobalUtils.isNetworkAvailable(LoginActivity.this)) {
                GlobalUtils.showDialog(LoginActivity.this, "Authenticating", false);
                userLogin();
            } else {
                GlobalUtils.showToast(LoginActivity.this, getResources().getString(R.string.no_internet_msg));
            }
        }

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

    private void userLogin() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResult> call = apiInterface.Login(User_Name, Password);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                Log.e("LoginResponse", ":" + new GsonBuilder().setPrettyPrinting().create().toJson(response.body()));
                String message = "";
                GlobalUtils.hidedialog();
                try {
                    if (response.isSuccessful()) {
                        boolean status = response.body().getStatus();
                        message = response.body().getMessage();
                        if (status) {
                            if (response.body().getData() != null) {
                                String UserId = response.body().getData().getId();
                                String UserEmail = response.body().getData().getEmail();
                                String UserMobile = response.body().getData().getPhone();
                                String Token = response.body().getData().getToken();
                                PreferenceUtil.setAccessTokenFromLogin(LoginActivity.this, Token);
                                PreferenceUtil.setUserId(LoginActivity.this, UserId);
                                PreferenceUtil.setUserEmail(LoginActivity.this, UserEmail);
                                PreferenceUtil.setUserMobile(LoginActivity.this, UserMobile);
                                if (Type != null) {
                                    if (Type.equalsIgnoreCase("DetailPage")) {
                                        Intent intent = new Intent(LoginActivity.this, VehicleListActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                        }
                    } else {
                        GlobalUtils.showToast(LoginActivity.this, getResources().getString(R.string.something_wrong));
//                            GlobalUtils.showToast(LoginActivity.this,message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    GlobalUtils.showToast(LoginActivity.this, getResources().getString(R.string.something_wrong));
                    GlobalUtils.showToast(LoginActivity.this, message);
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.e("exception", ":" + t);
                GlobalUtils.hidedialog();
                GlobalUtils.showToast(LoginActivity.this, getResources().getString(R.string.something_wrong));
            }
        });
    }

}
