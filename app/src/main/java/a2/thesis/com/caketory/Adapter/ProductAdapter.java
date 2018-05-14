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
import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.R;

/**
 * Created by Amin on 24/01/2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private ProductAdapterListener productAdapterListener;
    private List<ItemProduct> productsList;
    private Typeface yekanFont;

    private ImageLoader imageLoader;

    public ProductAdapter(Context context, ProductAdapterListener listener, List<ItemProduct> productsList) {
        this.context = context;
        productAdapterListener = listener;
        this.productsList = productsList;
        yekanFont = Typeface.createFromAsset(context.getAssets(), "fonts/b_yekan.ttf");
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ItemProduct itemProduct = productsList.get(position);

        holder.name.setText(itemProduct.getProductName());
        holder.price.setText(itemProduct.getProductPrice() + " تومان");

        holder.name.setTypeface(yekanFont);
        holder.price.setTypeface(yekanFont);

        String imagePath = itemProduct.getProductImage();
        if (imagePath != null) {
            imageLoader.get(Constants.imagesDirectory + imagePath, new ImageLoader.ImageListener() {
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

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productAdapterListener.onItemClicked(productsList.get(holder.getAdapterPosition()).getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public interface ProductAdapterListener {
        void onItemClicked(long id);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        ImageView image;
        TextView name, price;

        MyViewHolder(View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card_product);
            image = itemView.findViewById(R.id.card_image);
            name = itemView.findViewById(R.id.card_title);
            price = itemView.findViewById(R.id.card_price);

        }
    }
}
