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

public class CategoryProductsAdapter extends RecyclerView.Adapter<CategoryProductsAdapter.MyViewHolder> {

    private Context context;
    private CategoryProductsAdapterListener categoryProductsAdapterListener;
    private List<ItemProduct> catProductsList;
    private ImageLoader imageLoader;
    private Typeface yekanFont;

    public CategoryProductsAdapter(Context context, List<ItemProduct> catProductsList) {
        this.context = context;
        this.categoryProductsAdapterListener = (CategoryProductsAdapterListener) context;
        this.catProductsList = catProductsList;
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
        yekanFont = Typeface.createFromAsset(context.getAssets(), "fonts/b_yekan.ttf");

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_products, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ItemProduct catProduct = catProductsList.get(position);
        holder.name.setText(catProduct.getProductName());
        holder.price.setText(catProduct.getProductPrice() + " تومان");
        holder.des.setText(catProduct.getProductDescription());

        holder.name.setTypeface(yekanFont);
        holder.price.setTypeface(yekanFont);
        holder.des.setTypeface(yekanFont);

        String image = catProduct.getProductImage();
        if (image != null) {
            imageLoader.get(Constants.imagesDirectory + image, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Glide.with(context).load(response.getRequestUrl()).into(holder.image);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("amina2", error.toString());
                }
            });
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryProductsAdapterListener.onProductItemClicked(catProduct.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return catProductsList.size();
    }

    public interface CategoryProductsAdapterListener {
        void onProductItemClicked(long id);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView image;
        TextView name, price, des;

        MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_cat_product);
            image = itemView.findViewById(R.id.imageView_cat_product);
            name = itemView.findViewById(R.id.textView_cat_productName);
            price = itemView.findViewById(R.id.textView_cat_productPrice);
            des = itemView.findViewById(R.id.textView_cat_productDes);
        }
    }
}
