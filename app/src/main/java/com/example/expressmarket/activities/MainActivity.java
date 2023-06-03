package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.expressmarket.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PANTALLA COMPLETA
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        firebaseAuth= FirebaseAuth.getInstance();

        //Iniciar el login despues de 2sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user==null){
                    //usuario no logeado
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }else{
                    //user loggeado
                    checkUserType();

                }
            }
        }, 2000);
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
                                //usuario es vendedor
                                startActivity(new Intent(MainActivity.this, MainVendedor.class));
                                finish();
                            }else{
                                 //usuario es usuario
                                startActivity(new Intent(MainActivity.this, MainUsuario.class));
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