package com.example.meeko.sentry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    // list of images
    public int[] lst_images = {
            R.drawable.image_1,
            R.drawable.image_2,
            R.drawable.image_3,
            R.drawable.image_4
    };
    // list of titles
    public String[] lst_title = {
            "Location Tracker",
            "Track Near Officers",
            "Alarm System",
            "Statistical Mappings"
    }   ;
    // list of descriptions
    public String[] lst_description = {
            "Sentry uses location tracking using either GPS or Internet. \nWhichever is available.",
            "Sentry allows easy contact of near officers.",
            "Sentry is equipped with an uniquely coded alarm and report systems.",
            "Sentry provides users with mapping features of different areas whether\n areas are to be said crime prone."
    };
    // list of background colors
    public int[]  lst_backgroundcolor = {
            Color.rgb(55,55,55),
            Color.rgb(55,55,55),
            Color.rgb(55,55,55),
            Color.rgb(55,55,55)
    };


    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);
        LinearLayout layoutslide = (LinearLayout) view.findViewById(R.id.slidelinearlayout);
        ImageView imgslide = (ImageView)  view.findViewById(R.id.slideimg);
        TextView txttitle= (TextView) view.findViewById(R.id.txttitle);
        TextView description = (TextView) view.findViewById(R.id.txtdescription);
        layoutslide.setBackgroundColor(lst_backgroundcolor[position]);
        imgslide.setImageResource(lst_images[position]);
        txttitle.setText(lst_title[position]);
        description.setText(lst_description[position]);
        at.markushi.ui.CircleButton btnMark = (at.markushi.ui.CircleButton)view.findViewById(R.id.btnProceed);
        btnMark.setVisibility(View.INVISIBLE);
        if(position>2){
            Log.v("FUCKING HELL","PLEASE WORK");
            btnMark.setVisibility(View.VISIBLE);
            btnMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(container.getContext(),LoginActivity.class);
                    container.getContext().startActivity(intent);
                }
            });

        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
