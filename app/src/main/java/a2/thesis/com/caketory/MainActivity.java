package a2.thesis.com.caketory;

import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;

import a2.thesis.com.caketory.Adapter.ProductAdapter;
import a2.thesis.com.caketory.Adapter.SliderViewPagerAdapter;
import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private List<ItemProduct> productsList;
    private String productAPI;

    private Typeface yekanFont;

    public ViewPager viewPager;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(" ");

        TextView bestSellingText = findViewById(R.id.text_bestSelling);
        TextView categoriesText = findViewById(R.id.text_categories);
        bestSellingText.setTypeface(yekanFont);
        categoriesText.setTypeface(yekanFont);

        viewPager = findViewById(R.id.slider_viewPager);
        viewPager.setAdapter(new SliderViewPagerAdapter(this));
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimerTask(), 5000, 5000);

        AppBarLayout appBarLayout = findViewById(R.id.appbar_layout);
        appBarLayout.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    boolean sliderCanceled = false;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        //slider runs, if only the appbar layout is completely expanded
                        if (Math.abs(verticalOffset) == 0) {
                            //expanded completely
                            if (sliderCanceled) {
                                //timer restarts, if it had been canceled
                                timer = new Timer();
                                timer.scheduleAtFixedRate(new SliderTimerTask(), 1000, 5000);
                                sliderCanceled = false;
                            }
                        } else {
                            if (!sliderCanceled) {
                                //timer cancels, if it was running
                                timer.cancel();
                                sliderCanceled = true;
                            }
                        }
                    }
                }
        );

        productAPI = Constants.productAPI + "?access=" + Constants.AccessKey;

        RecyclerView productRecycler = findViewById(R.id.recycler_product);

        productsList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productsList);
//        productRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        productRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productRecycler.setItemAnimator(new DefaultItemAnimator());
        productRecycler.setAdapter(productAdapter);

        populateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings2) {
            Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                Log.d("amina2", "sec1: " + error.toString());
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
                Log.d("amina2", id + " ... " + name + " ... " + image);
                productsList.add(new ItemProduct(id, name, image, price));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            productAdapter.notifyDataSetChanged();
        }
    }

    public class SliderTimerTask extends TimerTask {
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                        viewPager.setCurrentItem(0, true);
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                }
            });
        }
    }
}
