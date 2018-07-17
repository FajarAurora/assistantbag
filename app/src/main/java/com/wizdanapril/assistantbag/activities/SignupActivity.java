package com.wizdanapril.assistantbag.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.utils.Constant;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText inputEmail, inputPassword, inputDevice;
    private Button signupButton;
    private ProgressBar progressBar;

    String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.et_email);
        inputPassword = (EditText) findViewById(R.id.et_password);
        inputDevice = (EditText) findViewById(R.id.et_device);
        signupButton = (Button) findViewById(R.id.bt_signup);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                final String deviceId = inputDevice.getText().toString();
                if (!email.equals("") && email.contains("@")) {
                     account = email.substring(0, email.indexOf("@"));
                }



                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_empty_email),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_empty_password),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(deviceId)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_empty_device),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.INVISIBLE);

                // Create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this,
                                new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this,
                                            getString(R.string.error_auth_failed),
                                            Toast.LENGTH_SHORT).show();
                                    signupButton.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(SignupActivity.this,
                                            getString(R.string.success_signup), Toast.LENGTH_SHORT).show();

                                    // Login and set deviceId to database
                                    DatabaseReference deviceReference = FirebaseDatabase.getInstance().getReference(Constant.DEVICE)
                                            .child(deviceId);
                                    deviceReference.child("id").setValue(deviceId);
                                    deviceReference.child("currentUser").setValue(email);

                                    final DatabaseReference userReference = FirebaseDatabase.getInstance()
                                            .getReference(Constant.USER);
                                    if (userReference.child(account).child("name").getKey() == null) {
                                        userReference.child(account).child("name").setValue("User");
                                    }

                                    SharedPreferences preferences =
                                            getSharedPreferences("LoggedAccount", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("userEmail", email);
                                    editor.putString("userAccount", account);
                                    editor.putString("deviceId", inputDevice.getText().toString());
                                    editor.apply();

                                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

            }
        });

        Button loginButton = (Button) findViewById(R.id.bt_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}
