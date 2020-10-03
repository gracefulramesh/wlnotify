package com.worldline.notify.ui.login;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class RowViewHolder extends RecyclerView.ViewHolder implements TextView.OnClickListener {

    private RecyclerViewClickListener mListener;
    public TextView textView;
    public RowViewHolder(TextView v, RecyclerViewClickListener listener) {
        super(v);
        mListener = listener;
        textView = v;
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition());
    }
}
