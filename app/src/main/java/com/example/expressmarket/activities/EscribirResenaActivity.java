package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressmarket.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EscribirResenaActivity extends AppCompatActivity {
    private String shopUid;
    private ImageButton btnSalir;
    private ImageView imgPerfil;
    private TextView nombreTiendaTv;
    private EditText reviewEt;
    private RatingBar ratingBar;
    private FloatingActionButton submitBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_resena);

        btnSalir = findViewById(R.id.btnSalir);
        imgPerfil = findViewById(R.id.imgPerfil);
        nombreTiendaTv = findViewById(R.id.nombreTiendaTv);
        reviewEt = findViewById(R.id.reviewEt);
        submitBtn = findViewById(R.id.submitBtn);
        ratingBar = findViewById(R.id.ratingBar);

        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();

        cargarInfoTienda();

        cargarResena();

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        
        //subir datos
        
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });
        
        
    }

    private void cargarInfoTienda() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nombreTienda = ""+dataSnapshot.child("shopName").getValue();
                String imgTienda = ""+dataSnapshot.child("profileImage").getValue();

                nombreTiendaTv.setText(nombreTienda);
                try {
                    Picasso.get().load(imgTienda).placeholder(R.drawable.ic_store_gray);
                }catch (Exception e){
                    imgPerfil.setImageResource(R.drawable.ic_store_gray);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void cargarResena() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String uid = ""+dataSnapshot.child("uid").getValue();
                        String ratings = ""+dataSnapshot.child("ratings").getValue();
                        String review = ""+dataSnapshot.child("review").getValue();
                        String timestamp = ""+dataSnapshot.child("timestamp").getValue();

                        float myRating = Float.parseFloat(ratings);
                        ratingBar.setRating(myRating);
                        reviewEt.setText(review);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void inputData() {
        String ratings = ""+ratingBar.getRating();
        String review = reviewEt.getText().toString().trim();

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+ firebaseAuth.getUid());
        hashMap.put("ratings",""+ ratings);
        hashMap.put("review",""+ review);
        hashMap.put("timestamp",""+ timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(EscribirResenaActivity.this, "Reseña publicada...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EscribirResenaActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}