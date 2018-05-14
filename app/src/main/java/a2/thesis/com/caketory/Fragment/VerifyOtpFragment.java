package a2.thesis.com.caketory.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import a2.thesis.com.caketory.Constants;
import a2.thesis.com.caketory.MainActivity;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.R;


public class VerifyOtpFragment extends Fragment {

    EditText input;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_otp, container, false);

        Typeface yekanFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/b_yekan.ttf");

        input = view.findViewById(R.id.input_code);
        TextView textView = view.findViewById(R.id.text_submit);
        CardView submit = view.findViewById(R.id.cart_submit);
        textView.setTypeface(yekanFont);
        input.setTypeface(yekanFont);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = input.getText().toString();
                sendCode(code);
            }
        });
        return view;
    }

    private void sendCode(final String code) {

        StringRequest strReq = new StringRequest(Request.Method.POST, Constants.VERIFY_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject responseObj;
                Log.d("amina2", "verify code response: " + response);
                try {
                    responseObj = new JSONObject(response);
                    // Parsing json object response
                    int error = responseObj.getInt("error");
                    // Checking for error, if not error SMS is initiated
                    // Device should receive it shortly
                    if (error == 0) {
                        //TODO do something with access token
                        String accessToken = responseObj.getString("access_token");
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    } else {
                        input.setError(responseObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("amina2", "error: " + error.toString());
                input.setError("اشکال در برقرای ارتباط با سرور");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("otp", code);
                }};
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(strReq);
    }

}
