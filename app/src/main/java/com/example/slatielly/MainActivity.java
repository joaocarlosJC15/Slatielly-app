package com.example.slatielly;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slatielly.app.calendar.CalendarDateFinish.CalendarDateFinishFragment;
import com.example.slatielly.app.calendar.CalendarDateStart.CalendarDateStartFragment;
import com.example.slatielly.app.calendar.ConfirmRent.ConfirmRentFragment;
import com.example.slatielly.app.dress.DressFragment;
import com.example.slatielly.app.dress.EditPhotosDress;
import com.example.slatielly.app.dress.answers.AnswersFragment;
import com.example.slatielly.app.dress.comments.CommentsFragment;
import com.example.slatielly.app.dress.dresses.DressesFragment;
import com.example.slatielly.app.dress.edit.EditDressContract;
import com.example.slatielly.app.dress.edit.EditDressFragment;
import com.example.slatielly.app.dress.newAnswer.NewAnswerFragment;
import com.example.slatielly.app.dress.newComment.NewCommentFragment;
import com.example.slatielly.app.dress.registerDress.RegisterDressContract;
import com.example.slatielly.app.dress.registerDress.RegisterDressFragment;
import com.example.slatielly.app.profile.ProfileFragment;
import com.example.slatielly.app.rent.rentRequests.RentRequestsFragment;
import com.example.slatielly.model.Dress;
import com.example.slatielly.model.Rent;
import com.example.slatielly.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnSuccessListener<DocumentSnapshot>, View.OnClickListener, RegisterDressFragment.OnNavigationListener,
        DressesFragment.OnNavigationListener, DressFragment.OnNavigationListener, CommentsFragment.OnNavigationListener, NewCommentFragment.OnNavigationListener, CalendarDateStartFragment.OnNavigateListener, CalendarDateFinishFragment.OnNavigateListener,
        AnswersFragment.OnNavigationListener, NewAnswerFragment.OnNavigationListener, EditDressFragment.OnNavigationListener, EditPhotosDress.OnNavigationListener, ConfirmRentFragment.OnNavigateListener {


    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private TextView txtNavHeader;
    private int[] icons = {
            R.string.fa_female_solid, R.string.fa_calendar_alt_solid
    };
    private int[] adminIcons = {
            R.string.fa_plus_solid,
            R.string.fa_calendar_check_solid,
    };
    private MenuItem menuItemChecked;
    private ArrayList<MenuItem> menuItems;
    private User currentUser;
    private ArrayList<Integer> titles;
    private int lastTitle;
    private boolean isProfileActive;
    private ActionBarDrawerToggle toggle;
    private boolean toolBarNavigationListenerIsRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.titles = new ArrayList<>();
        this.menuItems = new ArrayList<>();

        this.initNavView();
        this.getUser();
        this.initDrawerLayout();

        this.fragmentManager = this.getSupportFragmentManager();

        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);

        this.setNavigationFragment(new DressesFragment(), R.string.all_dresses, true);
    }

    private void initDrawerLayout() {
        this.toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.drawerLayout = this.findViewById(R.id.drawerLayout);

        this.toggle = new ActionBarDrawerToggle(this, this.drawerLayout, this.toolbar,
                R.string.open_drawer, R.string.close_drawer);

        this.drawerLayout.addDrawerListener(this.toggle);
        this.toggle.syncState();
    }

    @Override
    public void enableViews(boolean enable) {
        if (enable) {
            this.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            this.toggle.setDrawerIndicatorEnabled(false);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (!this.toolBarNavigationListenerIsRegistered) {
                this.toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                this.toolBarNavigationListenerIsRegistered = true;
            }

        } else {
            this.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            this.toggle.setDrawerIndicatorEnabled(true);
            this.toggle.setToolbarNavigationClickListener(null);
            this.toolBarNavigationListenerIsRegistered = false;
        }
    }

    private void getUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(this);
    }

    private void initNavView() {
        this.navigationView = this.findViewById(R.id.navView);
        this.navigationView.setNavigationItemSelectedListener(this);

        View headerView = this.navigationView.getHeaderView(0);
        ImageView iconHeader = headerView.findViewById(R.id.ivNavHeader);
        FontDrawable drawable = new FontDrawable(this, R.string.fa_user_circle_solid, true, true);
        drawable.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        drawable.setTextSize(35);
        iconHeader.setImageDrawable(drawable);

        this.txtNavHeader = headerView.findViewById(R.id.txtNavHeader);
        headerView.setOnClickListener(this);

        this.renderMenuIcons(this.navigationView.getMenu(), this.icons);

        this.renderMenuIcons(navigationView.getMenu().getItem(2).getSubMenu(), this.adminIcons);
    }

    private void renderMenuIcons(Menu menu, int[] icons) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (!menuItem.hasSubMenu()) {
                FontDrawable drawable = new FontDrawable(this, icons[i], true, false);
                drawable.setTextColor(ContextCompat.getColor(this, R.color.colorGray600));
                drawable.setTextSize(25);
                menu.getItem(i).setIcon(drawable);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (this.menuItemChecked == menuItem) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }

        this.unCheckMenuItem(true);
        this.checkMenuItem(menuItem);

        switch (menuItem.getItemId()) {
            case R.id.nav_item_all_dresses: {
                this.setNavigationFragment(new DressesFragment(), R.string.all_dresses, false);
                break;
            }

            case R.id.nav_item_all_rents: {
                this.setNavigationFragment(new RentsFragment(), R.string.allRents, false);
                break;
            }

            case R.id.nav_item_rent_requests: {
                this.setNavigationFragment(RentRequestsFragment.newInstance(), R.string.rentsRequests, false);
                break;
            }

            case R.id.nav_item_register_dress: {
                this.setNavigationFragment(new RegisterDressFragment(), R.string.wRegisterDress, false);
                break;
            }

            default: {
                this.setNavigationFragment(new DressesFragment(), R.string.all_dresses, false);
                break;
            }
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkMenuItem(MenuItem menuItem) {
        menuItem.setChecked(true);
        FontDrawable icon = (FontDrawable) menuItem.getIcon();
        icon.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        this.menuItemChecked = menuItem;
    }

    private void unCheckMenuItem(boolean addToMenuItemsList) {
        if (this.menuItemChecked != null) {
            if (addToMenuItemsList) {
                this.menuItems.add(this.menuItemChecked);
            }

            this.menuItemChecked.setChecked(false);
            FontDrawable icon = (FontDrawable) this.menuItemChecked.getIcon();
            icon.setTextColor(ContextCompat.getColor(this, R.color.colorGray600));
            this.menuItemChecked = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (this.toolBarNavigationListenerIsRegistered) {
            this.enableViews(false);
        }

        if (!this.titles.isEmpty()) {
            int titlesSize = this.titles.size();
            int title = this.titles.get(titlesSize - 1);
            this.toolbar.setTitle(title);
            this.lastTitle = title;
            this.titles.remove(titlesSize - 1);
        }

        if (!this.menuItems.isEmpty() && this.lastTitle != R.string.profile) {
            int menuItemsSize = this.menuItems.size();
            if (!this.isProfileActive) {
                this.unCheckMenuItem(false);
            }

            MenuItem menuItem = this.menuItems.get(menuItemsSize - 1);
            this.checkMenuItem(menuItem);
            this.menuItems.remove(menuItemsSize - 1);
            this.isProfileActive = false;
        }

        if (this.lastTitle == R.string.profile) {
            this.unCheckMenuItem(false);
            this.isProfileActive = true;
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onBackPressed();
    }

    private void setNavigationFragment(Fragment fragment, int title, boolean isFirst) {
        FragmentTransaction fragmentTransaction = this.fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_content, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (!isFirst) {
            fragmentTransaction = fragmentTransaction.addToBackStack(null);
            this.titles.add(lastTitle);
        }

        fragmentTransaction.commit();

        this.toolbar.setTitle(title);
        this.lastTitle = title;
    }

    private void checkIfUserIsAdmin() {
        if (!this.currentUser.getRole().equals("admin")) {
            this.navigationView.getMenu().getItem(2).setVisible(false);
        }
    }

    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        this.currentUser = documentSnapshot.toObject(User.class);
        this.txtNavHeader.setText(this.currentUser.getName());
        this.checkIfUserIsAdmin();
    }

    @Override
    public void onClick(View v) {
        this.isProfileActive = true;
        this.unCheckMenuItem(true);
        this.setNavigationFragment(ProfileFragment.newInstance(), R.string.profile, false);
        this.drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof RegisterDressFragment) {
            RegisterDressFragment registerDressFragment = (RegisterDressFragment) fragment;
            registerDressFragment.setOnNavigationListener(this);
        }

        if (fragment instanceof DressesFragment) {
            DressesFragment dressesFragment = (DressesFragment) fragment;
            dressesFragment.setNavigationListener(this);
        }

        if (fragment instanceof DressFragment) {
            DressFragment dressFragment = (DressFragment) fragment;
            dressFragment.setOnNavigationListener(this);
        }
        if (fragment instanceof CommentsFragment) {
            CommentsFragment commentsFragment = (CommentsFragment) fragment;
            commentsFragment.setOnNavigationListener(this);
        }
        if (fragment instanceof NewCommentFragment) {
            NewCommentFragment newCommentFragment = (NewCommentFragment) fragment;
            newCommentFragment.setOnNavigationListener(this);
        }
        if (fragment instanceof AnswersFragment) {
            AnswersFragment answersFragment = (AnswersFragment) fragment;
            answersFragment.setOnNavigationListener(this);
        }
        if (fragment instanceof NewAnswerFragment) {
            NewAnswerFragment newAnswerFragment = (NewAnswerFragment) fragment;
            newAnswerFragment.setOnNavigationListener(this);
        }
        if (fragment instanceof CalendarDateStartFragment) {
            CalendarDateStartFragment calendarDateStartFragment = (CalendarDateStartFragment) fragment;
            calendarDateStartFragment.setOnNavigationListener(this);
        }

        if (fragment instanceof CalendarDateFinishFragment) {
            CalendarDateFinishFragment calendarDateFinishFragment = (CalendarDateFinishFragment) fragment;
            calendarDateFinishFragment.setOnNavigationListener(this);
        }
        if (fragment instanceof EditDressFragment) {
            EditDressFragment editDressFragment = (EditDressFragment) fragment;
            editDressFragment.setOnNavigationListener(this);
        }
        if (fragment instanceof EditPhotosDress) {
            EditPhotosDress editPhotosDress = (EditPhotosDress) fragment;
            editPhotosDress.setOnNavigationListener(this);
        }
        if (fragment instanceof ConfirmRentFragment) {
            ConfirmRentFragment confirmRentFragment = (ConfirmRentFragment) fragment;
            confirmRentFragment.setOnNavigationListener(this);
        }
    }

    @Override
    public void onNavigateToAllDresses() {
        this.unCheckMenuItem(false);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        enableViews(false);
        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        this.setNavigationFragment(new DressesFragment(), R.string.all_dresses, true);
    }

    @Override
    public void onNavigateToDress(String id) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(DressFragment.newInstance(id), R.string.Dress, false);
    }

    @Override
    public void onNavigateToComments(String dressId) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(CommentsFragment.newInstance(dressId), R.string.COMMENTS, false);
    }

    @Override
    public void onNavigateToNewComment(String dressId) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(NewCommentFragment.newInstance(dressId), R.string.New_comment, false);
    }

    @Override
    public void onNavigateToAnswers(String dressId, String commentId) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(AnswersFragment.newInstance(dressId, commentId), R.string.ANSWERS, false);
    }

    @Override
    public void onNavigateToNewAnswer(String dressId, String commentId) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(NewAnswerFragment.newInstance(dressId, commentId), R.string.NEW_ANSWER, false);
    }

    @Override
    public void onRentDress(String dressId) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(CalendarDateStartFragment.newInstance(dressId), R.string.wStartDate, false);
    }


    @Override
    public void onSelectFinishDateRent(String dressId, long startDate) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        System.out.println("BUG 5: " + dressId);
        System.out.println("Date Start 2: " + startDate);
        this.setNavigationFragment(CalendarDateFinishFragment.newInstance(dressId, startDate), R.string.wDateEnd, false);
    }

    public void onNavigateToEditPhotos(RegisterDressContract.Presenter presenterRegister, EditDressContract.Presenter presenterEdit) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(new EditPhotosDress(presenterRegister, presenterEdit), R.string.Edit_Photos, false);
    }

    @Override
    public void onNavigateToEditDress(String id) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(EditDressFragment.newInstance(id), R.string.Edit_dress, false);
    }

    @Override
    public void onNavigateToConfirmRent(Rent rent, Dress dress) {
        this.unCheckMenuItem(true);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        this.setNavigationFragment(new ConfirmRentFragment(rent, dress), R.string.Confirm_rent, false);
    }

    @Override
    public void onNavigateToAllDresses2() {
        this.unCheckMenuItem(false);
        MenuItem menuItem = this.navigationView.getMenu().getItem(0);
        this.checkMenuItem(menuItem);
        enableViews(false);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        this.setNavigationFragment(new DressesFragment(), R.string.all_dresses, true);
    }
}
