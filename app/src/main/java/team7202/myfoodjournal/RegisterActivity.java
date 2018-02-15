package team7202.myfoodjournal;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Charlie on 2/12/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    //UI References for Inputs
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText first_nameEditText;
    private TextInputEditText last_nameEditText;
    private TextInputEditText emailEditText;

    //Booleans
    protected Boolean emailValid;
    protected Boolean usernameValid;
    protected Boolean passwordValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set Input UIs
        usernameEditText = (TextInputEditText) findViewById(R.id.username_input);
        passwordEditText = (TextInputEditText) findViewById(R.id.password_input);
        first_nameEditText = (TextInputEditText) findViewById(R.id.first_name_input);
        last_nameEditText = (TextInputEditText) findViewById(R.id.last_name_input);
        emailEditText = (TextInputEditText) findViewById(R.id.email_input);

        //Button UIs
        final Button register_button = (Button) findViewById(R.id.register_button);
        RadioButton accept_terms = (RadioButton) findViewById(R.id.acceptTermsButton);
        RadioButton reject_terms = (RadioButton) findViewById(R.id.rejectTermsButton);
        register_button.setEnabled(false);


        emailEditText.addTextChangedListener(new TextValidator(emailEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isEmailValid(text)) {
                    emailEditText.setError(getString(R.string.error_invalid_email));
                } else if (TextUtils.isEmpty(text)) {
                    emailEditText.setError(getString(R.string.error_field_required));
                }
            }
        });

        usernameEditText.addTextChangedListener(new TextValidator(usernameEditText) {
            @Override
            public void validate(TextView textView, String text) {
               if (!isUsernameValid(text)) {
                   usernameEditText.setError(getString(R.string.error_incorrect_username));
               } else if (TextUtils.isEmpty(text)) {
                   usernameEditText.setError(getString(R.string.error_field_required));
               }
            }
        });

        passwordEditText.addTextChangedListener(new TextValidator(passwordEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isPasswordValid(text)) {
                    passwordEditText.setError(getString(R.string.error_invalid_password));
                } else if (TextUtils.isEmpty(text)) {
                    passwordEditText.setError(getString(R.string.error_field_required));
                }
            }
        });

        accept_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    register_button.setEnabled(true);
                }
            }
        });

        reject_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    register_button.setEnabled(false);
                }
            }
        });


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });

        Button return_button = (Button) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private boolean isUsernameValid(String username_input) {
        Boolean isMatch;
        String username_regex = "^\\w{5,8}$";
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

    private boolean isPasswordValid(String password_input) {
        Boolean isMatch;
        String password_regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
        Pattern pattern = Pattern.compile(password_regex);
        Matcher matcher = pattern.matcher(password_input);

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

    public void registerNewUser() {
        //TODO Talk to Ben about implementation

        //Pull in values from EditText Views
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String first_name = first_nameEditText.getText().toString();
        String last_name = last_nameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (!TextUtils.isEmpty(username)) {
            usernameValid = isUsernameValid(username);
        }
        if (!TextUtils.isEmpty(password)) {
            passwordValid = isPasswordValid(password);
        }

        if (usernameValid && passwordValid) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child(username).setValue(password);

            //Go to Default Activity
            Intent i = new Intent(RegisterActivity.this, DefaultActivity.class);
            startActivity(i);
            finish();
        }
    }

    /**
     * TextValidator class that uses TextWatcher to check input fields while
     * user completes each one individually. Real-time validation with Regex.
     */
    public abstract class TextValidator implements TextWatcher {
        private final TextView textView;

        public TextValidator(TextView textView) {
            this.textView = textView;
        }

        public abstract void validate(TextView textView, String text);

        @Override
        final public void afterTextChanged(Editable s) {
            String text = textView.getText().toString();
            validate(textView, text);
        }
        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /* Don't care */
        }
        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) {
            /* Don't care */
        }
    }
}
