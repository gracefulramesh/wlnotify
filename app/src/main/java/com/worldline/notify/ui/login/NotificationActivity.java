package com.worldline.notify.ui.login;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.worldline.notify.R;
import com.worldline.notify.data.Device;
import com.worldline.notify.data.PagerAdapter;
import com.worldline.notify.data.SharedPrefManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class NotificationActivity extends AppCompatActivity {
    private int userid;
    private String project;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Device device = SharedPrefManager.getInstance(this).getDevice();
        userid = device.getUserid();
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("project")) {
            project = getIntent().getExtras().getString("project");
        } else {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //setting the title
        toolbar.setTitle("Messages :: " + project);

        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Success"));
        tabLayout.addTab(tabLayout.newTab().setText("Failure"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.notification_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Fragment currentFragment = ((PagerAdapter) viewPager.getAdapter())
                            .instantiateItem(viewPager, tabLayout.getSelectedTabPosition());
                    if (currentFragment instanceof MessageFragment) {
                        ((MessageFragment) currentFragment).searchMessage("");
                    }
                    return false;
                }
            });

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    Fragment currentFragment = ((PagerAdapter) viewPager.getAdapter())
                            .instantiateItem(viewPager, tabLayout.getSelectedTabPosition());
                    if (currentFragment instanceof MessageFragment) {
                        ((MessageFragment) currentFragment).searchMessage(query);
                    }
                    return true;
                }

            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAbout:
                Toast.makeText(this, "Coming Soon!!!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuSettings:
                Toast.makeText(this, "Coming Soon!!!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                break;
        }
        return true;
    }
}