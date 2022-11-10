package com.example.expressmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressmarket.activities.Addproducto;
import com.example.expressmarket.activities.Login;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainVendedorAct extends AppCompatActivity {

    private TextView nombreTv,nombreTienda,emailTv;
    private ImageButton btnSalir,btnAddprod,btnPerfil;
    private ImageView imgPerfil;

    private FirebaseAuth firebaseauth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor_act);
        nombreTv = findViewById(R.id.nombreTv);
        nombreTienda = findViewById(R.id.nombreTienda);
        emailTv = findViewById(R.id.emailTv);
        btnSalir = findViewById(R.id.btnSalir);
        btnPerfil = findViewById(R.id.btnEdit);
        btnAddprod = findViewById(R.id.btnAddprod);
        imgPerfil = findViewById(R.id.imgPerfil);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espera");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseauth = FirebaseAuth.getInstance();
        revisarUsuario();
        
        btnSalir.setOnClickListener(v -> {
            offlineUsr();
        });

        btnPerfil.setOnClickListener(v -> {
            startActivity(new Intent(MainVendedorAct.this, EditPerfilVendedor.class));
        });

        btnAddprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //abrir editar producto
                startActivity(new Intent(MainVendedorAct.this, Addproducto.class));

            }
        });
        

    }

    private void offlineUsr() {

        progressDialog.setMessage("Saliendo...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("En linea"," Fuera de linea");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseauth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                    firebaseauth.signOut();
                    revisarUsuario();
        })
                .addOnFailureListener((e)->{
                    progressDialog.dismiss();
                    Toast.makeText(MainVendedorAct.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    
                });
    }

    private void revisarUsuario() {
        FirebaseUser user = firebaseauth.getCurrentUser();
        if ( user == null){
            startActivity(new Intent(MainVendedorAct.this, Login.class));
            finish();
        }else {
            cargarInfo();
        }
        
    }

    private void cargarInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseauth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String nombre = ""+ds.child("nombre").getValue();
                            String tipo = ""+ds.child("tipo").getValue();
                            String correo = ""+ds.child("correo").getValue();
                            String nomTienda = ""+ds.child("nombreTienda").getValue();
                            String imPerfil = ""+ds.child("imagenPerfil").getValue();




                            nombreTv.setText(nombre);
                            nombreTienda.setText(nomTienda);
                            emailTv.setText(correo);
                            try {
                                Picasso.get().load(imPerfil).placeholder(R.drawable.ic_store_gray).into(imgPerfil);
                            }catch (Exception e){

                                imgPerfil.setImageResource(R.drawable.ic_store_gray);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}