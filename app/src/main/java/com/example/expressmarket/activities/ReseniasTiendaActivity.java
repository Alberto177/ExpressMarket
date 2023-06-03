package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.expressmarket.R;
import com.example.expressmarket.adapters.AdapterReviews;
import com.example.expressmarket.models.ModeloResenia;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReseniasTiendaActivity extends AppCompatActivity {

    private String shopUid;
    private ImageView imgPerfil;
    private TextView nombreTiendaTv, ratingsTv;
    private RatingBar ratingBar;
    private ImageButton btnSalir;
    private RecyclerView reviewsRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModeloResenia> reseniaArrayList;
    private AdapterReviews adapterReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resenias_tienda);


        shopUid = getIntent().getStringExtra("shopUid");

        imgPerfil = findViewById(R.id.imgPerfil);
        nombreTiendaTv = findViewById(R.id.nombreTiendaTv);
        ratingsTv = findViewById(R.id.ratingsTv);
        ratingBar = findViewById(R.id.ratingBar);
        btnSalir = findViewById(R.id.btnSalir);
        reviewsRv = findViewById(R.id.reviewsRv);

        firebaseAuth = FirebaseAuth.getInstance();

        cargarDetallesTienda();
        cargarResenias();
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
// FALTA AGREGARLO EN LA PARTE DE DETALLES DE LA TIENDA 15 40:00
    private float ratingSum = 0;
    private void cargarResenias() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //limpiar lista despues de aniadir datos

                        reseniaArrayList.clear();
                        ratingSum=0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                            ModeloResenia modeloResenia = ds.getValue(ModeloResenia.class);
                            reseniaArrayList.add(modeloResenia);

                        }
                        adapterReviews = new AdapterReviews(ReseniasTiendaActivity.this, reseniaArrayList);
                        reviewsRv.setAdapter(adapterReviews);
                        long numerodereviews = dataSnapshot.getChildrenCount();
                        float avgRating = ratingSum/numerodereviews;
                        ratingsTv.setText(String.format("%.2f", avgRating)+ " [" + numerodereviews+"] ");
                        ratingBar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void cargarDetallesTienda() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String nombreTienda = ""+dataSnapshot.child("shopName").getValue();
                        String perfilImg = ""+dataSnapshot.child("profileImage").getValue();

                        nombreTiendaTv.setText(nombreTienda);
                        try {
                            Picasso.get().load(perfilImg).placeholder(R.drawable.ic_person_gray).into(imgPerfil);
                        }catch (Exception e){
                            imgPerfil.setImageResource(R.drawable.ic_store_gray);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}