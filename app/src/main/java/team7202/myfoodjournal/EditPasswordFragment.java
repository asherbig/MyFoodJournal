package team7202.myfoodjournal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditPasswordFragment.OnEditPasswordListener} interface
 * to handle interaction events.
 * Use the {@link EditPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPasswordFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_MENU_OPTION = "menu_option";

    //parameters
    private String menuOptionParam;
    private static Context baseContext;

    private OnEditPasswordListener mListener;
    private View view;

    public EditPasswordFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param menuOptionParam the menu option being initialized.
     * @return A new instance of fragment EditPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditPasswordFragment newInstance(String menuOptionParam, Context context) {
        EditPasswordFragment fragment = new EditPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MENU_OPTION, menuOptionParam);
        fragment.setArguments(args);
        baseContext = context;
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
        view = inflater.inflate(R.layout.fragment_edit_password, container, false);
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditPasswordListener) {
            mListener = (OnEditPasswordListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditPasswordListener");
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
        //hides the keyboard when a button is pressed
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        switch (v.getId()) {
            //Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.save_button):
                EditText oldPassField = (EditText) view.findViewById(R.id.old_pass);
                String oldPass = oldPassField.getText().toString();

                EditText newPassField1 = (EditText) view.findViewById(R.id.new_pass_1);
                String newPass = newPassField1.getText().toString();

                EditText newPassField2 = (EditText) view.findViewById(R.id.new_pass_2);
                String newPass2 = newPassField2.getText().toString();

                //TODO Add database communication
                Log.d("EDIT PASS", "newPass: " + newPass);
                Log.d("EDIT PASS", "newPass2: " + newPass2);
                Log.d("EDIT PASS", "equals?: " + newPass.equals(newPass2));
                if (!newPass.equals(newPass2)) {
                    CharSequence text = "The new passwords don't match! " +
                            "Please make your new passwords match.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(baseContext, text, duration);
                    toast.show();
                    break;
                }

                //if (oldPass == user.oldPass)
                if (mListener != null) {
                    mListener.onPassSaveClicked();
                }
                break;
            case (R.id.cancel_button):
                if (mListener != null) {
                    mListener.onPassCancelClicked();
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
    public interface OnEditPasswordListener {
        void onPassSaveClicked();
        void onPassCancelClicked();
    }
}
