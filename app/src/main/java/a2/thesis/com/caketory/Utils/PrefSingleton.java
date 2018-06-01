package a2.thesis.com.caketory.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Amin on 16/05/2018.
 */

public class PrefSingleton {
    private static PrefSingleton ourInstance;
    private static SharedPreferences sharedPreferences;
    private static Editor editor;


    private PrefSingleton(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static PrefSingleton getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new PrefSingleton(context.getApplicationContext());
        }
        return ourInstance;
    }

    public Boolean getUserHaveBeenRegistered() {
        return sharedPreferences.getBoolean("userHaveRegistered", false);
    }

    public void setUserHaveBeenRegistered(Boolean value) {
        editor.putBoolean("userHaveRegistered", value);
        editor.commit();
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", "0");
    }

    public void setAccessToken(String value) {
        editor.putString("accessToken", value);
        editor.commit();
        editor.apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString("phoneNumber", null);
    }

    public void setPhoneNumber(String value) {
        editor.putString("phoneNumber", value);
        editor.commit();
        editor.apply();
    }
}
