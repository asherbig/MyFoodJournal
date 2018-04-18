package team7202.myfoodjournal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfileFragment.OnEditProfileListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_MENU_OPTION = "menu_option";
    private static final String BASE_CONTEXT = "base_context";

    //parameters
    private String menuOptionParam;
    private static Context baseContext;
    private EditText usernameField, emailField, firstNameField, lastNameField;

    private OnEditProfileListener mListener;
    private View view;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditProfileFragment.
     */
    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
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
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        firstNameField = (EditText) view.findViewById(R.id.edit_first_name);
        lastNameField = (EditText) view.findViewById(R.id.edit_last_name);
        usernameField = (EditText) view.findViewById(R.id.edit_username);
        emailField = (EditText) view.findViewById(R.id.edit_email);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditProfileListener) {
            mListener = (OnEditProfileListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditProfileListener");
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
                    String firstname = null;
                    String lastname = null;
                    String username = null;
                    String email = null;

                    String suggestion = usernameField.getText().toString();
                    if (!suggestion.equals("") && isUsernameValid(suggestion)) {
                        username = suggestion;
                    }

                    suggestion = emailField.getText().toString();
                    if (!suggestion.equals("") && isEmailValid(suggestion)) {
                        email = suggestion;
                    }

                    suggestion = firstNameField.getText().toString();
                    if (!suggestion.equals("")) {
                        firstname = suggestion;
                    }

                    suggestion = lastNameField.getText().toString();
                    if (!suggestion.equals("")) {
                        lastname = suggestion;
                    }
                    mListener.onProfileSaveClicked(username, email, firstname, lastname);
                }
                break;
            case (R.id.cancel_button):
                if (mListener != null) {
                    mListener.onProfileCancelClicked();
                }
                break;
        }
    }

    private boolean isUsernameValid(String username_input) {
        Boolean isMatch;
        String username_regex = "^\\w{5,12}$";
        Pattern pattern = Pattern.compile(username_regex);
        Matcher matcher = pattern.matcher(username_input);

        //Check for Matches, whole string case
        if (matcher.matches()) {
            isMatch = true;
        } else {
            isMatch = false;
        }
        return isMatch;
    }

    private boolean isEmailValid(String email_input) {
        Boolean isMatch;
        String email_regex = "^([A-Z|a-z|0-9](\\.|_){0,1})+[A-Z|a-z|0-9]\\@([A-Z|a-z|0-9])+((\\.){0,1}[A-Z|a-z|0-9]){2}\\.[a-z]{2,3}$";
        Pattern pattern = Pattern.compile(email_regex);
        Matcher matcher = pattern.matcher(email_input);

        //Check for Matches, whole string case
        if (matcher.matches()) {
            isMatch = true;
        } else {
            isMatch = false;
        }
        return isMatch;
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
    public interface OnEditProfileListener {
        void onProfileSaveClicked(String username, String email, String firstname, String lastname);
        void onProfileCancelClicked();
    }
}
