package com.example.slatielly.app.dress;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.slatielly.R;
import com.example.slatielly.model.Dress;
import com.example.slatielly.model.Image;
import com.example.slatielly.model.User;
import com.example.slatielly.model.repository.FirestoreRepository;

import java.text.NumberFormat;
import java.util.ArrayList;

public class DressFragment extends Fragment implements DressContract.View, View.OnClickListener {

    private ViewPager mPager;
    private TextView txtDescription;
    private TextView txtSize;
    private TextView txtColor;
    private TextView txtPrice;
    private TextView txtType;
    private TextView txtMaterial;
    private Button btnComments;
    private Button btnRent;
    private DressContract.Presenter presenter;
    private OnNavigationListener listener;
    private MenuItem menuItem;
    private Dress dress;
    private FragmentManager fragmentManager;

    public DressFragment() {
        // Required empty public constructor
    }

    public static DressFragment newInstance(String id) {
        DressFragment dressFragment = new DressFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        dressFragment.setArguments(bundle);
        return dressFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = this.getChildFragmentManager();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_dress, menu);
        menuItem = menu.findItem(R.id.edit_dress_image);
        menuItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirestoreRepository<Dress> repository = new FirestoreRepository<>(Dress.class, Dress.DOCUMENT_NAME);
        this.presenter = new DressPresenter(this, repository);

        if (this.listener != null) {
            this.listener.enableViews(true);
        }

        this.setupViews(view);
        mPager = view.findViewById(R.id.pager_dress_fragment_screen_slide);

        if (this.getArguments() != null) {
            String id = this.getArguments().getString("id");
            this.presenter.loadDress(id);
        }
    }

    private void setupViews(View view) {
        txtDescription = view.findViewById(R.id.txtDescription);
        txtSize = view.findViewById(R.id.txtSize);
        txtColor = view.findViewById(R.id.txtColor);
        txtPrice = view.findViewById(R.id.txtPrice);
        txtType = view.findViewById(R.id.txtType);
        txtMaterial = view.findViewById(R.id.txtMaterial);

        btnComments = view.findViewById(R.id.btnComments);
        btnRent = view.findViewById(R.id.btnRent);
        btnComments.setOnClickListener(this);
        btnRent.setOnClickListener(this);
    }

    @Override
    public void setDressViews(Dress dress, User currentUser) {
        this.dress = dress;

        if (currentUser.getRole().equals("admin")) {
            menuItem.setVisible(true);
        }

        txtDescription.setText(dress.getDescription());
        txtSize.setText(dress.getSize());
        txtColor.setText(dress.getColor());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        this.txtPrice.setText(format.format(dress.getPrice()));
        txtType.setText(dress.getType());
        txtMaterial.setText(dress.getMaterial());
    }

    @Override
    public void setScreenSlideAdapter(ArrayList<Image> images) {
        PagerAdapter pagerAdapter = new ScreenSlideAdapter(fragmentManager, images);
        mPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == btnComments) {
            if (this.getArguments() != null) {
                String id = this.getArguments().getString("id");
                this.listener.onNavigateToComments(id);
                return;
            }

            return;
        }

        if (v == btnRent) {
            if (this.getArguments() != null) {
                String id = this.getArguments().getString("id");
                this.listener.onRentDress(id);
                return;
            }

            return;
        }
    }

    public void setOnNavigationListener(OnNavigationListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_dress_image) {
            this.listener.onNavigateToEditDress(dress.getId());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnNavigationListener {
        void onNavigateToComments(String dressId);

        void enableViews(boolean enable);

        void onRentDress(String dressId);


        void onNavigateToEditDress(String dressId);
    }
}
