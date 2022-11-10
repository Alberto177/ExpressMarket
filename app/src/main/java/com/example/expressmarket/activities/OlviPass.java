package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.expressmarket.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class OlviPass extends AppCompatActivity {

    private ImageButton back;
    private EditText email;
    private Button recuperar;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvi_pass);

        back= findViewById(R.id.back);
        email= findViewById(R.id.emailEt);
        recuperar=findViewById(R.id.recoverBtn);

        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Por favor espera");
        progressDialog.setCanceledOnTouchOutside(false);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperarPass();
            }
        });

    }

    private String emailn;
    private void recuperarPass() {
        emailn= email.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailn).matches()){
            Toast.makeText(this, "Email invalido", Toast.LENGTH_SHORT).show();
        }
        progressDialog.setMessage("Enviando instrucciones para recuperar");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(emailn)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //instrucciones enviadas
                        progressDialog.dismiss();
                        Toast.makeText(OlviPass.this, "Instrucciones enviadas a tu correo", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //instrucciones no enviadas
                        progressDialog.dismiss();
                        Toast.makeText(OlviPass.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}