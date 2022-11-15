package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressmarket.R;
import com.example.expressmarket.adapters.AdapterShop;
import com.example.expressmarket.models.ModelShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUsuario extends AppCompatActivity {

    private TextView nametv, emailtv, phonetv, tabShop, tabOrden;
    private RelativeLayout shopRl, ordenRl;
    private ImageButton logout, editbtn;
    private ImageView perfil;
    private RecyclerView shopsRw;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelShop> shopsList;
    private AdapterShop adapterShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_usuario);

        nametv= findViewById(R.id.nameTv);
        emailtv= findViewById(R.id.emailTv);
        phonetv= findViewById(R.id.phoneTv);
        tabShop= findViewById(R.id.tabShopsTv);
        tabOrden= findViewById(R.id.tabOrdersTv);
        logout= findViewById(R.id.logoutBtn);
        editbtn= findViewById(R.id.editProfileBtn);
        perfil= findViewById(R.id.perfilIv);
        shopRl= findViewById(R.id.shopsRl);
        ordenRl= findViewById(R.id.orderRl);
        shopsRw= findViewById(R.id.shopsRw);

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere un momento");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth= FirebaseAuth.getInstance();
        checkUser();
        //Tiendas
        showShopUI();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //modo offline
                makeOffLine();
            }
        });
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //editar perfil
                startActivity(new Intent(MainUsuario.this, EditPerfilUsuario.class));

            }
        });
        tabShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar tiendas
                showShopUI();
            }
        });
        tabOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar ordenes
                showOrdenUI();
            }
        });
    }

    private void showShopUI() {
        //Mostrar tiendas Ui, Ocultar Ordenes Ui
        shopRl.setVisibility(View.VISIBLE);
        ordenRl.setVisibility(View.GONE);

        tabShop.setTextColor(getResources().getColor(R.color.Black));
        tabShop.setBackgroundResource(R.drawable.shape_rect04);

        tabOrden.setTextColor(getResources().getColor(R.color.white));
        tabShop.setBackgroundResource(android.R.color.transparent);
    }

    private void showOrdenUI() {
        //Mostrar ordenes Ui, Ocultar tiendas  Ui
        shopRl.setVisibility(View.GONE);
        ordenRl.setVisibility(View.VISIBLE);

        tabShop.setTextColor(getResources().getColor(R.color.white));
        tabShop.setBackgroundResource(android.R.color.transparent);

        tabOrden.setTextColor(getResources().getColor(R.color.Black));
        tabShop.setBackgroundResource(R.drawable.shape_rect04);
    }

    private void makeOffLine() {
        progressDialog.setMessage("Verificando...");

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("online","false");

        //actualizacion db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //actualizacion bien
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // actualizacion mal
                        progressDialog.dismiss();
                        Toast.makeText(MainUsuario.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainUsuario.this, Login.class));
            finish();
        }else
        {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            //obtiene informacion del usuario
                            String name = ""+ds.child("nombre").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String tipo = ""+ds.child("tipo").getValue();
                            String estado = ""+ds.child("estado").getValue();

                            //manda informacion del usuario
                            nametv.setText(name);
                            emailtv.setText(email);
                            phonetv.setText(phone);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(perfil);
                            }catch (Exception e){
                                perfil.setImageResource(R.drawable.ic_person_gray);
                            }

                            //carga las tiendas del estado del usuario
                            loadShops(estado);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShops(String myestado) {

        shopsList= new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("tipo").equalTo("Venddedor")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //limpiar lista  despues de agregar
                        shopsList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelShop modelShop = ds.getValue(ModelShop.class);
                            String shopEstado = ""+ds.child("estado").getValue();

                            //muestra las tiendas en linea del estado del usuario
                            if (shopEstado.equals(myestado)){
                                shopsList.add(modelShop);
                            }

                            ///muestre todas las tiendas
                            //shopsList.add(modelShop);

                        }
                        //configurar adapter
                        adapterShop = new AdapterShop(MainUsuario.this, shopsList);
                        //enviar el adapter a recyclerview
                        shopsRw.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}