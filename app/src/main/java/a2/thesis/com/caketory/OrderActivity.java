package a2.thesis.com.caketory;

import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import a2.thesis.com.caketory.Adapter.CategoryProductsAdapter;
import a2.thesis.com.caketory.Adapter.OrderItemsAdapter;
import a2.thesis.com.caketory.Entity.ItemOrder;
import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Utils.CustomRequest;
import a2.thesis.com.caketory.Utils.PrefSingleton;

public class OrderActivity extends AppCompatActivity {

    private ArrayList<ItemOrder> orderItemsList;
    private OrderItemsAdapter orderItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Typeface yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  //put back button in the toolbar
            actionBar.setDisplayShowTitleEnabled(false); //hide actionBar title
        }

        TextView toolbarTitle = findViewById(R.id.textView_toolbarTitle);
        toolbarTitle.setTypeface(yekanFont);

        RecyclerView orderItemsRecycler = findViewById(R.id.recyclerView_orderItems);
        orderItemsList = new ArrayList<>();
        orderItemsAdapter = new OrderItemsAdapter(this, orderItemsList);
        orderItemsRecycler.setNestedScrollingEnabled(false);
        orderItemsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        orderItemsRecycler.setItemAnimator(new DefaultItemAnimator());
        orderItemsRecycler.setAdapter(orderItemsAdapter);

        fetchData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {
        Map<String, String> param = new HashMap<>();
        param.put("access_token", PrefSingleton.getInstance(this).getAccessToken());

        CustomRequest requestCatProducts = new CustomRequest(Request.Method.POST, Constants.getOrderAPI, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateOrderItems(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "get order request: " + response.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(requestCatProducts);
    }

    private void populateOrderItems(JSONObject response) {
        try {
            if (!response.getBoolean("error")) {
                JSONArray array = response.getJSONArray("result");
                JSONObject orderItem, product;
                for (int i = 0; i < array.length(); i++) {
                    orderItem = array.getJSONObject(i).getJSONObject("order_item");
                    product = array.getJSONObject(i).getJSONObject("product");
                    orderItemsList.add(new ItemOrder(
                            orderItem.getLong("item_id"), orderItem.getLong("order_id"),
                            orderItem.getLong("product_id"), orderItem.getInt("quantity"),
                            product.getString("product_name"), product.getLong("category_id"),
                            product.getString("product_image"), product.getString("product_image2"),
                            product.getInt("product_price"), product.getString("product_description")));
                }
            } else {
                Toast.makeText(this, response.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            orderItemsAdapter.notifyDataSetChanged();
        }
    }
}
