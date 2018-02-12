package team7202.myfoodjournal;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import static team7202.myfoodjournal.PageFragment.ARG_MENU_OPTION;

/**
 * Created by Zach on 2/12/2018.
 */

public class FilterMenuDialogFragment extends DialogFragment implements View.OnClickListener {
    private SearchView mRestaurantSearch;
    private ImageButton mCloseMenu;
    private OnFilterInteractionListener mListener;
    private String menuOptionParam;
    private View view;

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
                if (mListener != null) {
                    mListener.onRestaurantFieldClicked();
                }
                break;
            case (R.id.closeFilterMenu):
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
        void onRestaurantFieldClicked();
    }
}
