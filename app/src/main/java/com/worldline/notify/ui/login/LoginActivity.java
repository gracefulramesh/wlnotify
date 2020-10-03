package com.worldline.notify.ui.login;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.worldline.notify.R;
import com.worldline.notify.data.Device;
import com.worldline.notify.data.SharedPrefManager;
import com.worldline.notify.utility.Config;
import com.worldline.notify.utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }



    private void login(final String username, final String password){

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }
        try {

            StringRequest postRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                Log.d("Response", response);
                                JSONObject res = new JSONObject(response);
                                Boolean error = (Boolean) res.get("error");
                                String message = (String) res.get("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                if(error.equals(false)) {
                                    final String name = (String) res.get("name");

                                    String token = (String) res.get("token");

                                    final int userid = Integer.parseInt(res.getString("userid"));
                                    Log.d("TOKEN",token);

                                    if(token.length()>10){
                                        Device device = new Device(userid, name, token);
                                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(device);

                                        finish();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                    }else {
                                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                final String newToken = instanceIdResult.getToken();
                                                Log.e("newToken", newToken);
                                                StringRequest postRequest = new StringRequest(Request.Method.POST, Config.TOKEN_SAVE_URL,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                // response
                                                                try {
                                                                    Log.d("Token Response", response);
                                                                    JSONObject res = new JSONObject(response);
                                                                    Device device = new Device(userid, name, newToken);
                                                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(device);

                                                                    finish();
                                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                                                        params.put("email", username);
                                                        params.put("token", newToken);
                                                        return params;
                                                    }
                                                };
                                                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);

                                            }
                                        });


                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                Log.e("JSON_RES_PARSE", "unexpected JSON exception", e);
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Log.d("password", password);
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("email", username);
                    params.put("password", password);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(postRequest);

        } catch (Exception e) {
            Log.d("Login Error:","Login Failed");
        }
    }
}