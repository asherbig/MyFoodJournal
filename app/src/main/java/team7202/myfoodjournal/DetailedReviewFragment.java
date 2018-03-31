package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Zach on 3/31/2018.
 */

public class DetailedReviewFragment extends Fragment implements View.OnClickListener {

    private static CharSequence[] restaurantInfo;
    private View view;
    private DetailedReviewFragment.OnReviewInteractionListener mListener;
    public static DetailedReviewFragment newInstance(CharSequence[] information) {
        DetailedReviewFragment fragment = new DetailedReviewFragment();
        restaurantInfo = information;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detailed_review, container, false);
        TextView name = (TextView) view.findViewById(R.id.restuarant_name);
        name.setText(restaurantInfo[0]);
        TextView menuItem = (TextView) view.findViewById(R.id.menu_item_name);
        menuItem.setText(restaurantInfo[1]);
        TextView rating = (TextView) view.findViewById(R.id.rating_value);
        rating.setText(restaurantInfo[2]);
        TextView description = (TextView) view.findViewById(R.id.description_value);
        description.setText(restaurantInfo[3]);

        Button addToWishlistButton = (Button) view.findViewById(R.id.add_wishlist_button);
        addToWishlistButton.setOnClickListener(this);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailedReviewFragment.OnReviewInteractionListener) {
            mListener = (DetailedReviewFragment.OnReviewInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnReviewInteractionListener");
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
            case (R.id.add_wishlist_button):
                if (mListener != null) {
                    mListener.onAddWishlistButtonClicked();
                }
                break;
            case (R.id.cancel_button):
                if (mListener != null) {
                    mListener.onCancelButtonClicked();
                }
                break;
        }
    }

    public interface OnReviewInteractionListener {
        // TODO: Update argument type and name
        void onAddWishlistButtonClicked();
        void onCancelButtonClicked();
    }
}
