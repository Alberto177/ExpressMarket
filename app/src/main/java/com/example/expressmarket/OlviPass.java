package com.example.expressmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class OlviPass extends AppCompatActivity {

    private ImageButton back;
    private EditText email;
    private Button recuperar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvi_pass);

        back= findViewById(R.id.back);
        email= findViewById(R.id.emailEt);
        recuperar=findViewById(R.id.recoverBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}