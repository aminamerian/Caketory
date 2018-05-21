package a2.thesis.com.caketory;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a2.thesis.com.caketory.Adapter.CategoryProductsAdapter;
import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Utils.CustomRequest;
import a2.thesis.com.caketory.Utils.PrefSingleton;

public class CategoryActivity extends AppCompatActivity implements CategoryProductsAdapter.CategoryProductsAdapterListener {

    private List<ItemProduct> catProductsList;
    private CategoryProductsAdapter catProductsAdapter;

    private long catId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Typeface yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        Toolbar toolbar = findViewById(R.id.cat_toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  //put back button in the toolbar
            actionBar.setDisplayShowTitleEnabled(false); //hide actionBar title
        }

        TextView toolbarTitle = findViewById(R.id.cat_textView_toolbarTitle);
        toolbarTitle.setTypeface(yekanFont);
        toolbarTitle.setText(getIntent().getStringExtra("CAT_NAME"));

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.cat_toolbar_layout);
        toolbarLayout.setTitleEnabled(false);

        Button orderingButton = findViewById(R.id.button_ordering);
        Button filteringButton = findViewById(R.id.button_filtering);
        orderingButton.setTypeface(yekanFont);
        filteringButton.setTypeface(yekanFont);

        catId = getIntent().getLongExtra("CAT_ID", 0);

        RecyclerView catProductsRecycler = findViewById(R.id.recyclerView_catProducts);
        catProductsList = new ArrayList<>();
        catProductsAdapter = new CategoryProductsAdapter(this, catProductsList);
        catProductsRecycler.setNestedScrollingEnabled(false);
        catProductsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        catProductsRecycler.setItemAnimator(new DefaultItemAnimator());
        catProductsRecycler.setAdapter(catProductsAdapter);

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
        Map<String, String> params = new HashMap<>();
        params.put("access_token", PrefSingleton.getInstance(this).getAccessToken());
        params.put("category_id", String.valueOf(catId));

        CustomRequest requestCatProducts = new CustomRequest(Request.Method.POST, Constants.catProductAPI, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateCatProducts(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "sec6: " + response.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(requestCatProducts);
    }

    private void populateCatProducts(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("data");
            JSONObject mObject;
            for (int i = 0; i < array.length(); i++) {
                mObject = array.getJSONObject(i).getJSONObject("product");
                long id = mObject.getLong("product_id");
                String name = mObject.getString("product_name");
                String image = mObject.getString("product_image");
                int price = mObject.getInt("product_price");
                String des = mObject.getString("product_description");
                catProductsList.add(new ItemProduct(id, name, image, price, des));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            catProductsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onProductItemClicked(long id) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("PRODUCT_ID", id);
        startActivity(intent);
    }
}
