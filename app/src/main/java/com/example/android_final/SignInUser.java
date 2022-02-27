package com.example.android_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInUser extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView editPassword;
    private EditText  editEmail;
    private Button signIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_user);

        signIn=(Button) findViewById(R.id.btnSignIn);
        signIn.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.editEmailSignIn);
        editPassword =(AutoCompleteTextView) findViewById(R.id.editPassword);
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignIn:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email= editEmail.getText().toString().trim();
        String password= editPassword.getText().toString().trim();

        if(email.isEmpty()){
            editEmail.setError("Email field is empty!");
            editEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Provide correct format of email!");
            editEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editPassword.setError("Password is required!");
            editPassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   startActivity(new Intent(SignInUser.this, HomePage.class));

                }
                else {
                    Toast.makeText(SignInUser.this, "Failed to login! Try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}