package team7202.myfoodjournal;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultActivity extends AppCompatActivity
        implements ProfileFragment.OnProfileInteractionListener,
        EditProfileFragment.OnEditProfileListener,
        EditPasswordFragment.OnEditPasswordListener,
        WishlistFragment.OnWishlistInteractionListener,
        FilterMenuDialogFragment.OnFilterInteractionListener,
        MyReviewsFragment.OnMyReviewsInteractionListener,
        AddReviewFragment.OnAddReviewListener,
        RestaurantFragment.OnRestaurantInteractionListener,
        DetailedReviewFragment.OnReviewInteractionListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    public Place restaurantName;

    public HashMap<String, ReviewData> allreviews;
    private ArrayList<String> myReviewFilters = new ArrayList<>();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        selectNavOption("fragment_myreviews");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Defines open and closed states for drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Incomplete, requires override of onPrepareOptionsMenu
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Listens for open and closed events.
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        // Creates the NavigationView object containing the list of menu options.
        mNavigationView = (NavigationView) findViewById(R.id.navigation);

        // Sets the Home page menu option as selected by default.
        mNavigationView.getMenu().getItem(0).setChecked(true);
        ab.setTitle(mNavigationView.getMenu().getItem(0).getTitle());

        // Sets the username in the navigation header
        View headerView = mNavigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navheader_username);
        navUsername.setText(UsernameSingleton.getInstance().getUsername());

        // Creates listener for events when clicking on navigation drawer options.
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        String layout = getLayoutName(menuItem.getItemId());
                        if (layout.equals("Log Out")) {
                            Intent i = new Intent(DefaultActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else if (layout.equals("Restaurants")) {
                            menuItem.setChecked(true);
                            loadPlaces(2);
                            mDrawerLayout.closeDrawers();
                        } else {
                            selectNavOption(layout);
                            // Updates selected item and title, then closes the drawer
                            menuItem.setChecked(true);
                            ab.setTitle(menuItem.getTitle());
                            mDrawerLayout.closeDrawers();
                        }
                        return true;
                    }
                }
        );
        allreviews = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
    }

    private String getLayoutName(int resourceId) {
        String layoutName = "";
        switch(resourceId) {
            case R.id.nav_myreviews:
                layoutName = "fragment_myreviews";
                break;
            case R.id.nav_restaurants:
                layoutName = "Restaurants";
                break;
            case R.id.nav_profile:
                layoutName = "fragment_profile";
                break;
            case R.id.nav_wishlist:
                layoutName = "fragment_wishlist";
                break;
            case R.id.nav_logout:
                layoutName = "Log Out";
                break;
        }
        return layoutName;
    }

    /** Swaps fragments in the default activity. */
    private void selectNavOption(String option) {
        // Create a new fragment and specify the screen to show based on the option selected
        if (option.equals("fragment_profile")) {
            Fragment fragment = ProfileFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option.equals("fragment_edit_profile")) {
            Fragment fragment = EditProfileFragment.newInstance(option, this);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option.equals("fragment_edit_password")) {
            Fragment fragment = EditPasswordFragment.newInstance(option, this);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option.equals("fragment_wishlist")) {
            Fragment fragment = WishlistFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option.equals("fragment_myreviews")) {
            Fragment fragment = MyReviewsFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option.equals("fragment_add_review")) {
            Fragment fragment = AddReviewFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option.equals("restaurant_summary_fragment")) {
            Fragment fragment = RestaurantFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {
            Fragment fragment = new PageFragment();
            Bundle args = new Bundle();
            args.putString(PageFragment.ARG_MENU_OPTION, option);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Pass the event to ActionBarDrawerToggle, if it returns true, then it has
           handled the event.
         */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //methods for the profile interface
    //opens the edit profile screen
    @Override
    public void onEditButtonClicked() {
        Log.d("PROFILE", "Edit profile clicked");

        selectNavOption("fragment_edit_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit Profile");
    }

    //opens the edit password screen
    @Override
    public void onChangePassClicked() {
        Log.d("PROFILE", "Change password clicked");
        selectNavOption("fragment_edit_password");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit Password");
    }

    //methods for the edit profile interface
    //returns to the profile screen
    @Override
    public void onProfileSaveClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Save profile button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    //returns to the profile summary screen
    @Override
    public void onProfileCancelClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Cancel profile edit button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    //methods for the edit password fragment interface
    //returns to the profile screen
    @Override
    public void onPassSaveClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Save password button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    //returns to the profile summary screen
    @Override
    public void onPassCancelClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Cancel password edit button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    @Override
    public void onFilterButtonClicked() {
        //make sure that this correctly launches filter for myReviews, otherReviews and wishlist
        Log.d("WISHLIST", "Filters button clicked on Wishlist page");
        FilterMenuDialogFragment filterMenu = FilterMenuDialogFragment.newInstance(myReviewFilters);
        FragmentManager fm = getFragmentManager();
        filterMenu.show(fm, "Filter Menu generated");
    }

    @Override
    public void onApplyFiltersClicked(ArrayList<String> filtersList) {
        //make the filters apply
        myReviewFilters = filtersList;
        Log.d("FILTERS", "Filters received from filters menu: " + filtersList.toString());
    }

    @Override
    public void onSortByButtonClicked() {
        Log.d("SORTBY", "Sort By button clicked");
        final View anchor = findViewById(R.id.sortby_button);
        PopupMenu popup = new PopupMenu(this, anchor);
        getMenuInflater().inflate(R.menu.sortby_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // TODO: Add code for filtering list of entries
                switch (menuItem.getItemId()) {
                    case R.id.sortby_mostrecent:
                        break;
                    case R.id.sortby_rating:
                        break;
                    case R.id.sortby_restaurant:
                        break;
                    case R.id.sortby_food:
                        break;
                }
                Button sortByButton = (Button) anchor;
                sortByButton.setText("Sort By: \n" + menuItem.getTitle());
                return true;
            }
        });

        popup.show();
    }


    @Override
    public void onFloatingButtonClicked() {
        loadPlaces(1);
    }

    private void loadPlaces(int requestCode) {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            final View view = findViewById(R.id.fab);
            Snackbar.make(view, "Update your Google Play Services!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        } catch (GooglePlayServicesNotAvailableException e) {
            final View view = findViewById(R.id.fab);
            Snackbar.make(view, "Google Play Services are currently unavailable.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActionBar ab = getSupportActionBar();
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);

            boolean isTaggedRestaurant = false;
            List<Integer> placeTypes = place.getPlaceTypes();
            for (int i = 0; i < placeTypes.size(); i++) {
                if (placeTypes.get(i) == Place.TYPE_RESTAURANT) {
                    isTaggedRestaurant = true;
                    break;
                }
            }

            if (isTaggedRestaurant) {
                restaurantName = place;
                switch (requestCode) {
                    case (1):
                        selectNavOption("fragment_add_review");
                        ab.setTitle("Add Review");
                        break;
                    case (2):
                        selectNavOption("restaurant_summary_fragment");
                        ab.setTitle("Restaurant Reviews");
                        break;
                }
            } else {
                final View view = findViewById(R.id.fab);
                Snackbar.make(view, "This is not a restaurant!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    public Place getRestaurantName() {
        return restaurantName;
    }

    public HashMap<String, ReviewData> getAllReviews() {
        return allreviews;
    }

    @Override
    public void onSaveReviewClicked(String restaurant_id, String restaurant_name, String menuitem, int rating, String description) {
        Log.d("SAVE REVIEW", "Saved review written by user.");
        View headerView = mNavigationView.getHeaderView(0);
        String username = ((TextView) headerView.findViewById(R.id.navheader_username)).getText().toString();
        final View view = findViewById(R.id.fab);
        if (rating < 1 || rating > 5) {
            Snackbar.make(view, "Rating must be between 1 and 5", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        String currentTime = "" + (System.currentTimeMillis() / 1000);
        allreviews.put(restaurant_id + ":" + menuitem, new ReviewData(restaurant_name, menuitem, rating, description, currentTime));

        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("my_reviews").child(user.getUid());

        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(restaurant_id);

        String key = myRef.push().getKey();
        myRef.child(key).child("username").setValue(username);
        myRef.child(key).child("restaurant_name").setValue(restaurant_name);
        myRef.child(key).child("menuitem").setValue(menuitem);
        myRef.child(key).child("rating").setValue(rating);
        myRef.child(key).child("description").setValue(description);
        myRef.child(key).child("date_submitted").setValue(currentTime);

        key = restaurantRef.push().getKey();
        restaurantRef.child(key).child("username").setValue(username);
        restaurantRef.child(key).child("restaurant_name").setValue(restaurant_name);
        restaurantRef.child(key).child("menuitem").setValue(menuitem);
        restaurantRef.child(key).child("rating").setValue(rating);
        restaurantRef.child(key).child("description").setValue(description);
        restaurantRef.child(key).child("date_submitted").setValue(currentTime);

        //TODO: PUSH THE INFORMATION (username, id, menuitem, rating, description) to database
        selectNavOption("fragment_myreviews");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("My Reviews");
    }

    @Override
    public void onAddReviewCancelClicked() {
        Log.d("CANCEL REVIEW", "Canceled review written by user.");
        selectNavOption("fragment_myreviews");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("My Reviews");
    }

    @Override
    public void onSearchBarClicked() {
        Log.d("SEARCH BAR CLICKED", "Search bar clicked by user.");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Search Bar successfully clicked.");
    }

    @Override
    public void onCancelButtonClicked() {
        Log.d("CANCEL BUTTON CLICKED", "Cancel button clicked by user.");
        ActionBar ab = getSupportActionBar();
        selectNavOption("restaurant_summary_fragment");
        ab.setTitle("Restaurant Reviews");
    }

    @Override
    public void onAddWishlistButtonClicked() {
        Log.d("WISHLIST BUTTON CLICKED", "Wishlist button clicked by user.");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Wishlist Button clicked");
    }
}