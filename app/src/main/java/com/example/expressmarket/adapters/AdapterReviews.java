package com.example.expressmarket.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressmarket.R;
import com.example.expressmarket.models.ModeloResenia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReviews extends RecyclerView.Adapter<AdapterReviews.HolderReview> {

    private Context context;
    private ArrayList<ModeloResenia> reviewArrayList;

    public AdapterReviews(Context context, ArrayList<ModeloResenia> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout fila_resenia
        View view = LayoutInflater.from(context).inflate(R.layout.fila_resenias, parent, false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {

        ModeloResenia modeloResenia = reviewArrayList.get(position);
        String uid = modeloResenia.getUid();
        String ratings = modeloResenia.getRatings();
        String timestamp = modeloResenia.getTimestamp();
        String review = modeloResenia.getReview();

        cargarDetallesUsuario(modeloResenia, holder);

        // convertir el timestamp en formato dd/MM/yyyy

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(review);
        holder.fechaTv.setText(dateFormat);



    }

    private void cargarDetallesUsuario(ModeloResenia modeloResenia, HolderReview holder) {

        String uid =  modeloResenia.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String nombre = ""+dataSnapshot.child("name").getValue();
                        String imgPerfil = ""+dataSnapshot.child("profileImage").getValue();

                        holder.nombreTv.setText(nombre);
                        try {
                            Picasso.get().load(imgPerfil).placeholder(R.drawable.ic_person_gray).into(holder.imgPerfil);
                        }catch (Exception e){
                            holder.imgPerfil.setImageResource(R.drawable.ic_store_gray);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size(); // retorna el tamanio de la lista
    }

    class  HolderReview extends RecyclerView.ViewHolder{

        //vistas de los layouts de la fila resenias

        private ImageView imgPerfil;
        private TextView nombreTv,fechaTv, reviewTv;
        private RatingBar ratingBar;




        public HolderReview(@NonNull View itemView) {
            super(itemView);

            imgPerfil = itemView.findViewById(R.id.imgPerfil);
            nombreTv = itemView.findViewById(R.id.nombreTv);
            fechaTv = itemView.findViewById(R.id.fechaTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);



        }
    }


}
