package a2.thesis.com.caketory.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import a2.thesis.com.caketory.Entity.ItemOrder;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.R;
import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Utils.OrderItemsInterface;

/**
 * Created by Amin on 21/05/2018.
 */

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.MyViewHolder> implements OrderItemsInterface {

    private Context context;
    private List<ItemOrder> orderItemsList;
    private Typeface yekanFont;

    public OrderItemsAdapter(Context context, List<ItemOrder> orderItemsList) {
        this.context = context;
        this.orderItemsList = orderItemsList;
        yekanFont = Typeface.createFromAsset(context.getAssets(), "fonts/b_yekan.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ItemOrder itemOrder = orderItemsList.get(position);
        holder.name.setText(itemOrder.getProductName());
        holder.price.setText(itemOrder.getProductPrice() + " تومان");
        holder.quantity.setText(itemOrder.getQuantity() + " کیلوگرم");
        holder.des.setText(itemOrder.getProductDescription());
        holder.totalPrice.setText(itemOrder.getProductPrice() * itemOrder.getQuantity() + " تومان");
        holder.totalDiscount.setText("0 تومان");
        holder.finalPrice.setText(itemOrder.getProductPrice() * itemOrder.getQuantity() + " تومان");
        holder.slideToDelete.setText(Html.fromHtml("برای <font color='#F1383E'>حذف</font> به سمت راست بکشید"));

        holder.name.setTypeface(yekanFont);
        holder.price.setTypeface(yekanFont);
        holder.priceW.setTypeface(yekanFont);
        holder.quantity.setTypeface(yekanFont);
        holder.quantityW.setTypeface(yekanFont);
        holder.des.setTypeface(yekanFont);
        holder.totalPrice.setTypeface(yekanFont);
        holder.totalPriceW.setTypeface(yekanFont);
        holder.totalDiscount.setTypeface(yekanFont);
        holder.totalDiscountW.setTypeface(yekanFont);
        holder.finalPrice.setTypeface(yekanFont);
        holder.finalPriceW.setTypeface(yekanFont);
        holder.slideToDelete.setTypeface(yekanFont);

        String imagePath = Constants.imagesDirectory + itemOrder.getProductImage();
        Glide.with(context)
                .load(imagePath)
                .fitCenter()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return orderItemsList.size();
    }

    @Override
    public boolean onItemDismiss(int position) {
        orderItemsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderItemsList.size());
        return orderItemsList.isEmpty();
    }

    @Override
    public long getOrderItemId(int position) {
        return orderItemsList.get(position).getItemId();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name, price, priceW, quantity, quantityW, des, totalPrice, totalPriceW,
                totalDiscount, totalDiscountW, finalPrice, finalPriceW, slideToDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView_order);
            name = itemView.findViewById(R.id.textView_order_productName);
            price = itemView.findViewById(R.id.textView_order_productPrice);
            priceW = itemView.findViewById(R.id.textView_order_w_productPrice);
            des = itemView.findViewById(R.id.textView_order_productDes);
            totalPrice = itemView.findViewById(R.id.textView_totalPrice);
            totalPriceW = itemView.findViewById(R.id.textView_w_totalPrice);
            quantity = itemView.findViewById(R.id.textView_order_quantity);
            quantityW = itemView.findViewById(R.id.textView_order_w_quantity);
            totalDiscount = itemView.findViewById(R.id.textView_discount);
            totalDiscountW = itemView.findViewById(R.id.textView_w_discount);
            finalPrice = itemView.findViewById(R.id.textView_finalPrice);
            finalPriceW = itemView.findViewById(R.id.textView_w_finalPrice);
            slideToDelete = itemView.findViewById(R.id.textView_slideToDelete);
        }
    }
}
