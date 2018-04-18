package team7202.myfoodjournal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_MENU_OPTION = "menu_option";

    //parameters
    private String menuOptionParam;

    private OnProfileInteractionListener mListener;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button editButton = (Button) view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(this);
        Button passButton = (Button) view.findViewById(R.id.change_pass_button);
        passButton.setOnClickListener(this);

        TextView usernameField = (TextView) view.findViewById(R.id.profile_username);
        usernameField.setText(currentUser.getDisplayName());
        TextView emailField = (TextView) view.findViewById(R.id.profile_email);
        emailField.setText(currentUser.getEmail());
        final TextView firstNameField = (TextView) view.findViewById(R.id.first_name_field);
        final TextView lastNameField = (TextView) view.findViewById(R.id.last_name_field);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUser.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = (String) dataSnapshot.child("firstname").getValue();
                firstNameField.setText(firstName);

                String lastName = (String) dataSnapshot.child("lastname").getValue();
                lastNameField.setText(lastName);
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
        if (context instanceof OnProfileInteractionListener) {
            mListener = (OnProfileInteractionListener) context;
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
            case (R.id.edit_button):
                if (mListener != null) {
                    mListener.onEditButtonClicked();
                }
                break;
            case (R.id.change_pass_button):
                if (mListener != null) {
                    mListener.onChangePassClicked();
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
    public interface OnProfileInteractionListener {
        // TODO: Update argument type and name
        void onEditButtonClicked();
        void onChangePassClicked();
    }
}
