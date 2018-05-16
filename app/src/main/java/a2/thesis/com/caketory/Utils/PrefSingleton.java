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

    public Boolean getUserHaveBeenAuthenticated() {
        return sharedPreferences.getBoolean("userHaveAuthenticated", false);
    }

    public void setUserHaveBeenAuthenticated(Boolean value) {
        editor.putBoolean("userHaveAuthenticated", value);
        editor.commit();
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", null);
    }

    public void setAccessToken(String value) {
        editor.putString("accessToken", value);
        editor.commit();
        editor.apply();
    }
}
