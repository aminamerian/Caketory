package a2.thesis.com.caketory.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import a2.thesis.com.caketory.AuthActivity;
import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.R;


public class SendSmsFragment extends Fragment {

    ViewPager mPager;
    EditText input;
    private Interface1 anInterface;

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

        Typeface yekanFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/b_yekan.ttf");

        anInterface = (Interface1) getActivity();
        input = view.findViewById(R.id.input_phoneNumber);
        TextView textView = view.findViewById(R.id.text_submit);
        CardView submit = view.findViewById(R.id.cart_submit);
        textView.setTypeface(yekanFont);
        input.setTypeface(yekanFont);

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
                try {
                    responseObj = new JSONObject(response);
                    int error = responseObj.getInt("error");
                    if (error == 0) {
                        anInterface.setPhoneNumber(phoneNumber);
                        mPager.setCurrentItem(1);
                    } else {
                        final String message = responseObj.getString("message");
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
                    put("phone_number", phoneNumber);
                    Log.d("amina2", "phone_number value: " + phoneNumber);
                }};
            }
        };

        //To avoid volley sending data twice bug, there is need to set retry policy fo the request
        strReq.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(strReq);
    }

    private static boolean isValidMobileNumber(String mobile) {
        return mobile.matches("^(09|9)\\d{9}$");
    }

    public interface Interface1 {
        void setPhoneNumber(String phoneNumber);
    }

}
