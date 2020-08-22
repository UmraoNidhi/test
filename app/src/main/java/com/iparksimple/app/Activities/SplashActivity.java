package com.iparksimple.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.iparksimple.app.R;

public class SplashActivity extends AppCompatActivity {
    TextView tvLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLoading = findViewById(R.id.tv_loading);
        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);

                }
            }
        };thread.start();

    }
}
