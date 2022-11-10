package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressmarket.MainUsuario;
import com.example.expressmarket.MainVendedor;
import com.example.expressmarket.R;
import com.example.expressmarket.RegistroUsuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText email, pass;
    private TextView forg, noAccount;
    private Button login;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email= findViewById(R.id.emailEt);
        pass= findViewById(R.id.passwordEt);
        forg= findViewById(R.id.forgotTv);
        login= findViewById(R.id.loginBtn);
        noAccount= findViewById(R.id.noAccountTv);

        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Por favor espera");
        progressDialog.setCanceledOnTouchOutside(false);

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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                longinUser();
            }
        });

    }

    private String emailn, passn;
    private void longinUser() {
        emailn = email.getText().toString().trim();
        passn= pass.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailn).matches()){
            Toast.makeText(this, "Email invalido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passn)){
            Toast.makeText(this, "Ingrese su password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Iniciando sesion");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailn, passn)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //inicio bien
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //inicio mal
                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void makeMeOnline() {
        progressDialog.setMessage("Verificando usuario...");

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("online","true");

        //actualizacion db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //actualizacion bien
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // actualizacion mal
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {
        //si es vendedor se va al menu vendedor
        //si es usuario se va al menu de usuario

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String tipo= ""+ds.child("tipo").getValue();
                            if (tipo.equals("Vendedor")){
                                progressDialog.dismiss();
                                //usuario es vendedor
                                startActivity(new Intent(Login.this, MainVendedor.class));
                                finish();
                            }else{
                                progressDialog.dismiss();
                                //usuario es usuario
                                startActivity(new Intent(Login.this, MainUsuario.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}