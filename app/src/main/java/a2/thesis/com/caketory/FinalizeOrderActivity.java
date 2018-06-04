package a2.thesis.com.caketory;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Utils.CustomRequest;
import a2.thesis.com.caketory.Utils.PrefSingleton;

public class FinalizeOrderActivity extends AppCompatActivity {

    private Typeface yekanFont;
    private TextView name, address, deliveryPrice, finalPrice;
    private int totalOrderPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_order);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        totalOrderPrice = getIntent().getIntExtra("FINAL_ORDER_PRICE", 0);

        TextView toolbarTitle, finalOrderPrice, personalInfo, phoneNumber, deliveryInfo;
        toolbarTitle = findViewById(R.id.textView_toolbarTitle);
        finalOrderPrice = findViewById(R.id.textView_finalOrderPrice);
        finalPrice = findViewById(R.id.textView_finalPrice);
        personalInfo = findViewById(R.id.textView_personalInfo);
        phoneNumber = findViewById(R.id.textView_phoneNumber);
        name = findViewById(R.id.textView_name);
        address = findViewById(R.id.textView_address);
        deliveryInfo = findViewById(R.id.textView_deliveryInfo);
        deliveryPrice = findViewById(R.id.textView_deliveryPrice);
        RadioButton radioPeyk, radioCar;
        radioPeyk = findViewById(R.id.radio_peyk);
        radioCar = findViewById(R.id.radio_car);
        CheckBox checkBox = findViewById(R.id.checkbox_withFactor);
        Button payment = findViewById(R.id.button_payment);

        toolbarTitle.setTypeface(yekanFont);
        finalOrderPrice.setTypeface(yekanFont);
        finalPrice.setTypeface(yekanFont);
        personalInfo.setTypeface(yekanFont);
        phoneNumber.setTypeface(yekanFont);
        name.setTypeface(yekanFont);
        address.setTypeface(yekanFont);
        deliveryInfo.setTypeface(yekanFont);
        deliveryPrice.setTypeface(yekanFont);
        radioPeyk.setTypeface(yekanFont);
        radioCar.setTypeface(yekanFont);
        checkBox.setTypeface(yekanFont);
        payment.setTypeface(yekanFont);

        finalOrderPrice.setText("هزینه سفارش: " + totalOrderPrice + " تومان");
        //default final price which include 1000 Toman for delivery price
        finalPrice.setText("هزینه قابل پرداخت: " + (totalOrderPrice + 1000) + " تومان");
        deliveryPrice.setText("هزینه ی ارسال: 1000 تومان");
        phoneNumber.setText(String.valueOf(PrefSingleton.getInstance(this).getPhoneNumber()));
        fetchData();

        findViewById(R.id.imageButton_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinalizeOrderActivity.this, ProfileActivity.class));
            }
        });
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
        params.put("user_data", "profile");

        VolleySingleton.getInstance(this).addToRequestQueue(
                new CustomRequest(Request.Method.POST, Constants.userDataAPI, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            name.setText(response.getString("name"));
                            address.setText(response.getString("address"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError response) {
                        Log.d("amina2", "fetch profile data 2: " + response.toString());
                    }
                }));
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int sum = 0;
        switch (view.getId()) {
            case R.id.radio_peyk:
                if (checked) {
                    sum = totalOrderPrice + 1000;
                    deliveryPrice.setText("هزینه ی ارسال: 1000 تومان");
                }
                break;
            case R.id.radio_car:
                if (checked) {
                    sum = totalOrderPrice + 3000;
                    deliveryPrice.setText("هزینه ی ارسال: 3000 تومان");
                }
                break;
        }
        finalPrice.setText("هزینه قابل پرداخت: " + sum + " تومان");
    }
}
