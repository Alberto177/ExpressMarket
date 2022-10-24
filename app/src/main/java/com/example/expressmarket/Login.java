package com.example.expressmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    private EditText email, pass;
    private TextView forg, noAccount;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email= findViewById(R.id.emailEt);
        pass= findViewById(R.id.passwordEt);
        forg= findViewById(R.id.forgotTv);
        login= findViewById(R.id.loginBtn);
        noAccount= findViewById(R.id.noAccountTv);
    }
}