package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by Zach on 4/1/2018.
 */

public class DetailedMyReviewFragment extends Fragment implements View.OnClickListener {
    private static Map<String, String> reviewInfo;
    private static boolean source;
    private View view;
    private Place restaurantName;
    private DetailedMyReviewFragment.OnMyDetailedReviewInteractionListener mListener;
    public static DetailedMyReviewFragment newInstance(Map<String, String> information, boolean inMyReviews) {
        DetailedMyReviewFragment fragment = new DetailedMyReviewFragment();
        source = inMyReviews;
        reviewInfo = information;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detailed_my_review, container, false);
        TextView name = (TextView) view.findViewById(R.id.restuarant_name);
        name.setText(reviewInfo.get("Restaurant Name"));
        TextView menuItem = (TextView) view.findViewById(R.id.menu_item_name);
        menuItem.setText(reviewInfo.get("Menu Item"));
        TextView rating = (TextView) view.findViewById(R.id.rating_value);
        rating.setText(reviewInfo.get("Rating"));
        TextView description = (TextView) view.findViewById(R.id.description_value);
        description.setText(reviewInfo.get("Description"));

        final DefaultActivity activity = (DefaultActivity) getActivity();
        restaurantName = activity.getRestaurantName();

        Button editReviewButton = (Button) view.findViewById(R.id.edit_review_button);
        editReviewButton.setOnClickListener(this);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailedMyReviewFragment.OnMyDetailedReviewInteractionListener) {
            mListener = (DetailedMyReviewFragment.OnMyDetailedReviewInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnResReviewInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.edit_review_button):
                if (mListener != null) {
                    DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(restaurantName.getId());
                }
                break;
            case (R.id.cancel_button):
                if (mListener != null) {
                    mListener.onCancelButtonClicked(source);
                }
                break;
        }
    }

    public interface OnMyDetailedReviewInteractionListener {
        // TODO: Update argument type and name
        void onEditReviewButtonClicked();
        void onCancelButtonClicked(boolean source);
    }
}
