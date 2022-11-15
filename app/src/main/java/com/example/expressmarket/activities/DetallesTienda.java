package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.expressmarket.R;
import com.example.expressmarket.models.ModeloProductos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetallesTienda extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopName,phoneTv, emailTv, openCloseTv, gastoTv, direccionTv, filterTv ;
    private ImageButton call,map,cartBtn, back,filterprduct;
    private EditText buscar;
    private RecyclerView products;
    private String shopUid;
    private String myLatitud, myLongitud;
    private String shpName, shpEmail, shpPhone, shpAddres,shpLatitud, shpLongitud;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModeloProductos> productsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tienda);

        shopIv= findViewById(R.id.shopIv);
        shopName= findViewById(R.id.shopnameTv);
        phoneTv= findViewById(R.id.phoneTv);
        emailTv= findViewById(R.id.emailTv);
        openCloseTv= findViewById(R.id.openCloseTv);
        gastoTv= findViewById(R.id.gastoEnvioEt);
        direccionTv= findViewById(R.id.addressTv);
        call= findViewById(R.id.callBt);
        map= findViewById(R.id.mapBt);
        cartBtn= findViewById(R.id.cartBtn);
        back= findViewById(R.id.back);
        buscar= findViewById(R.id.searchProductEt);
        filterprduct=findViewById(R.id.filterProductBtn);
        filterTv=findViewById(R.id.filterProductTv);
        products= findViewById(R.id.productsRv);

        //obtener el uid de la tiende desde intent
        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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
                            myLatitud = ""+ds.child("Latitud").getValue();
                            myLongitud = ""+ds.child("Longitud").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Obtiene informacion de la tienda
                String name = ""+snapshot.child("nombre").getValue();
                shpName = ""+snapshot.child("ShopNombre").getValue();
                shpPhone = ""+snapshot.child("ShopPhone").getValue();
                shpEmail = ""+snapshot.child("ShopEmail").getValue();
                shpLatitud = ""+snapshot.child("Latitud").getValue();
                shpLongitud = ""+snapshot.child("Longitud").getValue();
                String gasto= ""+snapshot.child("GastoEnvio").getValue();
                String profileImage= ""+snapshot.child("profileImage").getValue();
                String shpOpen= ""+snapshot.child("shpOpen").getValue();

                //Envia la informacion
                shopName.setText(shpName);
                emailTv.setText(shpEmail);
                gastoTv.setText(gasto);
                direccionTv.setText(shpAddres);
                phoneTv.setText(shpPhone);

                if (!shpOpen.equals("true")){
                    openCloseTv.setText("Abierto");
                }else
                {
                    openCloseTv.setText("Cerrado");
                }
                try {
                    Picasso.get().load(profileImage).into(shopIv);
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadShopProducts() {
    }




}