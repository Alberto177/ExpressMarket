package com.example.expressmarket.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressmarket.R;
import com.example.expressmarket.activities.FiltroProductos;
import com.example.expressmarket.models.ModeloProductos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProducto extends RecyclerView.Adapter<AdapterProducto.HolderProducto> implements Filterable {

    private Context context;
    public ArrayList<ModeloProductos>productosList, filterList;
    private FiltroProductos filter;

    public AdapterProducto(Context context, ArrayList<ModeloProductos> productosList) {
        this.context = context;
        this.productosList = productosList;
        this.filterList= productosList;
    }

    @NonNull
    @Override
    public HolderProducto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fila_productos_vendedor, parent, false);
        return new HolderProducto(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProducto holder, int position) {
        ModeloProductos modeloProductos = productosList.get(position);
        String id = modeloProductos.getProductId();
        String uid = modeloProductos.getUid();
        String descuentoDisponible = modeloProductos.getDescuentoDisponible();
        String notaDescuento = modeloProductos.getNotaDescuento();
        String precioDescuento = modeloProductos.getPrecioDescuento();
        String categoriaProducto = modeloProductos.getCategoriaProducto();
        String descripcionProducto = modeloProductos.getDescripcionProducto();
        String icon = modeloProductos.getImagenProducto();
        String cantidad = modeloProductos.getCantidadProducto();
        String titulo = modeloProductos.getTituloProducto();
        String timestamp = modeloProductos.getTimestamp();
        String precioOriginal= modeloProductos.getPrecioOriginal();

        // set data

        holder.tituloTv.setText(titulo);
        holder.cantidadTv.setText(cantidad);
        holder.notaDescuento.setText(notaDescuento);
        holder.precioDescuento.setText("$"+precioDescuento);
        holder.precioOriginal.setText("$"+precioOriginal);
        if (descuentoDisponible.equals("true")){
            holder.precioDescuento.setVisibility(View.VISIBLE);
            holder.notaDescuento.setVisibility(View.VISIBLE);
            holder.precioOriginal.setPaintFlags(holder.precioOriginal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            holder.precioDescuento.setVisibility(View.GONE);
            holder.notaDescuento.setVisibility(View.GONE);

        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_carritocompra_azul).into(holder.iconoProducto);

        }catch (Exception e){
            holder.iconoProducto.setImageResource(R.drawable.ic_carritocompra_azul);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FiltroProductos(this, filterList);
        }
        return filter;
    }

    class HolderProducto extends RecyclerView.ViewHolder{

        private ImageView iconoProducto;
        private TextView notaDescuento, tituloTv, cantidadTv, precioDescuento, precioOriginal;
        /*Vistas del recycleview*/
        public HolderProducto(@NonNull View itemView){
            super(itemView);
            iconoProducto =  itemView.findViewById(R.id.iconoProducto);
            notaDescuento =  itemView.findViewById(R.id.notaDescuento);
            tituloTv =  itemView.findViewById(R.id.tituloTv);
            cantidadTv =  itemView.findViewById(R.id.cantidadTv);
            precioDescuento =  itemView.findViewById(R.id.precioDescuento);
            precioOriginal =  itemView.findViewById(R.id.precioOriginal);
        }
    }
}
