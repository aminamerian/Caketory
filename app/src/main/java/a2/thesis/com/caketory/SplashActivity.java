package a2.thesis.com.caketory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //immediately change the launcher theme to the AppTheme
        //so the launcher theme fills the gap between starting the app and calling the onCreate method of launcher activity
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
