package com.shamsshafiq.accompli;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends BaseAdapter {

    private Context mContext;
    private List<Pair<String, String>> mData = new ArrayList<Pair<String, String>>();

    public DataAdapter(Context context, List<Pair<String, String>> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Pair<String, String> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_row, null);
        }

        ((TextView)convertView.findViewById(R.id.text_name)).setText(getItem(position).first);
        ((TextView)convertView.findViewById(R.id.text_value)).setText(getItem(position).second);


        return convertView;
    }

    public void updateEntries(List<Pair<String, String>> entries) {
        mData = entries;
        notifyDataSetChanged();
    }
}
