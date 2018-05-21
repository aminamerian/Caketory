package a2.thesis.com.caketory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;

import a2.thesis.com.caketory.Entity.ItemProduct;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Utils.CustomRequest;
import a2.thesis.com.caketory.Utils.PrefSingleton;

public class ProductActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ImageView errorImageView;
    private Typeface yekanFont;
    private ImageLoader imageLoader;

    private BottomSheetBehavior sheetBehavior;

    private Long productId = null;
    private int orderWeight = 1;

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

        Button addToBasketButton = findViewById(R.id.button_addToBasket);
        TextView verifiedText = findViewById(R.id.textView_verified);
        TextView storeText = findViewById(R.id.textView_store);
        TextView ShippingText = findViewById(R.id.textView_shipping);

        addToBasketButton.setTypeface(yekanFont);
        verifiedText.setTypeface(yekanFont);
        storeText.setTypeface(yekanFont);
        ShippingText.setTypeface(yekanFont);


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

        addToBasketButton.setOnClickListener(new View.OnClickListener() {
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

        Map<String, String> params = new HashMap<>();
        params.put("access_token", PrefSingleton.getInstance(this).getAccessToken());
        params.put("product_id", String.valueOf(productId));

        CustomRequest requestProduct = new CustomRequest(Request.Method.POST, Constants.productAPI, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateProductObject(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "sec21: " + response.toString());
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

        populateBottomSheet(product);

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

    private void populateBottomSheet(final ItemProduct product) {

        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final Animation anim_error = AnimationUtils.loadAnimation(ProductActivity.this, R.anim.error_anim);

        TextView title = findViewById(R.id.textView_bottomSheet_title);
        TextView subtitle = findViewById(R.id.textView_bottomSheet_subtitle);
        final TextView price = findViewById(R.id.textView_bottomSheet_price);
        final TextView weight = findViewById(R.id.textView_bottomSheet_weight);
        TextView deliveryAddress = findViewById(R.id.textView_bottomSheet_deliveryAddress);
        TextView address = findViewById(R.id.textView_bottomSheet_address);
        Button finalizeOrder = findViewById(R.id.button_finalizeOrder);

        final ImageButton increment = findViewById(R.id.imageButton_increment);
        final ImageButton decrement = findViewById(R.id.imageButton_decrement);

        title.setTypeface(yekanFont);
        subtitle.setTypeface(yekanFont);
        price.setTypeface(yekanFont);
        weight.setTypeface(yekanFont);
        deliveryAddress.setTypeface(yekanFont);
//        address.setTypeface(yekanFont);
        finalizeOrder.setTypeface(yekanFont);

        title.setText(product.getProductName());
        subtitle.setText(product.getProductDescription());
        price.setText(product.getProductPrice() + " تومان");
        weight.setText(orderWeight + " کیلوگرم");

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderWeight < 10) {
                    orderWeight++;
                    price.setText(product.getProductPrice() * orderWeight + " تومان");
                    weight.setText(orderWeight + " کیلوگرم");
                    vibrator.vibrate(50);
                } else {
                    increment.startAnimation(anim_error);
                    Toast.makeText(ProductActivity.this, "بیشتر از ده کیلوگرم ممکن نیست!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderWeight > 1) {
                    orderWeight--;
                    price.setText(product.getProductPrice() * orderWeight + " تومان");
                    weight.setText(orderWeight + " کیلوگرم");
                    vibrator.vibrate(100);
                } else {
                    decrement.startAnimation(anim_error);
                    Toast.makeText(ProductActivity.this, "کمتر از یک کیلوگرم ممکن نیست!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        finalizeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerOrder(productId, orderWeight);
            }
        });

    }

    private void registerOrder(Long productId, int orderWeight) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", PrefSingleton.getInstance(this).getAccessToken());
        params.put("product_id", String.valueOf(productId));
        params.put("quantity", String.valueOf(orderWeight));

        CustomRequest requestProduct = new CustomRequest(Request.Method.POST, Constants.addOrderAPI, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("enough")) {
                        if (response.getBoolean("successful")) {
                            Toast.makeText(ProductActivity.this,
                                    "سفارش شما با موقثیت ثبت شد.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductActivity.this, OrderActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProductActivity.this, "خطا در ثبت سفارش!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.coordinatorLayout), "موجودی انبار ناکافیست!", Snackbar.LENGTH_LONG);
                        View view = snackbar.getView();
                        TextView snackbarTextView = view.findViewById(android.support.design.R.id.snackbar_text);
                        snackbarTextView.setTextColor(Color.WHITE);
                        snackbarTextView.setTypeface(yekanFont);
                        snackbarTextView.setTextSize(getResources().getDimension(R.dimen.snackbar_text));
                        snackbar.show();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("amina2", "register order request: " + response.toString());
                tryAgain();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(requestProduct);
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
