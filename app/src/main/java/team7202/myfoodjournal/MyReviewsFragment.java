package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zguthrie3 on 2/13/2018.
 */

public class MyReviewsFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_MENU_OPTION = "menu_option";

    //parameters
    private String menuOptionParam;

    private MyReviewsFragment.OnMyReviewsInteractionListener mListener;
    private View view;


    public MyReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param menuOptionParam the menu option being initialized.
     * @return A new instance of fragment ProfileFragment.
     */
    public static MyReviewsFragment newInstance(String menuOptionParam) {
        MyReviewsFragment fragment = new MyReviewsFragment();
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
        view = inflater.inflate(R.layout.fragment_myreviews, container, false);
        Button filtersButton = (Button) view.findViewById(R.id.filters_button);
        filtersButton.setOnClickListener(this);
        Button sortByButton = (Button) view.findViewById(R.id.sortby_button);
        sortByButton.setOnClickListener(this);
        sortByButton.setText("Sort By: \nMost Recent");
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        ListView listview = (ListView) view.findViewById(R.id.listviewID);
        DefaultActivity activity = (DefaultActivity) getActivity();
        HashMap<String, ReviewData> allreviews = activity.getAllReviews();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (String key: allreviews.keySet()) {
            ReviewData reviewdatum = allreviews.get(key);
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("Restaurant Name", reviewdatum.restaurant_name);
            datum.put("Menu Item", reviewdatum.menuitem);
            System.out.println(reviewdatum.restaurant_name);
            System.out.println(reviewdatum.menuitem);

            data.add(datum);
        }
        SimpleAdapter adapter = new SimpleAdapter(getContext(), data,
                android.R.layout.simple_list_item_2,
                new String[] {"Restaurant Name", "Menu Item"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        listview.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyReviewsFragment.OnMyReviewsInteractionListener) {
            mListener = (MyReviewsFragment.OnMyReviewsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyReviewsInteractionListener");
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
                    mListener.onSortByButtonClicked();
                }
                break;
            case (R.id.fab):
                if (mListener != null) {
                    mListener.onFloatingButtonClicked();
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
    public interface OnMyReviewsInteractionListener {
        // TODO: Update argument type and name
        void onFilterButtonClicked();
        void onSortByButtonClicked();
        void onFloatingButtonClicked();
    }
}