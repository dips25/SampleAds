package com.example.sampleads.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sampleads.MainActivity;
import com.example.sampleads.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button button;
    EditText email;
    EditText password;
    EditText cnf_password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // mAuth = FirebaseAuth.getInstance();
        button = (Button) findViewById(R.id.button_login);
        email = (EditText) findViewById(R.id.lemail);
        password = (EditText) findViewById(R.id.lpassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In.Please Wait..");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();

            }
        });
    }

    private void login() {

        progressDialog.show();

        String uemail = email.getText().toString();
        String upassword = password.getText().toString();

        if (TextUtils.isEmpty(uemail)){

            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }else if (TextUtils.isEmpty(upassword)) {

            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }else {

            mAuth.signInWithEmailAndPassword(uemail, upassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                progressDialog.dismiss();

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }

                            // ...
                        }
                    });

        }


    }
}
