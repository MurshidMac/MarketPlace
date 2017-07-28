package com.example.gabbygiordano.marketplace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.gabbygiordano.marketplace.R.layout.item;

/**
 * Created by tanvigupta on 7/12/17.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> mItems;

    static Context context;
    static Context mContext;

    // pass Items array into constructor
    public ItemAdapter(List<Item> items, Context context) {
        mItems = items;
        mContext = context;
    }

    // inflate layout and cache references into ViewHolder for each row
    // only called when entirely new row is to be created
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    // bind values based on position of the element
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // get the data according to position
        final Item item = mItems.get(position);

        if (item == null) {
            Log.e("ItemAdapter", "ITEM NULL");
        } else {
            Log.e("ItemAdapter", "Item not null");
            if (item.getOwner() == null) {
                Log.e("ItemAdapter", "USER NULL");
            } else {
                Log.e("ItemAdapter", "Nothing is nullllll");
            }
        }

        // populate the views according to item data
        holder.tvItemName.setText(item.getItemName());
        holder.tvPrice.setText(item.getPrice());
        holder.tvSeller.setText(item.getOwner().getString("name"));
        holder.tvTimeAgo.setText(item.getOwner().getString("_created_at"));

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> favoriteItems = (ArrayList) user.get("favoriteItems");
        if (favoriteItems.contains(item.getObjectId())) {
            // favorited
            holder.ibFavorite.setImageResource(R.drawable.ic_fav);
            holder.ibFavorite.setColorFilter(Color.rgb(255,87,34));
        } else {
            // unfavorited
            holder.ibFavorite.setImageResource(R.drawable.ic_unfav);
            holder.ibFavorite.setColorFilter(Color.rgb(155,155,155));
        }

        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> favoriteItems = (ArrayList) user.get("favoriteItems");

                if (favoriteItems.contains(item.getObjectId())) {
                    // unfavorite
                    holder.ibFavorite.setImageResource(R.drawable.ic_unfav);
                    holder.ibFavorite.setColorFilter(Color.rgb(155,155,155));

                    Toast.makeText(getContext(), "Unfavorited", Toast.LENGTH_LONG).show();

                    favoriteItems.remove(item.getObjectId());
                    user.put("favoriteItems", favoriteItems);
                    user.saveInBackground();
                } else {
                    // favorite
                    holder.ibFavorite.setImageResource(R.drawable.ic_fav);
                    holder.ibFavorite.setColorFilter(Color.rgb(255,87,34));

                    Toast.makeText(getContext(), "Favorited", Toast.LENGTH_LONG).show();

                    favoriteItems.add(item.getObjectId());
                    user.put("favoriteItems", favoriteItems);
                    user.saveInBackground();
                }
            }
        });

        holder.tvTimeAgo.setText(getRelativeTimeAgo(item.getCreatedAt()));

        if(item.getImage() != null)
        {
            String imageUri = item.getImage().getUrl();

            Glide
                    .with(context)
                    .load(imageUri)
                    .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 20, 0))
                    .placeholder(R.drawable.ic_camera)
                    .error(R.drawable.ic_camera)
                    .into(holder.ivItemImage);
        }
        else
        {
            Glide
                    .with(context)
                    .load(R.drawable.ic_camera)
                    .placeholder(R.drawable.ic_camera)
                    .error(R.drawable.ic_camera)
                    .into(holder.ivItemImage);
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(Date date) {
        String relativeDate = "";

        long createdDate = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(createdDate,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();

        relativeDate = relativeDate.replace(" ago", "");
        relativeDate = relativeDate.replace(" sec.", "s");
        relativeDate = relativeDate.replace(" min.", "m");
        relativeDate = relativeDate.replace(" hr.", "h");
        relativeDate = relativeDate.replace(" days", "d");

        if (relativeDate.equals("Yesterday")) {
            relativeDate = "1d";
        }

        return relativeDate;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Item> list) {
        mItems.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Item item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    public static Context getContext() {
        return mContext;
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView ivItemImage;
        public TextView tvItemName;
        public TextView tvSeller;
        public TextView tvPrice;
        public TextView tvTimeAgo;
        Item thisItem;

        ImageButton ibFavorite;

        // constructor
        public ViewHolder(View itemView) {
            super(itemView);

            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvSeller = itemView.findViewById(R.id.tvSeller);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);

            ibFavorite = itemView.findViewById(R.id.ibFavorite);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if( position != RecyclerView.NO_POSITION){
                thisItem = mItems.get(position);
            }
            String id = thisItem.getObjectId();
            Intent i = new Intent(context, DetailsActivity.class);
            i.putExtra("ID", id);

            String name = "sharedActivityTransition";
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ivItemImage, name);

            context.startActivity(i, options.toBundle());
        }
        @Override
        @SuppressLint("NewApi")
        public boolean onLongClick(View view){
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                thisItem = mItems.get(position);
            }
            if(thisItem.getOwner().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                thisItem.deleteInBackground();
                notifyItemRemoved(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Item Deleted!", Toast.LENGTH_LONG).show();

            }


            return true;
        }


    }
}
