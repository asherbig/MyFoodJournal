package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddReviewFragment.OnAddReviewListener} interface
 * to handle interaction events.
 * Use the {@link AddReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddReviewFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_MENU_OPTION = "menu_option";

    //parameters
    private String menuOptionParam;

    private OnAddReviewListener mListener;
    private View view;

    private Place restaurantName;

    private static Map<String, String> reviewInfo;

    private EditText menuitem;
    private EditText rating;
    private EditText description;


    public AddReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static AddReviewFragment newInstance() {
        return new AddReviewFragment();
    }
    public static AddReviewFragment newInstance(Map<String, String> information) {
        reviewInfo = information;
        return new AddReviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menuOptionParam = getArguments().getString(ARG_MENU_OPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_review, container, false);
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        TextView restaurantNameText = (TextView) view.findViewById(R.id.restname);
        menuitem = (EditText) view.findViewById(R.id.menu_item);

        if (reviewInfo != null) {
            restaurantNameText.setText(reviewInfo.get("Restaurant Name"));
            menuitem.setText(reviewInfo.get("Menu Item"));
            menuitem.setFocusable(false);
        } else {
            DefaultActivity activity = (DefaultActivity) getActivity();
            restaurantName = activity.getRestaurantName();
            restaurantNameText.setText(restaurantName.getName());

            menuitem = (EditText) view.findViewById(R.id.menu_item);
        }
        rating = (EditText) view.findViewById(R.id.rating_entry);
        description = (EditText) view.findViewById(R.id.description_entry);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddReviewListener) {
            mListener = (OnAddReviewListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        final String INCOMPLETE_FIELDS = "One or more fields cannot be left blank and rating must be between 1 and 5";
        switch (v.getId()) {
            //Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.save_button):
                if (mListener != null) {
                    if (reviewInfo != null) {
                        try {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference().child("wishlist").child(user.getUid());

                            wishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String reviewId = reviewInfo.get("Review ID");
                                    if (dataSnapshot.hasChild(reviewId)) {
                                        Log.d("Checking for deletion", "child has value at key");
                                        wishlistRef.child(reviewId).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            mListener.onSaveReviewClicked(reviewInfo.get("Restaurant ID"),
                                    reviewInfo.get("Restaurant Name"),
                                    reviewInfo.get("Menu Item"),
                                    Integer.valueOf(rating.getText().toString()),
                                    description.getText().toString());
                        } catch (Exception e) {
                            Log.d("BIG EXCEPTION", e.getMessage());
                            Snackbar.make(view, INCOMPLETE_FIELDS, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        try {
                            mListener.onSaveReviewClicked(restaurantName.getId(), restaurantName.getName().toString(), menuitem.getText().toString(), Integer.valueOf(rating.getText().toString()), description.getText().toString());
                        } catch (Exception e) {
                            Snackbar.make(view, INCOMPLETE_FIELDS, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
                break;
            case (R.id.cancel_button):
                if (mListener != null) {
                    mListener.onAddReviewCancelClicked();
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
    public interface OnAddReviewListener {
        void onSaveReviewClicked(String id, String name, String menuitem, int rating, String description);
        void onAddReviewCancelClicked();
    }
}
