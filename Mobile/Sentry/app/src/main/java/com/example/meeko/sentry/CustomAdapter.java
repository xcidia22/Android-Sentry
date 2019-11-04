package com.example.meeko.sentry;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Incident> list = new ArrayList<Incident>();
    private Context context;



    public CustomAdapter(ArrayList<Incident> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_view, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position).VictimName);

        //Handle buttons and add onClickListeners
        Button Goto = (Button)view.findViewById(R.id.Goto_btn);
        Button Close = (Button)view.findViewById(R.id.Close_btn);

        Goto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,HomeActivity.class);
                intent.putExtra("methodName","GotoLocation");
                intent.putExtra("Latitude",list.get(position).Latitude);
                intent.putExtra("Longitude",list.get(position).getLongitude());
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);


            }
        });
        Close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CloseCase.class);
                intent.putExtra("directory",list.get(position).getDirectory());
                intent.putExtra("IncidentOrigin",list.get(position).getLoc());
                intent.putExtra("name",list.get(position).getVictimName());
                intent.putExtra("IID",list.get(position).getID());
                context.startActivity(intent);
            }
        });

        return view;
    }
}
