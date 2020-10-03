package com.worldline.notify.data;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.worldline.notify.R;

public class View_Holder extends RecyclerView.ViewHolder {


    TextView txtMsg;
    TextView txtTime;


    View_Holder(View itemView) {
        super(itemView);
        txtMsg = (TextView) itemView.findViewById(R.id.textMsg);
        txtTime = (TextView) itemView.findViewById(R.id.textTime);
    }
}