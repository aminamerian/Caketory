package a2.thesis.com.caketory;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import a2.thesis.com.caketory.Adapter.ProductAdapter;
import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;

public class MainActivity extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private List<ItemProduct> productsList;
    private String productAPI;

    private Typeface yekanFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yekanFont = Typeface.createFromAsset(getAssets(),"fonts/b_yekan.ttf");

        TextView bestSellingText = findViewById(R.id.text_bestSelling);
        TextView categoriesText = findViewById(R.id.text_categories);
        bestSellingText.setTypeface(yekanFont);
        categoriesText.setTypeface(yekanFont);

        productAPI = Constants.productAPI + "?access=" + Constants.AccessKey;

        RecyclerView productRecycler = findViewById(R.id.recycler_product);

        productsList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productsList);
//        productRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        productRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        productRecycler.setItemAnimator(new DefaultItemAnimator());
        productRecycler.setAdapter(productAdapter);

        populateData();
    }

    private void populateData() {
        JsonObjectRequest request;
        request = new JsonObjectRequest(Request.Method.GET, productAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseJSONResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("amina2", "sec1: "+error.toString());
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void parseJSONResponse(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("data");
            JSONObject mObject;
            for (int i = 0; i < array.length(); i++) {
                mObject = array.getJSONObject(i).getJSONObject("product");
                long id = mObject.getLong("product_id");
                String name = mObject.getString("product_name");
                String image = Constants.imagesDirectory + mObject.getString("product_image");
                int price = mObject.getInt("product_price");
                Log.d("amina2",id+" ... "+name+" ... "+image);
                productsList.add(new ItemProduct(id, name, image, price));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            productAdapter.notifyDataSetChanged();
        }
    }
}
