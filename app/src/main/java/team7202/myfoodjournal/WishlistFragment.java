package team7202.myfoodjournal;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zach on 2/11/2018.
 */

public class WishlistFragment extends Fragment implements View.OnClickListener {

    //parameters
    private OnWishlistInteractionListener mListener;
    private View view;
    private List<Map<String, String>> data;
    private WishlistAdapter wishlistAdapter;

    public WishlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static WishlistFragment newInstance() {
        return new WishlistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        ListView listview = (ListView) view.findViewById(R.id.listviewID);
        data = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference().child("wishlist").child(user.getUid());

        wishlistAdapter = new WishlistAdapter(getContext(), data);
        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot entry : dataSnapshot.getChildren()) {
                    Map reviewInfo = (Map) entry.getValue();
                    Map<String, String> datum = new HashMap<>(4);
                    datum.put("Review ID", (String) reviewInfo.get("reviewId"));
                    datum.put("Restaurant Name", (String) reviewInfo.get("restaurant_name"));
                    datum.put("Address", (String) reviewInfo.get("address"));
                    datum.put("Restaurant ID", (String) reviewInfo.get("restaurant_id"));
                    datum.put("Menu Item", (String) reviewInfo.get("menuitem"));
                    datum.put("Date Submitted", (String) reviewInfo.get("date_submitted"));
                    data.add(datum);
                }
                wishlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listview.setAdapter(wishlistAdapter);
        Button filtersButton = (Button) view.findViewById(R.id.filters_button);
        filtersButton.setOnClickListener(this);
        Button sortByButton = (Button) view.findViewById(R.id.sortby_button);
        sortByButton.setOnClickListener(this);
        sortByButton.setText("Sort By: \nMost Recent");
        Collections.sort(data, time_comparator);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWishlistInteractionListener) {
            mListener = (OnWishlistInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWishlistInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //handles button clicks in the fragment
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.filters_button):
                if (mListener != null) {
                    mListener.onFilterButtonClicked();
                }
                break;
            case (R.id.sortby_button):
                if (mListener != null) {
                    onSortByButtonClicked();
                }
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnWishlistInteractionListener {
        // TODO: Update argument type and name
        void onFilterButtonClicked();
        void onSortByButtonClicked();
    }

    public class WishlistAdapter extends ArrayAdapter<Map<String, String>> {
        public WishlistAdapter(Context context, List wishlistData) {
            super(context, 0, wishlistData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Map<String, String> entry = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.wishlist_row, parent, false);
            }
            // Lookup view for data population
            TextView resName = (TextView) convertView.findViewById(R.id.text1);
            TextView address = (TextView) convertView.findViewById(R.id.text2);
            TextView menuItem = (TextView) convertView.findViewById(R.id.text3);

            // Populate the data into the template view using the data object
            resName.setText(entry.get("Restaurant Name"));
            address.setText(entry.get("Address"));
            menuItem.setText(entry.get("Menu Item"));

            Button add_review_button = (Button) convertView.findViewById(R.id.add_review_button);
            add_review_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = AddReviewFragment.newInstance(entry);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    Log.d("TEST", "Successfully registered onClick in ListView entry.");
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }

    public void onSortByButtonClicked() {
        Log.d("WISHLIST", "Sort By button clicked on Wishlist page");
        final View anchor = view.findViewById(R.id.sortby_button);
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        getActivity().getMenuInflater().inflate(R.menu.sortby_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.sortby_mostrecent:
                        Collections.sort(data, time_comparator);
                        break;
                    case R.id.sortby_rating:
                        Collections.sort(data, rating_comparator);
                        break;
                    case R.id.sortby_restaurant:
                        Collections.sort(data, restaurant_comparator);
                        break;
                    case R.id.sortby_food:
                        Collections.sort(data, food_comparator);
                        break;
                }
                wishlistAdapter.notifyDataSetChanged();
                Button sortByButton = (Button) anchor;
                sortByButton.setText("Sort By: \n" + menuItem.getTitle());
                return true;
            }
        });

        popup.show();
    }

    private static Comparator<Map<String, String>> rating_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return b.get("Rating").compareTo(a.get("Rating"));
        }
    };

    private static Comparator<Map<String, String>> restaurant_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return a.get("Restaurant Name").compareTo(b.get("Restaurant Name"));
        }
    };

    private static Comparator<Map<String, String>> food_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return a.get("Menu Item").compareTo(b.get("Menu Item"));
        }
    };

    private static Comparator<Map<String, String>> time_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return b.get("Date Submitted").compareTo(a.get("Date Submitted"));
        }
    };
}
