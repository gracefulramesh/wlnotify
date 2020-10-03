package com.worldline.notify.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.worldline.notify.R;
import com.worldline.notify.data.Device;
import com.worldline.notify.data.ProjectsAdapter;
import com.worldline.notify.data.SharedPrefManager;
import com.worldline.notify.utility.Config;
import com.worldline.notify.utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int userid;
    String[] myDataset;

    ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("project")) {
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            intent.putExtra("project", getIntent().getExtras().getString("project"));
            startActivity(intent);
        }

        //getting the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //setting the title
        toolbar.setTitle("Projects");

        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);


        loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.VISIBLE);

        Device device  = SharedPrefManager.getInstance(this).getDevice();
        userid = device.getUserid();

        recyclerView = (RecyclerView) findViewById(R.id.projects_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getData();


    }
    private void setData(String message) {
        // specify an adapter (see also next example)
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                final TextView projectView = view.findViewById(R.id.projects);
                String projectName = projectView.getText().toString();

                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                intent.putExtra("project", projectName);
                startActivity(intent);
            }
        };

        myDataset = message.split(",");

        mAdapter = new ProjectsAdapter(myDataset, listener);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(mAdapter);
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void getData() {


        try {

            StringRequest postRequest = new StringRequest(Request.Method.POST, Config.PROJECTS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                JSONObject res = new JSONObject(response);
                                Boolean error = (Boolean) res.get("error");
                                String message = (String) res.get("message");
                                if (error.equals(false)) {
                                    setData(message);
                                }else{
                                    Toast.makeText(getApplicationContext(), "No Projects configured", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", String.valueOf(userid));
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(postRequest);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
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