package a2.thesis.com.caketory.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import java.util.List;

import a2.thesis.com.caketory.Constants;
import a2.thesis.com.caketory.Entity.ItemCategory;
import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.R;

/**
 * Created by Amin on 24/01/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private Context context;
    private List<ItemCategory> categoryList;
    private ImageLoader imageLoader;

    public CategoryAdapter(Context context, List<ItemCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        ItemCategory itemCategory = categoryList.get(position);

        String imagePath = itemCategory.getCategoryImage();
        if (imagePath != null) {
            imageLoader.get(imagePath, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    // loading itemCategory cover using Glide library - efficient and no lag while scrolling
                    Glide.with(context).load(response.getRequestUrl()).into(holder.image);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("amina2", error.toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image_cat);
        }
    }
}
