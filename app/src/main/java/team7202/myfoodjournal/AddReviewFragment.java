package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;


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
     * @param menuOptionParam the menu option being initialized.
     * @return A new instance of fragment ProfileFragment.
     */
    public static AddReviewFragment newInstance(String menuOptionParam) {
        AddReviewFragment fragment = new AddReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MENU_OPTION, menuOptionParam);
        fragment.setArguments(args);
        return fragment;
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
        DefaultActivity activity = (DefaultActivity) getActivity();
        restaurantName = activity.getRestaurantName();
        TextView restaurantNameText = (TextView) view.findViewById(R.id.restname);
        restaurantNameText.setText(restaurantName.getName());

        menuitem = (EditText) view.findViewById(R.id.menu_item);
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
        switch (v.getId()) {
            //Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.save_button):
                if (mListener != null) {
                    try {
                        mListener.onSaveReviewClicked(restaurantName.getId(), restaurantName.getName().toString(), menuitem.getText().toString(), Integer.valueOf(rating.getText().toString()), description.getText().toString());
                    } catch (Exception e) {
                        Snackbar.make(view, "One or more fields cannot be left blank and rating must be between 1 and 5", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
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
