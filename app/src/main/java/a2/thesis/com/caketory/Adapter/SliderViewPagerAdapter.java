package a2.thesis.com.caketory.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import a2.thesis.com.caketory.Utils.Constants;
import a2.thesis.com.caketory.Network.VolleySingleton;
import a2.thesis.com.caketory.R;

/**
 * Created by Amin on 03/02/2018.
 */

public class SliderViewPagerAdapter extends PagerAdapter {

    private ImageLoader imageLoader;
    Context context;
    LayoutInflater inflater;
    ArrayList<String> images;

    public SliderViewPagerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
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
        final ImageView imageView = view.findViewById(R.id.image_slider);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar_header);

        String imagePath = Constants.imagesDirectory + images.get(position);
        imageLoader.get(imagePath, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Glide.with(context)
                        .load(response.getRequestUrl())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imageView);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("amina2", error.toString());
            }
        });

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
