package team7202.myfoodjournal;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import static team7202.myfoodjournal.PageFragment.ARG_MENU_OPTION;

public class FilterMenuDialogFragment extends DialogFragment implements View.OnClickListener {
    private SearchView mRestaurantSearch;
    private ImageButton mCloseMenu;
    private Button mAddFilter;
    private OnFilterInteractionListener mListener;
    private String menuOptionParam;
    private View view;
    private ArrayList<String> filterList;
    private SearchView filterSearch;

    public FilterMenuDialogFragment() {

    }

    public static FilterMenuDialogFragment newInstance(String menuOptionParam) {
        FilterMenuDialogFragment fragment = new FilterMenuDialogFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filters_popout, container);
        mRestaurantSearch = (SearchView) view.findViewById(R.id.restaurant_filter);
        mRestaurantSearch.setOnClickListener(this);
        mCloseMenu = (ImageButton) view.findViewById(R.id.closeFilterMenu);
        mCloseMenu.setOnClickListener(this);
        mAddFilter = (Button) view.findViewById(R.id.add_filter_button);
        mAddFilter.setOnClickListener(this);

        //generate list
        this.filterList = new ArrayList<String>();
        filterList.add("test filter 1");
        filterList.add("test filter 2");

        //instantiate custom filter list adapter
        FilterListAdapter adapter = new FilterListAdapter(filterList, getContext());

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

        Log.d("FILTER", "onClick Method Called");

        switch (v.getId()) {
            // Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.restaurant_filter):
                filterSearch.onActionViewExpanded();
                break;
            case (R.id.add_filter_button):
                //adding a filter to the list
                //get the filter to add, then add it
                String filterString = filterSearch.getQuery().toString();

                Log.d("FILTER", "filter text:" + filterString);

                //filterList.add()
                break;
            case (R.id.closeFilterMenu):
                if (mListener != null) {
                    mListener.onApplyFiltersClicked(filterList);
                }
                dismiss();
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
