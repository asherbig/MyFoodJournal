package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailedUserFragment extends Fragment implements View.OnClickListener {

    private static Map<String, String> userInfo;
    private static View view;
    private static Switch followingSwitch;
    private static boolean isFollowing;
    private static TextView status;
    private static List<Map<String, String>> data;
    private static SimpleAdapter adapter;

    private DetailedUserFragment.OnDetailedUserInteractionListener mListener;
    public static DetailedUserFragment newInstance(Map info) {
        DetailedUserFragment fragment = new DetailedUserFragment();
        userInfo = info;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detailed_user, container, false);
        followingSwitch = (Switch) view.findViewById(R.id.followingSwitch);
        followingSwitch.setOnClickListener(this);
        status = (TextView) view.findViewById(R.id.status);

        TextView usernameField = (TextView) view.findViewById(R.id.username_text);
        usernameField.setText(userInfo.get("User Name"));

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("my_reviews").child(userInfo.get("Uid"));

        ListView listview = (ListView) view.findViewById(R.id.listview_user_reviews);
        data = new ArrayList<>();

        adapter = new SimpleAdapter(getContext(), data,
                R.layout.myreview_row,
                new String[] {"Restaurant Name", "Menu Item", "Description", "Rating"},
                new int[] {R.id.text1,
                        R.id.text2, R.id.text3, R.id.text4});
        listview.setAdapter(adapter);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                //filters = DefaultActivity.getMyReviewsFilters();
                for (DataSnapshot entry : dataSnapshot.getChildren()) {
                    Map reviewInfo = (Map) entry.getValue();
                    //check to see if the review should be included
                    //if (shouldInclude(reviewInfo, filters)) {
                        Map<String, String> datum = new HashMap<>();
                        datum.put("Restaurant Name", (String) reviewInfo.get("restaurant_name"));
                        datum.put("Menu Item", (String) reviewInfo.get("menuitem"));
                        datum.put("Description", (String) reviewInfo.get("description"));
                        datum.put("Rating", reviewInfo.get("rating") + "/5");
                        datum.put("Date Submitted", (String) reviewInfo.get("date_submitted"));
                        datum.put("User ID", (String) reviewInfo.get("userId"));
                        datum.put("Review ID", (String) reviewInfo.get("reviewId"));
                        datum.put("Restaurant ID", (String) reviewInfo.get("restaurant_id"));
                        data.add(datum);
                    //}
                }
                Collections.sort(data, time_comparator);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailedUserFragment.OnDetailedUserInteractionListener) {
            mListener = (DetailedUserFragment.OnDetailedUserInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailedUserInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case(R.id.followingSwitch):
                isFollowing = followingSwitch.isChecked();
                String statusText = (isFollowing)? "Yes" : "No";
                status.setText(statusText);
                break;
        }
    }

    private static Comparator<Map<String, String>> time_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return b.get("Date Submitted").compareTo(a.get("Date Submitted"));
        }
    };

    public interface OnDetailedUserInteractionListener {

    }
}
