package team7202.myfoodjournal;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class FilterMenuDialogFragment extends DialogFragment implements View.OnClickListener {
    private SearchView mRestaurantSearch;
    private ImageButton mCloseMenu;
    //private Button mAddFilter;
    private OnFilterInteractionListener mListener;
    private View view;
    private ArrayList<String> filterList;
    private SearchView filterSearch;
    private FilterListAdapter adapter;
    private TextView searchText;

    public FilterMenuDialogFragment() {

    }

    public static FilterMenuDialogFragment newInstance(ArrayList<String> filtersParam) {
        FilterMenuDialogFragment fragment = new FilterMenuDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_FILTERS, filtersParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterList = getArguments().getStringArrayList(ARG_FILTERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filters_popout, container);
        mRestaurantSearch = (SearchView) view.findViewById(R.id.restaurant_filter);
        mRestaurantSearch.setOnClickListener(this);
        mCloseMenu = (ImageButton) view.findViewById(R.id.closeFilterMenu);
        mCloseMenu.setOnClickListener(this);
        //mAddFilter = (Button) view.findViewById(R.id.add_filter_button);
        //mAddFilter.setOnClickListener(this);
        searchText = (TextView) view.findViewById(R.id.textView3);

        //generate list if one wasn't passed in
        if (this.filterList == null) {
            this.filterList = new ArrayList<String>();
            filterList.add("test filter 1");
            filterList.add("test filter 2");
        }

        //instantiate custom filter list adapter
        FilterListAdapter adapter = new FilterListAdapter(filterList, getContext());
        this.adapter = adapter;

        //handle list-view and assign adapter
        ListView lView = (ListView) view.findViewById(R.id.filter_list);
        lView.setAdapter(adapter);

        this.filterSearch = (SearchView) view.findViewById(R.id.restaurant_filter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FilterMenuDialogFragment.OnFilterInteractionListener) {
            mListener = (FilterMenuDialogFragment.OnFilterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterInteractionListener");
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
            case (R.id.restaurant_filter):
                int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                        .build();
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    System.out.println("2");
                } catch (GooglePlayServicesRepairableException e) {
                    final View view = v.findViewById(R.id.fab);
                    Snackbar.make(view, "Update your Google Play Services!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } catch (GooglePlayServicesNotAvailableException e) {
                    final View view = v.findViewById(R.id.fab);
                    Snackbar.make(view, "Google Play Services are currently unavailable.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
                break;

//            case (R.id.add_filter_button):
//                //adding a filter to the list
//                //get the filter to add, then add it
//                String filterString = filterSearch.getQuery().toString();
//                addFilter(filterString);
//                break;

            case (R.id.closeFilterMenu):
                if (mListener != null) {
                    mListener.onApplyFiltersClicked(filterList);
                }
                dismiss();
        }
    }

    //this adds a string to the filters list
    private void addFilter(String toBeAdded) {
        filterList.add(0, toBeAdded);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                boolean isRestaurant = false;
                for (int i : place.getPlaceTypes()) {
                    if (i == Place.TYPE_RESTAURANT) {
                        isRestaurant = true;
                        break;
                    }
                }
                if (isRestaurant) {
                    //Add "place.getName().toString()" to the search/text view
                    addFilter(place.getName().toString());

                } else {
                    Snackbar.make(view, "This is not a restaurant!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                System.out.println(status);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
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
    public interface OnFilterInteractionListener {
        // TODO: Update argument type and name
        void onApplyFiltersClicked(ArrayList<String> filters);
    }
}
