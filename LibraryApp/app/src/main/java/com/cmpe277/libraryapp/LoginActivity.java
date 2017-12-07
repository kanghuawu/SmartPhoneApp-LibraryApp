package com.cmpe277.libraryapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    public static final String LIB_PREFS = "LibPrefs";
    public static final String IS_LIBRARIAN = "IsLibrarian";
    public static final String EMAIL = "email";

    private FirebaseAuth mAuth;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null ) {
//            Log.d("LibraryApp", "Current user" + mAuth.getCurrentUser().getEmail().toString());

            switchPage();
        }
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v) {
        attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        attemptLogin(email, password);
    }

    private void attemptLogin(String email, String password) {
        if (email.equals("") || password.equals("")) {
            return;
        }

        Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("LibraryApp", "singInWithEmail() onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d("LibraryApp", "Problem signing in: " + task.getException());
                    showErrorDialog("There was a problem signing in");
                } else {
                    switchPage();
                }
            }
        });

    }

    private void saveUserIdentity() {
        String email = mEmailView.getText().toString();
        Log.d("LibraryApp", "saveUserIdentity " + email);
        if (email.equals("")) {
            email = mAuth.getCurrentUser().getEmail().toString();
        }
        Log.d("LibraryApp", "saveUserIdentity " + email);
        boolean isLibrarian = email.contains("@sjsu.edu");
        SharedPreferences prefs = getSharedPreferences(LIB_PREFS, 0);
        Log.d("LibraryApp", "saveUserIdentity " + String.valueOf(isLibrarian));
        prefs.edit().putBoolean(IS_LIBRARIAN, isLibrarian).apply();
        prefs.edit().putString(EMAIL, email).apply();
    }

    private void switchPage() {
        Intent intent = new Intent(LoginActivity.this, LandingPageActivity.class);
        saveUserIdentity();
        finish();
        startActivity(intent);
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
