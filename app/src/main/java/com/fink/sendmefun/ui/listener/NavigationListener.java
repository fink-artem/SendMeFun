package com.fink.sendmefun.ui.listener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fink.sendmefun.R;
import com.fink.sendmefun.ui.activity.MainActivity;
import com.fink.sendmefun.ui.fragment.BaseFragment;
import com.fink.sendmefun.ui.fragment.HistoryFragment;
import com.fink.sendmefun.ui.fragment.SettingsFragment;

public class NavigationListener implements NavigationView.OnNavigationItemSelectedListener {

    private Activity activity;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    public NavigationListener(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar){
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        switch (menuItem.getItemId()) {
            case R.id.chat:
                fragmentTransaction.replace(R.id.main_container, BaseFragment.newInstance(((MainActivity)activity).getMessages()));
                toolbar.setTitle(activity.getResources().getString(R.string.app_name));
                break;
            case R.id.history:
                fragmentTransaction.replace(R.id.main_container, HistoryFragment.newInstance());
                toolbar.setTitle(activity.getResources().getString(R.string.title_history));
                break;
            case R.id.settings:
                fragmentTransaction.replace(R.id.main_container, SettingsFragment.newInstance());
                toolbar.setTitle(activity.getResources().getString(R.string.title_settings));
                break;
        }
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        menuItem.setChecked(false);
        return false;
    }
}
