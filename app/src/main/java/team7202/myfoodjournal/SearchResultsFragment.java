package team7202.myfoodjournal;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zach on 4/18/2018.
 */

public class SearchResultsFragment extends Fragment {

    private static String query;
    private static View view;
    private static FirebaseUser user;
    private static List<Map<String, String>> data;
    private static SimpleAdapter adapter;

    public static SearchResultsFragment newInstance(String text) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        query = text;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_results, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");

        data = new ArrayList<>();
        ListView listview = (ListView) view.findViewById(R.id.listviewID);

        adapter = new SimpleAdapter(getContext(), data,
                R.layout.search_result_row,
                new String[] {"User Name", "First Name", "Last Name"},
                new int[] {R.id.username_text, R.id.firstname_text, R.id.lastname_text});
        listview.setAdapter(adapter);

        userRef.orderByChild("username").equalTo(query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot entry : dataSnapshot.getChildren()) {
                    Map userInfo = (Map) entry.getValue();
                    boolean isPublic = (boolean) userInfo.get("isPublic");

                    if (isPublic) {
                        Map<String, String> datum = new HashMap<>();
                        datum.put("User Name", (String) userInfo.get("username"));
                        datum.put("First Name", (String) userInfo.get("firstname"));
                        datum.put("Last Name", (String) userInfo.get("lastname"));
                        datum.put("Uid", (String) userInfo.get("uid"));
                        data.add(datum);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map<String, String> info = (Map<String, String>) adapterView.getItemAtPosition(position);
                Fragment fragment = DetailedUserFragment.newInstance(info);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(info.get("User Name")).commit();
            }
        };

        listview.setOnItemClickListener(listListener);
        return view;
    }
}
