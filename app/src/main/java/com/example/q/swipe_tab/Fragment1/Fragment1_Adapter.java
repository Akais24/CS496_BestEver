package com.example.q.swipe_tab.Fragment1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.q.swipe_tab.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment1_Adapter extends BaseAdapter {
    Context context;
    ArrayList<JSONObject> con2;
    LayoutInflater inf;
    int layout;

    Fragment1_Adapter(Context c, int layout, ArrayList<JSONObject> con){
        this.context = c;
        this.con2 = con;
        this.layout = layout;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(R.layout.fragment1_row, null);
        TextView TextView1 = (TextView) convertView.findViewById(R.id.textView);
    /*        TextView TextView2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView TextView3 = (TextView) convertView.findViewById(R.id.textView3);*/
        JSONObject JO = con2.get(position);
        try {
            Object name = JO.get("name");
            TextView1.setText("   "+ name );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return convertView;
    }
    @Override
    public Object getItem(int position)
    {
        return position;
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public int getCount()
    {
        return con2.size();
    }
}