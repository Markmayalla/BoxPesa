package com.example.boxpesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mark on 2/27/18.
 */

public class MyCustomeAdapter extends BaseAdapter {

    private static ArrayList<SearchResults> searchArrayList;

    private LayoutInflater mInflater;

    public MyCustomeAdapter(Context context, ArrayList<SearchResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custome_row, null);
            holder = new ViewHolder();
            holder.pocketName = (TextView) convertView.findViewById(R.id.list_pocket_name);
            holder.pocketGoal = (TextView) convertView.findViewById(R.id.list_pocket_goal);
            holder.maturityDate = (TextView) convertView.findViewById(R.id.list_maturity_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pocketName.setText(searchArrayList.get(position).getPocket_name());
        holder.pocketGoal.setText(searchArrayList.get(position).getPocket_goal());
        holder.maturityDate.setText(searchArrayList.get(position).getMaturity_date());

        return convertView;
    }

    static class ViewHolder {
        TextView pocketName;
        TextView pocketGoal;
        TextView maturityDate;
    }
}


