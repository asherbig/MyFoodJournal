package team7202.myfoodjournal;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

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
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
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
                   usernameEditText.setError(getString(R.string.error_invalid_username));
               } else if (TextUtils.isEmpty(text)) {
                   usernameEditText.setError(getString(R.string.error_field_required));
               }
            }
        });

        passwordEditText.addTextChangedListener(new TextValidator(passwordEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (TextUtils.isEmpty(text)) {
                    passwordEditText.setError(getString(R.string.error_field_required));
                } else if (!isPasswordValid(text) && text.length() < 8) {
                    passwordEditText.setError(getString(R.string.error_invalid_password));
                } else if (!isPasswordValid(text)) {
                    passwordEditText.setError(getString(R.string.error_incorrect_password));
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
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        LoginButton return_button2 = (LoginButton) findViewById(R.id.return_button2);
        return_button2.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_location"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                getUserDetailsFromFB(loginResult.getAccessToken());
                Toast.makeText(getApplicationContext(),"fb user success",Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();

            }
            @Override
            public void onCancel() {

                Toast.makeText(getApplicationContext(),"fb user canceled",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException e) {

                Toast.makeText(getApplicationContext(),"fb error",Toast.LENGTH_SHORT).show();
            }
        });


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getUserDetailsFromFB(AccessToken accessToken) {
        GraphRequest req=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Toast.makeText(getApplicationContext(),"graph request completed",Toast.LENGTH_SHORT).show();
                try{
                    if (object.has("email")) {
                        String email =  object.getString("email");
                        emailEditText.setText(email);
                    }
                    if (object.has("first_name") && object.has("last_name")) {
                        String first = object.getString("first_name");
                        String last = object.getString("last_name");
                        first_nameEditText.setText(first);
                        last_nameEditText.setText(last);
                    } else {
                        String name = object.getString("name");
                        String[] delm = name.split(" ");
                        first_nameEditText.setText(delm[0]);
                        last_nameEditText.setText(delm[1]);
                    }



                }catch (JSONException e)
                {
                    System.out.println(e.getMessage());

                    Toast.makeText(getApplicationContext(),"graph request error : "+e.getMessage(),Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
        req.setParameters(parameters);
        req.executeAsync();
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

    private boolean isPasswordValid(String password_input) {
        Boolean isMatch;
        String password_regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
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
        //Pull in values from EditText Views
        final String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        final String first_name = first_nameEditText.getText().toString();
        final String last_name = last_nameEditText.getText().toString();
        final String email = emailEditText.getText().toString();

        // Reset errors.
        usernameEditText.setError(null);
        passwordEditText.setError(null);
        first_nameEditText.setError(null);
        last_nameEditText.setError(null);
        emailEditText.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            cancel = true;
        } else if (!isPasswordValid(password) && password.length() < 8) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_incorrect_password));
            focusView = passwordEditText;
            cancel = true;
        }

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            focusView = usernameEditText;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            usernameEditText.setError(getString(R.string.error_invalid_username));
            focusView = usernameEditText;
            cancel = true;
        }

        // Check for a valid email
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();
                                user.updateProfile(profileUpdates);

                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                UserData data = new UserData(username, email, first_name, last_name, user.getUid());
                                userRef.setValue(data);

                                Intent i = new Intent(RegisterActivity.this, DefaultActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                                focusView.requestFocus();
                            }

                        }
                    });
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
