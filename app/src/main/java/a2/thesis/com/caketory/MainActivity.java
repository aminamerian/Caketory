package a2.thesis.com.caketory;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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
import java.util.List;
import java.util.Map;

import a2.thesis.com.caketory.Adapter.CategoryAdapter;
import a2.thesis.com.caketory.Adapter.ProductAdapter;
import a2.thesis.com.caketory.Adapter.SliderViewPagerAdapter;
import a2.thesis.com.caketory.Entity.ItemCategory;
import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Utils.CustomRequest;
import a2.thesis.com.caketory.Utils.CustomTypefaceSpan;
import a2.thesis.com.caketory.Utils.PrefSingleton;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ProductAdapter.ProductAdapterListener, CategoryAdapter.CategoryAdapterListener {

    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;

    private List<ItemProduct> productsList;
    private List<ItemCategory> categoryList;

    private DrawerLayout drawerLayout;
    private TextView badgeText;
    private Typeface yekanFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);  //hide actionBar title
        }
        TextView bestSellingText = findViewById(R.id.text_bestSelling);
        TextView categoriesText = findViewById(R.id.text_categories);
        bestSellingText.setTypeface(yekanFont);
        categoriesText.setTypeface(yekanFont);

        drawerLayout = findViewById(R.id.drawer);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //initialize standard navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        //set drawer's item click listener
        navigationView.setNavigationItemSelectedListener(this);
        setTypefaceToNavigationView(navigationView);
        //hiding navigation view scroll bar
        NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navigationMenuView.setVerticalScrollBarEnabled(false);

        RecyclerView productRecycler = findViewById(R.id.recycler_product);
        productsList = new ArrayList<>();
        productAdapter = new ProductAdapter(productRecycler.getContext(), this, productsList);
//        productRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        productRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productRecycler.setItemAnimator(new DefaultItemAnimator());
        productRecycler.setAdapter(productAdapter);


        RecyclerView categoryRecycler = findViewById(R.id.recycler_category);
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        //set nested scrolling disable to recyclerView work smoothly inside the nestedScrollView
        categoryRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 0 ? 2 : 1;
            }
        });
        categoryRecycler.setLayoutManager(layoutManager);
        categoryRecycler.setAdapter(categoryAdapter);

        fetchData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem badge = menu.findItem(R.id.item_badge);
        badgeText = badge.getActionView().findViewById(R.id.menu_badge);
        badgeText.setTypeface(yekanFont);

        badge.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOrderItemNumberBadge();
    }

    private void fetchData() {

        CustomRequest requestUserData, requestHeaderImage, requestProduct, requestCat;

        Map<String, String> params = new HashMap<>();
        params.put("access_token", PrefSingleton.getInstance(this).getAccessToken());
        params.put("orders_number", "only");

        requestUserData = new CustomRequest(Request.Method.POST, Constants.userDataAPI, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateUserData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "orders number request: " + response.toString());
            }
        });

        Map<String, String> accessToken = new HashMap<>();
        accessToken.put("access_token", PrefSingleton.getInstance(this).getAccessToken());

        requestHeaderImage = new CustomRequest(Request.Method.POST, Constants.headerImageAPI, accessToken, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateHeaderImageList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "sec2: " + response.toString());
            }
        });
        requestProduct = new CustomRequest(Request.Method.POST, Constants.productAPI, accessToken, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateProductList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "sec3: " + response.toString());
            }
        });

        requestCat = new CustomRequest(Request.Method.POST, Constants.categoryAPI, accessToken, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateCategoryList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "sec4: " + response.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(requestUserData);
        VolleySingleton.getInstance(this).addToRequestQueue(requestHeaderImage);
        VolleySingleton.getInstance(this).addToRequestQueue(requestProduct);
        VolleySingleton.getInstance(this).addToRequestQueue(requestCat);
    }

    private void populateUserData(JSONObject response) {
        int orderItemNum = 0;
        try {
            orderItemNum = response.getInt("order_items_num");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Constants.setOrderItemNumber(orderItemNum);
        setOrderItemNumberBadge();
    }


    private void populateHeaderImageList(JSONObject response) {
        ArrayList<String> images = new ArrayList<>();
        try {
            JSONArray array = response.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                images.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViewPager viewPager = findViewById(R.id.slider_viewPager);
        viewPager.setAdapter(new SliderViewPagerAdapter(this, images));
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }

    private void populateProductList(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("data");
            JSONObject mObject;
            for (int i = 0; i < array.length(); i++) {
                mObject = array.getJSONObject(i).getJSONObject("product");
                long id = mObject.getLong("product_id");
                String name = mObject.getString("product_name");
                String image = mObject.getString("product_image");
                int price = mObject.getInt("product_price");
                productsList.add(new ItemProduct(id, name, image, price));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            productAdapter.notifyDataSetChanged();
        }
    }

    private void populateCategoryList(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("data");
            JSONObject mObject;
            for (int i = 0; i < array.length(); i++) {
                mObject = array.getJSONObject(i).getJSONObject("category");
                long id = mObject.getLong("category_id");
                String name = mObject.getString("category_name");
                String image = mObject.getString("category_image");
                categoryList.add(new ItemCategory(id, name, image));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            categoryAdapter.notifyDataSetChanged();
        }
    }

    private void setOrderItemNumberBadge() {
        if (badgeText != null) {
            if (Constants.getOrderItemNumber() == 0) {
                badgeText.setVisibility(View.INVISIBLE);
            } else {
                badgeText.setVisibility(View.VISIBLE);
                badgeText.setText(String.valueOf(Constants.getOrderItemNumber()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        //when the user click the back button, drawer will be close if it was open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //navigation drawer's items click listener
        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_shb) {
            startActivity(new Intent(this, OrderActivity.class));
        } else if (id == R.id.nav_fav) {
            Toast.makeText(this, "Favorite Items", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_history) {
            Toast.makeText(this, "Orders History", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_message) {
            Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_cake) {
            goToCategoryActivity(1, "کیک");
        } else if (id == R.id.nav_dry) {
            goToCategoryActivity(2, "شیرینی خشک");
        } else if (id == R.id.nav_wet) {
            goToCategoryActivity(3, "شیرینی تر");
        } else if (id == R.id.nav_des) {
            goToCategoryActivity(4, "دسر");
        }
        //close drawer after any selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setTypefaceToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", yekanFont), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void setTypefaceToNavigationView(NavigationView navigationView) {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for applying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    setTypefaceToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            setTypefaceToMenuItem(mi);
        }
    }


    @Override
    public void onItemClicked(long id) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("PRODUCT_ID", id);
        startActivity(intent);
    }

    @Override
    public void onCategoryItemClicked(long id, String name) {
        goToCategoryActivity(id, name);
    }

    private void goToCategoryActivity(long id, String name) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("CAT_ID", id);
        intent.putExtra("CAT_NAME", name);
        startActivity(intent);
    }
}
