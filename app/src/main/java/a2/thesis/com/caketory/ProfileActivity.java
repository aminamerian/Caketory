package a2.thesis.com.caketory;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProfileActivity extends AppCompatActivity {

    private EditText name, email, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Typeface yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        TextView toolbarTitle = findViewById(R.id.textView_toolbarTitle);
        final Button register = findViewById(R.id.button_register);
        toolbarTitle.setTypeface(yekanFont);
        register.setTypeface(yekanFont);

        TextView logout, addAccount, phoneNumber;
        logout = findViewById(R.id.textView_logout);
        addAccount = findViewById(R.id.textView_addAccount);
        phoneNumber = findViewById(R.id.textView_phoneNumber);
        name = findViewById(R.id.editText_name);
        email = findViewById(R.id.editText_email);
        address = findViewById(R.id.editText_address);

        phoneNumber.setTypeface(yekanFont);
        logout.setTypeface(yekanFont);
        addAccount.setTypeface(yekanFont);
        name.setTypeface(yekanFont);
        address.setTypeface(yekanFont);

        phoneNumber.setText(PrefSingleton.getInstance(this).getPhoneNumber());

        findViewById(R.id.cardView_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("آیا قصد خروج از حساب کاربری خود را دارید؟");
                builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                }).setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //no
                    }
                }).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()) {
                    name.requestFocus();
                    name.setError("نام نمی تواند خالی باشد");
                } else if (address.getText().toString().isEmpty()) {
                    address.requestFocus();
                    address.setError("آدرس نمی تواند خالی باشد");
                } else {
                    register(name.getText().toString(), email.getText().toString(), address.getText().toString());
                }
            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_account_active, 0);
                } else {
                    name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_account, 0);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_email_active, 0);
                } else {
                    email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_email, 0);
                }
            }
        });

        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    address.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_location_active, 0);
                } else {
                    address.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_location, 0);
                }
            }
        });

        //if user have been registered, populate the editTexts with previous data
        if (PrefSingleton.getInstance(this).getUserHaveBeenRegistered()) {
            fetchData();
        }
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
                            email.setText(response.getString("email"));
                            address.setText(response.getString("address"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError response) {
                        Log.d("amina2", "fetch profile data: " + response.toString());
                    }
                }));
    }

    private void register(final String name, final String email, String address) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", PrefSingleton.getInstance(this).getAccessToken());
        params.put("user_data", "register");
        params.put("name", name);
        params.put("email", email);
        params.put("address", address);

        VolleySingleton.getInstance(this).addToRequestQueue(
                new CustomRequest(Request.Method.POST, Constants.userDataAPI, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("successful")) {
                                Toast.makeText(ProfileActivity.this, "اطلاعات با موفقیت ثبت شد.", Toast.LENGTH_SHORT).show();
                                if (!PrefSingleton.getInstance(ProfileActivity.this).getUserHaveBeenRegistered()) {
                                    PrefSingleton.getInstance(ProfileActivity.this).setUserHaveBeenRegistered(true);
                                }
                                Intent intent = new Intent();
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(ProfileActivity.this, "خطا در ثبت اطلاعات!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError response) {
                        Log.d("amina2", "register user data: " + response.toString());
                    }
                }));
    }

    private void logout() {
        Map<String, String> accessToken = new HashMap<>();
        accessToken.put("access_token", PrefSingleton.getInstance(this).getAccessToken());
        VolleySingleton.getInstance(this).addToRequestQueue(
                new CustomRequest(Request.Method.POST, Constants.logoutAPI, accessToken, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.getBoolean("error")) {
                                if (response.getBoolean("successful")) {
                                    Toast.makeText(ProfileActivity.this, "عملیات با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                                    PrefSingleton.getInstance(ProfileActivity.this).setAccessToken("0");
                                    PrefSingleton.getInstance(ProfileActivity.this).setPhoneNumber(null);
                                    PrefSingleton.getInstance(ProfileActivity.this).setUserHaveBeenRegistered(false);
                                    startActivity(new Intent(ProfileActivity.this, SplashActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "خطا در خروج از حساب کاربری"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("amina2", response.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError response) {
                        Log.d("amina2", "logout request: " + response.toString());
                    }
                }));
    }
}
