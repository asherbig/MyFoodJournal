package team7202.myfoodjournal;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static team7202.myfoodjournal.PageFragment.ARG_FILTERS;

public class SearchMenuDialogFragment extends DialogFragment implements View.OnClickListener {
    private SearchView mRestaurantSearch;
    private ImageButton mCloseMenu;
    private Button mApplyFilter;
    private OnSearchInteractionListener mListener;
    private View view;
    private TextView searchText;

    public SearchMenuDialogFragment() {

    }

    public static SearchMenuDialogFragment newInstance() {
        SearchMenuDialogFragment fragment = new SearchMenuDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_popout, container);
        mRestaurantSearch = (SearchView) view.findViewById(R.id.user_search_bar);
        mRestaurantSearch.setOnClickListener(this);
        mCloseMenu = (ImageButton) view.findViewById(R.id.closeSearchMenu);
        mCloseMenu.setOnClickListener(this);
        mApplyFilter = (Button) view.findViewById(R.id.search_users_button);
        mApplyFilter.setOnClickListener(this);
        searchText = (TextView) view.findViewById(R.id.searchUserContent);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchMenuDialogFragment.OnSearchInteractionListener) {
            mListener = (SearchMenuDialogFragment.OnSearchInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchInteractionListener");
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
            case (R.id.search_users_button):
                if (mListener != null) {
                    String text = mRestaurantSearch.getQuery().toString();
                    mListener.onSearchButtonClicked(text);
                }
                dismiss();
                break;

            case (R.id.closeSearchMenu):
                dismiss();
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
    public interface OnSearchInteractionListener {
        // TODO: Update argument type and name
        void onSearchButtonClicked(String text);
    }
}
