package team7202.myfoodjournal;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends Activity {

    private EditText emailEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (EditText) findViewById(R.id.email);

        final Button send_password_reset = (Button) findViewById(R.id.send_password_reset);

        emailEditText.addTextChangedListener(new ForgotPasswordActivity.TextValidator(emailEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isEmailValid(text)) {
                    emailEditText.setError(getString(R.string.error_invalid_email));
                } else if (TextUtils.isEmpty(text)) {
                    emailEditText.setError(getString(R.string.error_field_required));
                }
            }
        });

        send_password_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordReset();
            }
        });

        mAuth = FirebaseAuth.getInstance();
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

    public void sendPasswordReset() {
        String email = emailEditText.getText().toString();

        emailEditText.setError(null);

        boolean cancel = false;
        View focusView = null;

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
            mAuth.sendPasswordResetEmail(email);
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
