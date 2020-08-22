package com.iparksimple.app.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.google.android.material.navigation.NavigationView;
import com.iparksimple.app.Fragments.NotificationFragment;
import com.iparksimple.app.Fragments.BookingFragment;
import com.iparksimple.app.Fragments.ProfileFragment;
import com.iparksimple.app.Fragments.HomeMapFragment;
import com.iparksimple.app.Fragments.HomeListFragment;
import com.iparksimple.app.Fragments.ReportFragment;
import com.iparksimple.app.R;
import com.iparksimple.app.utils.Constants;
import com.iparksimple.app.utils.PreferenceUtil;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static BottomNavigationView bottomNavigationView;
    FragmentTransaction fragmentTransaction;
    String token, userName;
    public static Menu menu;
    boolean isUserLoggedIn = false;
    TextView btnLogin, btnSignUp,header_userName;
    NavigationView navigationView;

    RelativeLayout searchLayout;
    TextView textView, tvSearchText, tvMapViewLabel, tvListViewLabel;
    ImageView iconMapView, iconListView;
    ImageButton btnSearch;
    LinearLayout tabMapView, tabListView, tabBarLayout;
    CardView cardMapIcon, cardListIcon;

    private static final int PERMISSION_REQUEST_CODE = 200;
    String selectedType="daily", type1="daily", type2="monthly", type3="airport";
    Boolean isListView=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        token = PreferenceUtil.getAccessTokenFromLogin(this);
        userName = PreferenceUtil.getUserName(this);
        PreferenceUtil.setIsListView(this,isListView);
        PreferenceUtil.setIsListView(this,isListView);

        bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Toolbar toolbar = findViewById(R.id.toolbar);
        searchLayout= toolbar.findViewById(R.id.rl_searchview);
        tvSearchText = toolbar.findViewById(R.id.tv_searchText);
        btnSearch = toolbar.findViewById(R.id.ibtn_search);
        tabBarLayout =findViewById(R.id.ll_view_tab);
        searchLayout.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.left_menu);
        toolbar.setBackgroundResource(R.color.white);
        enableToolBar(true);
        ViewGroup actionBarLayout = (ViewGroup) HomeActivity.this.getLayoutInflater().inflate(R.layout.custom_title, null);
        ActionBar actionBar = HomeActivity.this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        textView = findViewById(R.id.textviewactivityname);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.left_menu);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);

            }
        });
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        btnLogin = header.findViewById(R.id.tv_nav_login);
        btnSignUp = header.findViewById(R.id.tv_nav_signup);
        header_userName = header.findViewById(R.id.Text_name);
        header_userName.setText(toCamelCase(userName));


        tvMapViewLabel = findViewById(R.id.tv_mapview_label);
        tvListViewLabel = findViewById(R.id.tv_listview_label);
        iconMapView = findViewById(R.id.iv_map_icon);
        iconListView = findViewById(R.id.iv_list_icon);

        tabMapView = findViewById(R.id.ll_map_tab);
        tabListView = findViewById(R.id.ll_list_tab);
        cardMapIcon = findViewById(R.id.card_map_icon);
        cardListIcon = findViewById(R.id.card_list_icon);

        tabMapView.setOnClickListener(clickListener);
        tabListView.setOnClickListener(clickListener);

        btnLogin.setOnClickListener(clickListener);
        btnSignUp.setOnClickListener(clickListener);

        searchLayout.setOnClickListener(clickListener);
        btnSearch.setOnClickListener(clickListener);

        requestPermission();
        setInitialScreen();
    }


    public void enableToolBar(boolean b) {
        if(b) {
            tabBarLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.VISIBLE);
        }else{
            tabBarLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            Bundle bundle = new Bundle();
            Fragment selectedFragment=null;
            switch (v.getId()){
                case R.id.tv_nav_login:
                    intent = new Intent(HomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tv_nav_signup:
                    intent = new Intent(HomeActivity.this,SignUpActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ibtn_search:
                case R.id.rl_searchview:
                    intent = new Intent(HomeActivity.this, SearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_map_tab:
                    isListView=false;
                    changeAppearance(tvMapViewLabel,tvListViewLabel, iconMapView, iconListView,cardMapIcon,cardListIcon);
                    PreferenceUtil.setIsListView(HomeActivity.this,isListView);
                    if(getSupportFragmentManager().getBackStackEntryCount()>0){
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                    selectedFragment= new HomeMapFragment();
                    bundle.putString(Constants.PreferenceConstants.TYPE,selectedType);
                    selectedFragment.setArguments(bundle);
                    displaySelectedFragment(selectedFragment,null);
                    break;
                case R.id.ll_list_tab:
                    isListView=true;
                    changeAppearance(tvListViewLabel,tvMapViewLabel, iconListView,iconMapView,cardListIcon,cardMapIcon);
                    PreferenceUtil.setIsListView(HomeActivity.this,isListView);
                    if(getSupportFragmentManager().getBackStackEntryCount()>0){
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                    selectedFragment= new HomeListFragment();
                    bundle.putString(Constants.PreferenceConstants.TYPE,selectedType);
                    selectedFragment.setArguments(bundle);
                    displaySelectedFragment(selectedFragment,null);
                    break;
            }
        }
    };

    private void changeAppearance(TextView tvSel, TextView tvUnSel, ImageView iconSel, ImageView iconUnSel, CardView selCard, CardView unSelCard) {
        tvSel.setTextColor(getResources().getColor(R.color.blue));
        tvUnSel.setTextColor(getResources().getColor(R.color.Black));

        Drawable d1 = DrawableCompat.wrap(iconSel.getDrawable()).mutate();
        DrawableCompat.setTint(d1, getResources().getColor(R.color.white));

        Drawable d2 = DrawableCompat.wrap(iconUnSel.getDrawable()).mutate();
        DrawableCompat.setTint(d2, getResources().getColor(R.color.Black));

        selCard.setCardBackgroundColor(getResources().getColor(R.color.blue));
        unSelCard.setCardBackgroundColor(getResources().getColor(R.color.white));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem searchMenuItem = menu.findItem( R.id.action_search );
//        searchMenuItem.expandActionView();
        searchMenuItem.setVisible(false);
        HomeActivity.menu = menu;
        return true;
    }

    private void showNavItems(NavigationView navigationView) {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_Logout).setVisible(true);
        nav_Menu.findItem(R.id.nav_Profile).setVisible(true);
    }

    private void hideNavItems(NavigationView navigationView) {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_Logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_Profile).setVisible(false);
        nav_Menu.findItem(R.id.nav_Vehicle).setVisible(false);
    }

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    Bundle bundle = new Bundle();
                    switch (item.getItemId()) {
                        case R.id.navigation_Daily:
                            selectedType=type1;
                            tvSearchText.setText(getResources().getString(R.string.dailyMonthly_search_text));
                            setBottomNavAnimation(item.getItemId(),R.id.navigation_Monthly,R.id.navigation_Airport);
                            break;
                        case R.id.navigation_Monthly:
                            selectedType=type2;
                            tvSearchText.setText(getResources().getString(R.string.dailyMonthly_search_text));
                            setBottomNavAnimation(item.getItemId(),R.id.navigation_Daily,R.id.navigation_Airport);
                            break;
                        case R.id.navigation_Airport:
                            selectedType=type3;
                            tvSearchText.setText(getResources().getString(R.string.airport_search_text));
                            setBottomNavAnimation(item.getItemId(),R.id.navigation_Monthly,R.id.navigation_Daily);
                            break;
                    }

                    if(getSupportFragmentManager().getBackStackEntryCount()>0){
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                    if(PreferenceUtil.getIsListView(HomeActivity.this)){
                        selectedFragment = new HomeListFragment();
                    }else{
                        selectedFragment = new HomeMapFragment();
                    }
                    bundle.putString(Constants.PreferenceConstants.TYPE, selectedType);
                    selectedFragment.setArguments(bundle);
                    displaySelectedFragment(selectedFragment,null);
                    return true;
                }
            };

    private void setBottomNavAnimation(int selId, int unSelId1, int unSelId2) {
        View selected= bottomNavigationView.findViewById(selId);
        View unselected1= bottomNavigationView.findViewById(unSelId1);
        View unselected2= bottomNavigationView.findViewById(unSelId2);
        selected.setBackgroundColor(getResources().getColor(R.color.blue));
        unselected1.setBackgroundColor(getResources().getColor(R.color.white));
        unselected2.setBackgroundColor(getResources().getColor(R.color.white));
    }


    private void displaySelectedFragment(Fragment fragment, String backStateName) {
        //BackStack should be null for MapFragment and ListFragment

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount()>1){
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(1);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        Log.e("FragmentCount",":"+manager.getBackStackEntryCount());

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(backStateName);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        enableToolBar(true);
        bottomNavigationView.setVisibility(View.VISIBLE);
        if (!PreferenceUtil.getAccessTokenFromLogin(this).isEmpty()){
            isUserLoggedIn=true;
            if (!PreferenceUtil.getAccessTokenFromLogin(this).isEmpty()){
                header_userName.setText("iPark");
                btnSignUp.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                showNavItems(navigationView);
            }else{
                header_userName.setText("iPark");
                btnSignUp.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                hideNavItems(navigationView);
            }

        }else {
            header_userName.setText("iPark");
            btnSignUp.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            hideNavItems(navigationView);
        }

        super.onResume();
    }

    private void setInitialScreen() {
        enableToolBar(true);
        bottomNavigationView.setVisibility(View.VISIBLE);
        View view = bottomNavigationView.findViewById(R.id.navigation_Daily);
        view.performClick();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.nav_Book:
                tvSearchText.setText(getResources().getString(R.string.dailyMonthly_search_text));
                if(getSupportFragmentManager().getBackStackEntryCount()>0) {
                    getSupportFragmentManager().popBackStackImmediate();
                }
                if(PreferenceUtil.getIsListView(HomeActivity.this)){
                    fragment = new ListFragment();
                }else {
                    fragment = new HomeMapFragment();
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null).commit();
                break;
            case R.id.nav_Profile:
                displaySelectedFragment(new ProfileFragment(), ProfileFragment.class.getSimpleName());
                textView.setText(item.getTitle());
                break;
            case R.id.nav_Booking:
                displaySelectedFragment(new BookingFragment(),BookingFragment.class.getSimpleName());
                textView.setText(item.getTitle());
                break;
            case R.id.Share:
                shareApp();
                break;
            case R.id.nav_Notification:
                displaySelectedFragment(new NotificationFragment(), NotificationFragment.class.getSimpleName());
                textView.setText(item.getTitle());
                break;
            case R.id.Report:
                displaySelectedFragment(new ReportFragment(), ReportFragment.class.getSimpleName());
                textView.setText(item.getTitle());
                break;
            case R.id.nav_Logout:
                openLogoutConfirm();
                break;

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //hidden/not in use
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ExitPopup(){
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.logout_popup);
        TextView text_yes=dialog.findViewById(R.id.TvExit);
        TextView text_No=dialog.findViewById(R.id.TvClose);
        TextView txtTitle = dialog.findViewById(R.id.txtSubTitle);
        txtTitle.setText("Alert");
        TextView txtSubTitle = dialog.findViewById(R.id.txtSubTitle);
        txtSubTitle.setText("Are you sure want to Exit?");
        text_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                dialog.dismiss();
            }
        });
        text_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey I just found this interesting app about Car Parking"+"\n"+"https://www.iparksimple.com/";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "BOT");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void openLogoutConfirm() {
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.logout_popup);
        TextView text_yes=dialog.findViewById(R.id.TvExit);
        TextView text_No=dialog.findViewById(R.id.TvClose);
        TextView txtTitle = dialog.findViewById(R.id.txtSubTitle);
        txtTitle.setText("Alert");
        TextView txtSubTitle = dialog.findViewById(R.id.txtSubTitle);
        txtSubTitle.setText("Are you sure want to logout?");
        text_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.clearPreferenceObject(HomeActivity.this);

                PreferenceUtil.setUserEmail(HomeActivity.this,"");
                PreferenceUtil.setAccessTokenFromLogin(HomeActivity.this,"");
                dialog.dismiss();
                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        text_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        int count=getSupportFragmentManager().getBackStackEntryCount();
        Log.e("Count",":"+count);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (count > 1) {                                                 // this will execute if there any fragment in backstack.
                getSupportFragmentManager().popBackStackImmediate();
                setInitialScreen();
        } else if (count == 1){                                                // When on Home screen
            if (selectedType.equalsIgnoreCase(type1)) {
                setInitialScreen();
            } else {
                ExitPopup();
            }
        }
    }


    public static String toCamelCase(final String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted ) {
//                        Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    } else {

                    }
                }
                break;
        }
    }
}





