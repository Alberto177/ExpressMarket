package com.example.expressmarket.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressmarket.R;
import com.example.expressmarket.models.ModeloProductos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductUser extends  RecyclerView.Adapter<AdapterProductUser.HolderProductUser>{

    private Context context;
    public ArrayList<ModeloProductos> productsList;

    public AdapterProductUser(Context context, ArrayList<ModeloProductos> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);

        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

        //Obtiene la informacion
        ModeloProductos modeloProductos = productsList.get(position);
        String descuentoDisponible = modeloProductos.getDescuentoDisponible();
        String notaDescuento = modeloProductos.getNotaDescuento();
        String precioDescuento =  modeloProductos.getPrecioDescuento();
        String categoriaProducto =  modeloProductos.getCategoriaProducto();
        String precioOriginal =  modeloProductos.getPrecioOriginal();
        String descripcionProducto =  modeloProductos.getDescripcionProducto();
        String tituloProducto =  modeloProductos.getTituloProducto();
        String cantidadProducto =  modeloProductos.getCantidadProducto();
        String productId =  modeloProductos.getProductId();
        String timestamp =  modeloProductos.getTimestamp();
        String imagenProducto =  modeloProductos.getImagenProducto();

        //Manda la informacion
        holder.titleTv.setText(tituloProducto);
        holder.discountedNoteTv.setText(notaDescuento);
        holder.descriptionTv.setText(descripcionProducto);
        holder.originalPriceTv.setText("$"+precioOriginal);
        holder.discountedPriceTv.setText("$"+precioDescuento);


        if (descuentoDisponible.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else
        {
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(imagenProducto).placeholder(R.drawable.ic_add_shopping_primary).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        }

        holder.addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregar nuevo producto al carrito
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //muestra los detalles de los productos

            }
        });





    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv, descriptionTv,addtoCart, discountedPriceTv, originalPriceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addtoCart = itemView.findViewById(R.id.addtoCartTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);


        }
    }
}
