package com.fink.sendmefun.ui.activity;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fink.sendmefun.BusProvider;
import com.fink.sendmefun.model.Event;
import com.fink.sendmefun.model.History;
import com.fink.sendmefun.model.Mode;
import com.fink.sendmefun.net.Server;
import com.fink.sendmefun.ui.listener.NavigationListener;
import com.fink.sendmefun.R;

import com.fink.sendmefun.model.message.Message;
import com.fink.sendmefun.ui.fragment.BaseFragment;
import com.squareup.otto.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String MESSAGE = "message";
    private List<Message> messages = new ArrayList<>();

    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        navigationView.setNavigationItemSelectedListener(new NavigationListener(this, drawerLayout, toolbar));
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (savedInstanceState == null) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);
            Observable.create(Server.createServer(this))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Server.createServerSubscriber());
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.main_container, BaseFragment.newInstance(messages));
            fragmentTransaction.commit();
        }else{
            messages = (List<Message>)savedInstanceState.getSerializable(MESSAGE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MESSAGE, (Serializable) messages);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void addMessage(Message message) {
        messages.add(message);
        if(Mode.getInstance(this).getWriteMode()) {
            History.getInstance(this).writeHistory(message);
        }
        BusProvider.getInstance().post(new Event());
    }

    public List<Message> getMessages() {
        return messages;
    }

}
