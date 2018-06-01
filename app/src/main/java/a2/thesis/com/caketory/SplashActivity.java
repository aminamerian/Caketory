package a2.thesis.com.caketory;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private boolean connectedToNetwork = false;
    private boolean authenticated = false;
    private Typeface yekanFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //immediately change the launcher theme to the AppTheme
        //so the launcher theme fills the gap between starting the app and calling the onCreate method of launcher activity
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        yekanFont = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");

        checkNetConnRequest(false);

        progressBar = findViewById(R.id.progressBar);
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.playAnimation();
//        animationView.loop(true);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                checkNetConn();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * Authenticate and check network connection
     *
     * @param immediateCheck, if false, the checkNetConn method will be called after animation ended
     *                        if true, the checkNetConn functionality will be invoked when request's response received
     */
    private void checkNetConnRequest(final boolean immediateCheck) {
        Map<String, String> accessToken = new HashMap<>();
        accessToken.put("access_token", PrefSingleton.getInstance(this).getAccessToken());

        VolleySingleton.getInstance(this).addToRequestQueue(new CustomRequest(Request.Method.POST, Constants.checkConnectionAPI, accessToken, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    authenticated = response.getBoolean("authenticated");
                    connectedToNetwork = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (immediateCheck) {
                    //the connectedToNetwork is true, so there is no need to check like we did in checkNetConn method
                    progressBar.setVisibility(View.GONE);
                    if (authenticated) {
                        Log.d("amina2", "User Have Been Authenticated.");
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        Log.e("amina2", "User Have Not Been Authenticated!");
                        startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    }
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                connectedToNetwork = false;
                Log.d("amina2", response.toString());
                if (immediateCheck) {
                    progressBar.setVisibility(View.GONE);
                    setConnectionError();
                }
            }
        }));
    }

    private void checkNetConn() {
        Log.d("amina2", "authenticated: " + authenticated + "  connected: " + connectedToNetwork);
        if (connectedToNetwork) {
            if (authenticated) {
                Log.d("amina2", "User Have Been Authenticated.");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                Log.e("amina2", "User Have Not Been Authenticated!");
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }
            //calling finish method to not get back to splash screen later by pressing back button
            finish();
        } else {
            //connection error
            setConnectionError();
        }
    }

    private void setConnectionError() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.layout_root), "خطا در اتصال به شبکه!", Snackbar.LENGTH_INDEFINITE)
                .setAction("تلاش مجدد", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        checkNetConnRequest(true);
                    }
                });
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
}
