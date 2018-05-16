package a2.thesis.com.caketory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import a2.thesis.com.caketory.Fragment.SendSmsFragment;
import a2.thesis.com.caketory.Fragment.VerifyOtpFragment;

public class AuthActivity extends AppCompatActivity implements SendSmsFragment.Interface1, VerifyOtpFragment.Interface2 {

    private ViewPager mPager;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mPager = findViewById(R.id.viewPager);

        try {
            mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        } catch (Exception e) {
            Log.e("amina2", e.toString());
        }
    }

    public ViewPager getViewPager() {
        return null == mPager ? (ViewPager) findViewById(R.id.viewPager) : mPager;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new SendSmsFragment();
            } else if (position == 1) {
                fragment = new VerifyOtpFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
