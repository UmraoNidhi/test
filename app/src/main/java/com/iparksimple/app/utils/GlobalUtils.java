package com.iparksimple.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.widget.Toast;


import com.iparksimple.app.R;
import com.tomer.fadingtextview.FadingTextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GlobalUtils {

    private static Dialog  progressDialog;


    public String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String apdate = df.format(c.getTime());
        return apdate;
    }
    public String getTomorrowDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR,1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(c.getTime());

        return date;
    }
    public String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String time = df.format(c.getTime());
        return time;
    }
    public String getTime12hrs(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aaa");
        String time = df.format(c.getTime());
        return time;
    }
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isEmailValidate(String mal) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(mal);
        return matcher.matches();
    }


    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showDialog(Activity activity, String msg, Boolean cancellable){
        progressDialog = new Dialog(activity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(cancellable);
        progressDialog.setContentView(R.layout.custom_loading_layout);
        FadingTextView tvMsg= progressDialog.findViewById(R.id.tv_msg);
        String[] texts = {msg+".",msg+"..",msg+"..."};
        tvMsg.setTexts(texts);
        progressDialog.show();
    }

    public static void hidedialog(){
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog= null;
        }
    }
}
