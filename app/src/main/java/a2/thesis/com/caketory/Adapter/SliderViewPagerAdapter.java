package a2.thesis.com.caketory.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import a2.thesis.com.caketory.R;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Amin on 03/02/2018.
 */

public class SliderViewPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    int[] images = {R.drawable.apple_pie, R.drawable.cake1, R.drawable.cake2};

    public SliderViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_slider, null);
        ImageView image = view.findViewById(R.id.image_slider);
        image.setImageResource(images[position]);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
