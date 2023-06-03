package com.example.expressmarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressmarket.R;
import com.example.expressmarket.activities.DetallesTienda;
import com.example.expressmarket.models.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop> {

    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_shop, parent, false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        ModelShop modelShop = shopsList.get(position);
        //obtener informacion
        String tipo = modelShop.getTipo();
        String direccion = modelShop.getDireccion();
        String estado = modelShop.getEstado();
        String ciudad = modelShop.getCiudad();
        String gasto = modelShop.getGasto();
        String latitud = modelShop.getLatitud();
        String online = modelShop.getOnline();
        String nombre = modelShop.getName();
        String phone = modelShop.getPhone();
        String uid = modelShop.getUid();
        String tiempo = modelShop.getTiempo();
        String open = modelShop.getShopopen();
        String perfil = modelShop.getProfileimagen();
        String shopname = modelShop.getShopname();

        //mandar informacion
        holder.shopname.setText(shopname);
        holder.phone.setText(phone);
        holder.address.setText(direccion);
        //checa si esta en linea
        if (online.equals("true")){
            //el dueno de la tienda esta en linea
            holder.online.setVisibility(View.VISIBLE);
        }else {
            //el dueno de la tienda no esta en linea
            holder.online.setVisibility(View.GONE);
        }
        // checa si la tienda esta abierta
        if (open.equals("true")){
            //tienda en linea
            holder.shopclose.setVisibility(View.GONE);
        }else{
            //tienda no en linea
            holder.shopclose.setVisibility(View.VISIBLE);
        }

        try{
            Picasso.get().load(perfil).placeholder(R.drawable.ic_store_gray).into(holder.shop);
        }catch (Exception e){
            holder.shop.setImageResource(R.drawable.ic_store_gray);
        }

        //mostrar detalles de tienda
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetallesTienda.class);
                intent.putExtra("shopUid", uid);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return shopsList.size(); //retorna el numero de registros
    }

    class HolderShop extends RecyclerView.ViewHolder{

        //ui vistas de la fila shop
        private ImageView shop,online;
        private TextView shopclose, shopname,phone, address;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView){
            super(itemView);

            shop= itemView.findViewById(R.id.shopIv);
            online= itemView.findViewById(R.id.onlineIv);
            shopclose= itemView.findViewById(R.id.shopCloseTv);
            shopname= itemView.findViewById(R.id.shopnameTv);
            phone= itemView.findViewById(R.id.phoneTv);
            address= itemView.findViewById(R.id.addressTv);
            ratingBar= itemView.findViewById(R.id.ratingbar);


        }
    }
}
