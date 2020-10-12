package com.worldline.notify.ui.login;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.worldline.notify.R;
import com.worldline.notify.data.Device;
import com.worldline.notify.data.PagerAdapter;
import com.worldline.notify.data.SharedPrefManager;
import com.worldline.notify.utility.Config;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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


        /*final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();

                final  View dialogView = inflater.inflate(R.layout.layout_dialog_filter_add, null);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setPositiveButton("Add", null);
                builder.setNegativeButton("Cancel", null);
                builder.setView(dialogView);

                final AlertDialog mDialog = builder.create();
                mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Spinner spinner = (Spinner) dialogView.findViewById(R.id.spin_conditions);
                                EditText filter_text_in = (EditText) dialogView.findViewById(R.id.filter_text);
                                EditText filter_name_in = (EditText) dialogView.findViewById(R.id.filter_name);
                                final String like_condition = spinner.getSelectedItem().toString();
                                final String filter_text = filter_text_in.getText().toString();
                                final String filter_name = filter_name_in.getText().toString();

                                if(filter_text.trim().length() > 5 && filter_text.trim().length() > 5 ){
                                    try {
                                        final Context context = getActivity().getApplicationContext();
                                        final RequestQueue requestQueue = Volley.newRequestQueue(context);
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, Config.FILTER_ADD,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // response
                                                        try {
                                                            JSONObject res = new JSONObject(response);
                                                            Boolean error = (Boolean) res.get("error");
                                                            if (error.equals(false)) {
                                                                JSONObject notifications = res.getJSONObject("message");
                                                                if (notifications != null) {
                                                                    notifications.getString("return");
                                                                } else {
                                                                    Toast.makeText(context, "No Nofications", Toast.LENGTH_LONG).show();
                                                                }
                                                            } else {
                                                                Toast.makeText(context, "No Nofications", Toast.LENGTH_LONG).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            Log.e("JSON_RES_PARSE", "unexpected JSON exception", e);
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // error
                                                        Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                        ) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("userid", String.valueOf(userid));
                                                params.put("project", project);
                                                params.put("condition", String.valueOf(like_condition));
                                                params.put("name", filter_name);
                                                params.put("text", filter_text);
                                                return params;
                                            }
                                        };
                                        requestQueue.add(postRequest);
                                        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                            @Override
                                            public void onRequestFinished(Request<Object> request) {
                                                requestQueue.getCache().clear();
                                            }
                                        });
                                        //VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Filter name and text must contains atleast 5 characters", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });*/

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


    public NotificationActivity getActivity(){
        return this;
    }
}