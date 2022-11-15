package com.example.expressmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressmarket.activities.Addproducto;
import com.example.expressmarket.activities.EditPerfilVendedor;
import com.example.expressmarket.activities.Login;
import com.example.expressmarket.adapters.AdapterProducto;
import com.example.expressmarket.models.ModeloProductos;
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

public class MainVendedorAct extends AppCompatActivity {

    private TextView nombreTv,nombreTienda,emailTv, tabProductos, tabOrdenes, productosFiltrados;
    private EditText buscarProducto;
    private ImageButton btnSalir,btnAddprod,btnPerfil, filtroProductoIb;
    private ImageView imgPerfil;
    private RelativeLayout productosRl, ordenesRl;
    private RecyclerView productosRv;

    private FirebaseAuth firebaseauth;
    private ProgressDialog progressDialog;

    private ArrayList<ModeloProductos> listProductos;
    private AdapterProducto adapterProducto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor_act);
        productosFiltrados = findViewById(R.id.productosFiltrados);
        buscarProducto = findViewById(R.id.buscarProducto);
        filtroProductoIb = findViewById(R.id.filtroProductoIb);
        productosRv = findViewById(R.id.productosRv);
        nombreTv = findViewById(R.id.nombreTv);
        nombreTienda = findViewById(R.id.nombreTienda);
        emailTv = findViewById(R.id.emailTv);
        tabProductos = findViewById(R.id.tabProductos);
        tabOrdenes = findViewById(R.id.tabOrdenes);
        btnSalir = findViewById(R.id.btnSalir);
        btnPerfil = findViewById(R.id.btnEdit);
        btnAddprod = findViewById(R.id.btnAddprod);
        imgPerfil = findViewById(R.id.imgPerfil);
        productosRl= findViewById(R.id.productosRl);
        ordenesRl = findViewById(R.id.ordenesRl);

        
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espera");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseauth = FirebaseAuth.getInstance();
        revisarUsuario();
        mostrarProductosUI();
        mostrarOrdenesUI();
        cargarProductos();

        buscarProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int after, int before, int count) {
                try {
                    adapterProducto.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
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
        tabProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarProductosUI();

            }
        });
        tabOrdenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarOrdenesUI();

            }
        });
        filtroProductoIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainVendedorAct.this);
                builder.setTitle("Elije la categoria")
                        .setItems(Constants.productosCategorias1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String selected = Constants.productosCategorias1[which];
                                productosFiltrados.setText(selected);
                                if (selected.equals("Todas")){
                                    cargarProductos();
                                }else{
                                    loadProductosFiltrados(selected);
                                }
                            }
                        })
                        .show();
            }
        });
        

    }

    private void loadProductosFiltrados(String selected) {
        listProductos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseauth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listProductos.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String categoriaProductos = ""+ds.child("categoriaProducto").getValue();

                            if (selected.equals(categoriaProductos)){
                                ModeloProductos modeloProductos = ds.getValue(ModeloProductos.class);
                                listProductos.add(modeloProductos);
                            }



                        }
                        adapterProducto = new AdapterProducto(MainVendedorAct.this,listProductos);

                        productosRv.setAdapter(adapterProducto);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void cargarProductos() {
        listProductos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseauth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listProductos.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModeloProductos modeloProductos = ds.getValue(ModeloProductos.class);
                            listProductos.add(modeloProductos);

                        }
                        adapterProducto = new AdapterProducto(MainVendedorAct.this,listProductos);

                        productosRv.setAdapter(adapterProducto);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void mostrarOrdenesUI() {
        //Mostramamos los productos y los ocultamos dependiendo lo que seleccionemos

        productosRl.setVisibility(View.VISIBLE);
        ordenesRl.setVisibility(View.GONE);

        tabProductos.setTextColor(getResources().getColor(R.color.Black));
        tabProductos.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdenes.setTextColor(getResources().getColor(R.color.white));
        tabOrdenes.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void mostrarProductosUI() {
        productosRl.setVisibility(View.GONE);
        ordenesRl.setVisibility(View.VISIBLE);

        tabProductos.setTextColor(getResources().getColor(R.color.white));
        tabProductos.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdenes.setTextColor(getResources().getColor(R.color.Black));
        tabOrdenes.setBackgroundResource(R.drawable.shape_rect04);

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