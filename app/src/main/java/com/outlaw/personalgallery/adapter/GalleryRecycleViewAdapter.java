/*
 *
 *   Developed by Ben Slama Jihed
 *   ben.slama.jihed@gmail.com
 *   https://github.com/benslamajihed
 *   Copyright (c) 2016
 *
 */

package com.outlaw.personalgallery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.firebase.client.Query;
import com.firebase.client.annotations.Nullable;
import com.outlaw.personalgallery.R;
import com.outlaw.personalgallery.entities.Image;
import com.outlaw.personalgallery.utils.Utils;

import java.util.ArrayList;

/**
 * Created by jihedbenslama on 19/03/2016.
 */
public class GalleryRecycleViewAdapter extends FirebaseRecyclerAdapter<GalleryRecycleViewAdapter.ItemViewHolder, Image> {

    Context context;


    public GalleryRecycleViewAdapter(Context context, Query query, Class<Image> itemClass, @Nullable ArrayList<Image> items,
                                     @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.context = context;
    }

    @Override
    public GalleryRecycleViewAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_recycle_image, parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GalleryRecycleViewAdapter.ItemViewHolder holder, final int position) {

        final Image item = getItem(position);

        Utils.loadBitmap(context, item.getStringImage(), holder._imagePreview);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click" + position, Toast.LENGTH_SHORT).show();
                popWindowd(context, item.getStringImage());
            }
        });

    }


    @Override
    protected void itemAdded(Image item, String key, int position) {
        Log.d("MyAdapter", "Added a new item to the adapter.");
        notifyItemInserted(position);
        notifyDataSetChanged();

    }

    @Override
    protected void itemChanged(Image oldItem, Image newItem, String key, int position) {
        Log.d("MyAdapter", "Changed an item.");
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    @Override
    protected void itemRemoved(Image item, String key, int position) {
        Log.d("MyAdapter", "Removed an item from the adapter.");
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    protected void itemMoved(Image item, String key, int oldPosition, int newPosition) {
        Log.d("MyAdapter", "Moved an item.");
        notifyItemMoved(oldPosition, newPosition);
        notifyDataSetChanged();
    }

    public void popWindowd(Context context, String imageData) {

        LayoutInflater layoutInflater
                = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.pop_layout, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        ImageView imageViewPopup = (ImageView) popupView.findViewById(R.id.image_popup);
        Utils.loadBitmap(context, imageData, imageViewPopup);
        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        protected ImageView _imagePreview;

        public ItemViewHolder(View itemView) {
            super(itemView);
            _imagePreview = (ImageView) itemView.findViewById(R.id.image_preview);

        }
    }
}
