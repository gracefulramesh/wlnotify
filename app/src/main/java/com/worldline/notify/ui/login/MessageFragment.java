package com.worldline.notify.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.worldline.notify.R;
import com.worldline.notify.data.NotificationAdapter;
import com.worldline.notify.data.Notifications;
import com.worldline.notify.utility.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    private int userid;
    private String project;
    List<Notifications> rowItems;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar loadingProgressBar;
    NotificationAdapter notificationAdapter;
    View rootView;


    boolean isLoading = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String category;
    private String qStr = "";

    public MessageFragment(String category) {
        // Required empty public constructor
        this.category = category;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuccessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment("0");
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    public void searchMessage(String searchKey) {
        Log.d("searchMessage", searchKey);
        rowItems = new ArrayList<Notifications>();
        this.qStr = searchKey;
        if(isVisible()){
            getNotifications(userid, project, 0, 0);
        }
    }

    private void getNotifications(final int userid, final String project, final int adapterChangeFlag, final int offset) {
        Log.d("Function Open", "getNotifications");
        try {
            final Context context = getActivity().getApplicationContext();
            final RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Config.NOTIFICATIONS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                JSONObject res = new JSONObject(response);
                                Boolean error = (Boolean) res.get("error");
                                if (error.equals(false)) {
                                    JSONArray notifications = res.getJSONArray("message");
                                    if (notifications != null) {
                                        appendToAdapter(notifications, adapterChangeFlag);
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
                    params.put("offset", String.valueOf(offset));
                    params.put("category", category);
                    params.put("qStr", qStr);
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
    }

    private void appendToAdapter(JSONArray notifications, int adapterChangeFlag) {
        Log.d("Function Open", "appendToAdapter");
        try {
            for (int i = 0; i < notifications.length(); i++) {
                JSONObject notification = notifications.getJSONObject(i);
                Notifications item = new Notifications(
                        Integer.parseInt(notification.getString("id")),
                        notification.getString("msg"),
                        notification.getString("createdtime")
                );
                rowItems.add(item);
            }
            if (adapterChangeFlag == 1) {
                notificationAdapter.notifyDataSetChanged();
                isLoading = false;
            } else {
                setData(rowItems);
            }
        } catch (JSONException e) {
            Log.e("JSON_RES_PARSE", "unexpected JSON exception", e);
        }

    }

    private void setData(List<Notifications> rowItems) {
        Log.d("Function Open", "setData");
        Log.d("Entering", "NotificationActivity::setData");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.notifications_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        notificationAdapter = new NotificationAdapter(getActivity().getApplicationContext(), R.layout.notification_list_view, rowItems);
        recyclerView.setAdapter(notificationAdapter);
        loadingProgressBar.setVisibility(View.GONE);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        initScrollListener(userid, project);
    }

    private void initScrollListener(final int userid, final String project) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowItems.size() - 1) {
                        //bottom of list!
                        loadMore(userid, project);
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore(final int userid, final String project) {
        Log.d("Function Open", "loadMore");
        rowItems.add(null);
        recyclerView.post(new Runnable() {
            public void run() {
                notificationAdapter.notifyItemInserted(rowItems.size() - 1);
            }
        });

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                rowItems.remove(rowItems.size() - 1);
                int scrollPosition = rowItems.size();
                notificationAdapter.notifyItemRemoved(scrollPosition);
                getNotifications(userid, project, 1, rowItems.size());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);
        // Inflate the layout for this fragment
        loadingProgressBar = rootView.findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.VISIBLE);

        rowItems = new ArrayList<Notifications>();

        if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().getExtras().containsKey("project")) {
            project = getActivity().getIntent().getExtras().getString("project");
        }
        getNotifications(userid, project, 0, 0);
        return rootView;
    }


}