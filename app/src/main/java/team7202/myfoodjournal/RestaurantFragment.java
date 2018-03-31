package team7202.myfoodjournal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zach on 3/30/2018.
 */

public class RestaurantFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_MENU_OPTION = "menu_option";

    //parameters
    private String menuOptionParam;

    private RestaurantFragment.OnRestaurantInteractionListener mListener;
    private View view;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param menuOptionParam the menu option being initialized.
     * @return A new instance of fragment ProfileFragment.
     */
    public static RestaurantFragment newInstance(String menuOptionParam) {
        RestaurantFragment fragment = new RestaurantFragment();
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
        String[] keyStrings = {"Menu Item", "Description", "Rating"};
        view = inflater.inflate(R.layout.restaurant_summary_fragment, container, false);
        TextView title = (TextView) view.findViewById(R.id.name_header);

        final DefaultActivity activity = (DefaultActivity) getActivity();
        final CharSequence name = activity.getRestaurantName().getName();
        title.setText(name);
        HashMap<String, ReviewData> allreviews = activity.getAllReviews();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        ListView listview = (ListView) view.findViewById(R.id.listviewID);
        for (String key: allreviews.keySet()) {
            ReviewData reviewdatum = allreviews.get(key);
            Map<String, String> datum = new HashMap<String, String>(3);
            datum.put(keyStrings[0], reviewdatum.menuitem);
            datum.put(keyStrings[1], reviewdatum.description);
            datum.put(keyStrings[2], reviewdatum.rating + "/5");
            data.add(datum);
        }
        SimpleAdapter adapter = new SimpleAdapter(getContext(), data,
                R.layout.myreview_row, keyStrings,
                new int[] {R.id.text1, R.id.text2, R.id.text3});
        listview.setAdapter(adapter);

        AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TextView menuItem = (TextView) view.findViewById(R.id.text1);
                TextView description = (TextView) view.findViewById(R.id.text2);
                TextView rating = (TextView) view.findViewById(R.id.text3);
                CharSequence[] information = {name, menuItem.getText(), rating.getText(),
                        description.getText()};
                Fragment fragment = DetailedReviewFragment.newInstance(information);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        };
        listview.setOnItemClickListener(listListener);
        Button sortByButton = (Button) view.findViewById(R.id.sortby_button);
        sortByButton.setOnClickListener(this);
        sortByButton.setText("Sort By: \nMost Recent");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RestaurantFragment.OnRestaurantInteractionListener) {
            mListener = (RestaurantFragment.OnRestaurantInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRestaurantInteractionListener");
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
            case (R.id.search_bar):
                if (mListener != null) {
                    mListener.onSearchBarClicked();
                }
                break;
            case (R.id.sortby_button):
                if (mListener != null) {
                    mListener.onSortByButtonClicked();
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
    public interface OnRestaurantInteractionListener {
        // TODO: Update argument type and name
        void onSearchBarClicked();
        void onSortByButtonClicked();
    }
}
