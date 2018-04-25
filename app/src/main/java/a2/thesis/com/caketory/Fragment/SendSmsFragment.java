package a2.thesis.com.caketory.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import a2.thesis.com.caketory.AuthActivity;
import a2.thesis.com.caketory.Constants;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.R;


public class SendSmsFragment extends Fragment {

    ViewPager mPager;
    EditText input;
    boolean requestSent = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_sms, container, false);

        if (getActivity() != null) {
            mPager = ((AuthActivity) getActivity()).getViewPager();
        }

        input = view.findViewById(R.id.input_phoneNumber);
        Button submit = view.findViewById(R.id.button_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error = sendSMS(input.getText().toString().trim());
                if (error != null) {
                    input.setError(error);
                }
            }
        });
        return view;
    }


    private String sendSMS(String phoneNumber) {
        if (!phoneNumber.isEmpty()) {
            if (isValidMobileNumber(phoneNumber)) {
                phoneNumber = 0 == phoneNumber.indexOf('9') ? "0" + phoneNumber : phoneNumber;
                requestForSMS(phoneNumber);
            } else {
                return "شماره وارد شده معتبر نمی باشد.";
            }
        } else {
            return "شماره ای وارد نکرده اید.";
        }
        return null;
    }

    private void requestForSMS(final String phoneNumber) {

        StringRequest strReq = new StringRequest(Request.Method.POST, Constants.SEND_SMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject responseObj;
                Log.d("amina2", "response: " + response);

                try {
                    responseObj = new JSONObject(response);

                    // Parsing json object response
                    int error = responseObj.getInt("error");
                    final String message = responseObj.getString("message");

                    // Checking for error, if not error SMS is initiated
                    // Device should receive it shortly
                    if (error == 0) {
                        mPager.setCurrentItem(1);

                    } else {
                        input.setError(message);
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
                    put("phoneNumber", phoneNumber);
                }};
            }
        };
        if (!requestSent) {
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(strReq);
            requestSent = true;
        }
    }

    private static boolean isValidMobileNumber(String mobile) {
        return mobile.matches("^(09|9)\\d{9}$");
    }

}
