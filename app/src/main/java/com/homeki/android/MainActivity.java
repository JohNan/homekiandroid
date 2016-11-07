package com.homeki.android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.homeki.android.misc.Settings;
import com.homeki.android.server.ApiClient;
import com.homeki.android.server.ServerLocator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private MenuOption selectedMenuItem;
    private NavigationView mNavigationView;
    private ApiClient mApiClient;
    private View mHeaderLlayout;
    private TextView mHeaderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mHeaderLlayout = mNavigationView.getHeaderView(0);

        if(mHeaderLlayout != null) {
            mHeaderTitle = (TextView) mHeaderLlayout.findViewById(R.id.textview_title_drawer_header);
        }

        selectedMenuItem = MenuOption.DEVICES;
        if (savedInstanceState != null && savedInstanceState.containsKey("selectedMenuItem")) {
            selectedMenuItem = MenuOption.valueOf(savedInstanceState.getString("selectedMenuItem"));
        }

        mApiClient = new ApiClient(this);

        getServerInfo();
        updateFragment();
    }

    private void updateFragment() {
        switch (selectedMenuItem) {
            case ACTION_GROUPS:
                showActionGroups();
                break;
            case DEVICES:
            default:
                showDevices();
                break;
        }
    }

    private void showDevices() {
        mNavigationView.setCheckedItem(R.id.menu_device);
        changeFragment(new DeviceListFragment());
        selectedMenuItem = MenuOption.DEVICES;
    }

    private void showActionGroups() {
        mNavigationView.setCheckedItem(R.id.menu_action_groups);
        changeFragment(new ActionGroupListFragment());
        selectedMenuItem = MenuOption.ACTION_GROUPS;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedMenuItem", selectedMenuItem.toString());
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment, "Fragment");
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.menu_device:
                showDevices();
                return true;
            case R.id.menu_action_groups:
                showActionGroups();
                return true;
            case R.id.menu_settings:
                mNavigationView.setCheckedItem(item.getItemId());
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;

        }
        return false;
    }

    private void getServerInfo() {
        new AsyncTask<Void, Void, ApiClient.JsonServer>() {
            @Override
            protected ApiClient.JsonServer doInBackground(Void... params) {
                try {
                    return mApiClient.getServerInfo();
                } catch (Exception e1) {
                    Log.e(TAG, "Failed to get devices, trying to locate on wifi.", e1);

                    try {
                        String hostname = ServerLocator.locateServerOnWifi();
                        Settings.setServerUrl(MainActivity.this, hostname);
                    } catch (Exception e2) {
                        Log.e(TAG, "Failed to locate server on wifi, giving up.", e2);
                        return null;
                    }

                    try {
                        Log.i(TAG, "Retrying device fetch using server located on wifi.");
                        return mApiClient.getServerInfo();
                    } catch (Exception e2) {
                        Log.e(TAG, "Failed to get devices.", e2);
                        return null;
                    }
                }
            }

            @Override
            protected void onPostExecute(ApiClient.JsonServer jsonServerInfo) {
                if(jsonServerInfo != null && mHeaderTitle != null) {
                    mHeaderTitle.setText(jsonServerInfo.name);
                }
            }
        }.execute();
    }

    protected enum MenuOption {
        DEVICES,
        ACTION_GROUPS
    }
}
