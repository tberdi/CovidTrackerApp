package com.example.android_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button register;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register=(Button) findViewById(R.id.btnSignUp);
        register.setOnClickListener(this);
        signIn=(Button) findViewById(R.id.btnSignIn);
        signIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignUp:
                startActivity(new Intent(this, RegisterUser.class));
            case R.id.btnSignIn:
                startActivity(new Intent(this, SignInUser.class));
        }
    }
}