package a2.thesis.com.caketory;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;

public class ProductActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ImageView errorImageView;
    private Typeface yekanFont;
    private ImageLoader imageLoader;

    private BottomSheetBehavior sheetBehavior;

    private Long productId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        imageLoader = VolleySingleton.getInstance(this).getImageLoader();

        progressBar = findViewById(R.id.progressBar);
        errorImageView = findViewById(R.id.imageView_ic_error);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  //put back button in the toolbar
            actionBar.setDisplayShowTitleEnabled(false); //hide actionBar title
        }

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitleEnabled(false);

        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float offset = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
                toolbar.getBackground().setAlpha((int) (offset * 255));

            }
        });

        productId = getIntent().getLongExtra("PRODUCT_ID", 0);
        fetchData(productId);

        TextView addToBasketText = findViewById(R.id.text_addToBasket);
        TextView verifiedText = findViewById(R.id.textView_verified);
        TextView storeText = findViewById(R.id.textView_store);
        TextView ShippingText = findViewById(R.id.textView_shipping);

        addToBasketText.setTypeface(yekanFont);
        verifiedText.setTypeface(yekanFont);
        storeText.setTypeface(yekanFont);
        ShippingText.setTypeface(yekanFont);

        CardView addToBasketCart = findViewById(R.id.cart_addToBasket);

        LinearLayout layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        addToBasketCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchData(long productId) {
        JsonObjectRequest requestProduct;
        requestProduct = new JsonObjectRequest(Request.Method.GET, Constants.productAPI +
                "&product_id=" + productId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateProductObject(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("amina2", "sec21: " + error.toString());
                tryAgain();
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(requestProduct);
    }

    private void populateProductObject(JSONObject response) {
        try {
            JSONObject mObject = response.getJSONObject("data");
            long id = mObject.getLong("product_id");
            String name = mObject.getString("product_name");
            long catId = mObject.getLong("category_id");
            String image = Constants.imagesDirectory + mObject.getString("product_image");
            String image2 = mObject.getString("product_image2");
            int price = mObject.getInt("product_price");
            int stock = mObject.getInt("product_stock");
            String des = mObject.getString("product_description");
            String desDetail = mObject.getString("product_des_detail");
            populateViews(new ItemProduct(id, name, catId, image, image2, price, stock, des, desDetail));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateViews(ItemProduct product) {

        TextView productName = findViewById(R.id.textView_name);
        TextView productDes = findViewById(R.id.textView_des);
        TextView productDesDetail = findViewById(R.id.textView_desDetail);
        TextView productPrice = findViewById(R.id.textView_price);
        productName.setTypeface(yekanFont);
        productDes.setTypeface(yekanFont);
        productDesDetail.setTypeface(yekanFont);
        productPrice.setTypeface(yekanFont);

        productName.setText(product.getProductName());
        productDes.setText(product.getProductDescription());
        productDesDetail.setText(product.getProductDesDetail());
        productPrice.setText(product.getProductPrice() + " تومان");

        String image2Path = product.getProductImage2();
        if (image2Path != null && !image2Path.equals("")) {
            imageLoader.get(Constants.imagesDirectory + image2Path, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Glide.with(ProductActivity.this)
                            .load(response.getRequestUrl())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    Log.d("amina2", "Glide: error");
                                    tryAgain();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    Log.d("amina2", "Glide: successful -is from memory cache: " + isFromMemoryCache);
                                    loadContent();
                                    return false;
                                }
                            })
                            .into((ImageView) findViewById(R.id.imageView_header));
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("amina2", error.toString());
                    tryAgain();
                }
            });
        } else {
            loadContent();
        }
    }

    public void tryAgain() {
        progressBar.setVisibility(View.GONE);
        errorImageView.setVisibility(View.VISIBLE);
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinatorLayout), "خطا در دریافت اطلاعات از سرور", Snackbar.LENGTH_INDEFINITE)
                .setAction("تلاش مجدد", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchData(productId);
                        errorImageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
        //customizing snack bar
        View view = snackbar.getView();
        TextView snackbarTextView = view.findViewById(android.support.design.R.id.snackbar_text);
        TextView snackbarActionTextView = view.findViewById(android.support.design.R.id.snackbar_action);
        snackbarTextView.setTextColor(Color.WHITE);
        snackbarActionTextView.setTextColor(Color.GREEN);
        snackbarTextView.setTypeface(yekanFont);
        snackbarActionTextView.setTypeface(yekanFont);
        snackbarTextView.setTextSize(getResources().getDimension(R.dimen.snackbar_text));
        snackbarActionTextView.setTextSize(getResources().getDimension(R.dimen.snackbar_text));
        snackbar.show();
    }

    public void loadContent() {
        progressBar.setVisibility(View.GONE);
        appBarLayout.animate().setDuration(500).alpha(1);
        findViewById(R.id.nestedScrollView).animate().setDuration(500).alpha(1);
    }
}
