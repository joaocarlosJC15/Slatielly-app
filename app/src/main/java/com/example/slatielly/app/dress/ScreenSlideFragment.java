package com.example.slatielly.app.dress;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.slatielly.R;
import com.example.slatielly.model.Image;

public class ScreenSlideFragment extends Fragment {
    public static ScreenSlideFragment newInstance(Image image) {
        ScreenSlideFragment fragment = new ScreenSlideFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("image", image);

        fragment.setArguments(bundle);

        return fragment;
    }

    public ScreenSlideFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_dress, container, false);
        ImageView image_dress_fragment_screen_slide_page_dress = rootView.findViewById(R.id.image_dress_fragment_screen_slide_page_dress);

        if (this.getArguments() != null) {
            Image image = this.getArguments().getParcelable("image");

            if (image != null) {
                Glide.with(this).load(image.getdownloadLink()).into(image_dress_fragment_screen_slide_page_dress);
            }
        }

        return rootView;
    }
}