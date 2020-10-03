package com.worldline.notify.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worldline.notify.R;

import java.util.Collections;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    List<Notifications> list = Collections.emptyList();
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public NotificationAdapter(Context context,int resourceId, List<Notifications> items){
        this.context = context;
        this.list = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        if(viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_view, parent, false);
            View_Holder holder = new View_Holder(v);
            return holder;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(v);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        if(viewHolder instanceof View_Holder && list.get(position) instanceof Notifications) {
            populateItemRows((View_Holder) viewHolder, position);
        }else if(viewHolder instanceof  LoadingViewHolder){
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
        //animate(holder);
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
    }

    private void populateItemRows(View_Holder viewHolder, int position) {
        viewHolder.txtMsg.setText(list.get(position).getMessage());
        viewHolder.txtTime.setText(list.get(position).getCreatedtime());
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Notifications data) {
        list.add(position, data);
        notifyItemInserted(position);
    }


}
