package com.example.expressmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, RegistroUsuario.class));
            }
        });
        forg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, OlviPass.class ));
            }
        });
    }
}