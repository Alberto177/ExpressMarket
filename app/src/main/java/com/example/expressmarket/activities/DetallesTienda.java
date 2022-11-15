package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressmarket.Constants;
import com.example.expressmarket.R;
import com.example.expressmarket.adapters.AdapterProductUser;
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
    private AdapterProductUser adapterProductUser;


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

        //Buscar
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterProductUser.getFilter().filter(charSequence);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        filterprduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetallesTienda.this);
                builder.setTitle("Filtrar Productos")
                        .setItems(Constants.productosCategorias1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String select = Constants.productosCategorias1[i];
                                filterTv.setText(select);
                                if (select.equals("Tdos")){
                                    //carga todos
                                    loadShopProducts();
                                }else
                                {
                                    adapterProductUser.getFilter().filter(select);
                                }
                            }
                        }).show();
            }
        });

    }

    private void openMap() {
        String direccion1 = "https://maps.google.com/maps?saddr="+ myLatitud+ "," + myLongitud + "&addr=" +shpLatitud
 + "," +shpLongitud;
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(direccion1));
    startActivity(intent);
    }

    private void dialPhone() {
        startActivity( new Intent(Intent.ACTION_DIAL, Uri.parse("Tel:"+Uri.encode(shpPhone))));
        Toast.makeText(this, ""+shpPhone, Toast.LENGTH_SHORT).show();
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
                shpPhone = ""+snapshot.child("Phone").getValue();
                shpEmail = ""+snapshot.child("Email").getValue();
                shpLatitud = ""+snapshot.child("Latitud").getValue();
                shpAddres = ""+snapshot.child("Direccion").getValue();
                shpLongitud = ""+snapshot.child("Longitud").getValue();
                String gasto= ""+snapshot.child("GastoEnvio").getValue();
                String profileImage= ""+snapshot.child("profileImage").getValue();
                String shpOpen= ""+snapshot.child("shpOpen").getValue();

                //Envia la informacion
                shopName.setText(shpName);
                emailTv.setText(shpEmail);
                gastoTv.setText(gasto);
                direccionTv.setText("Gasto de envio:$ "+shpAddres);
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
        productsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Productos")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Limpia la lista
                        productsList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModeloProductos modeloProductos = ds.getValue(ModeloProductos.class);
                            productsList.add(modeloProductos);
                        }
                        adapterProductUser= new AdapterProductUser(DetallesTienda.this,  productsList);
                        products.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }




}