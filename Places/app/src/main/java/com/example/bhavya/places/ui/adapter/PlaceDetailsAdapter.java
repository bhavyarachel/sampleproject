package com.example.bhavya.places.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.Reviews;


import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bhavya on 22/9/16.
 */

public class PlaceDetailsAdapter extends BaseAdapter {


    private ViewHolder holder;
    private Context ctxt;
    private int resource;
    private Reviews[] reviews;


    static class ViewHolder {
        @BindView(R.id.text_personname)
        TextView mTextViewPersonName;
        @BindView(R.id.person_reviews)
        TextView mTextViewReviews;
        @BindView(R.id.text_date)
        TextView mTextDateOfReview;
        @BindView(R.id.showrating)
        RatingBar mShowRating;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public PlaceDetailsAdapter(Context context, int resource, Reviews[] reviews) {
        this.ctxt = context;
        this.resource = resource;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.length;
    }

    @Override
    public Object getItem(int position) {
        return reviews[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_place_reviews,
                    parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.mTextViewPersonName.setText(reviews[position].getAuthor_name());
        holder.mShowRating.setRating(Float.parseFloat(reviews[position].getRating()));
        holder.mTextViewReviews.setText(reviews[position].getText());
        Long timestamp = Long.valueOf(reviews[position].getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
        String dateString = formatter.format((timestamp));
        holder.mTextDateOfReview.setText(dateString);


        return view;
    }


}
