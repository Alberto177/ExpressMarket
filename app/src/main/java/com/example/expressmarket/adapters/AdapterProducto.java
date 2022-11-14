package com.example.expressmarket.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressmarket.R;
import com.example.expressmarket.activities.EditarProductoActivity;
import com.example.expressmarket.activities.FiltroProductos;
import com.example.expressmarket.models.ModeloProductos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

                botonDetalles(modeloProductos);



            }
        });

    }

    private void botonDetalles(ModeloProductos modeloProductos) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_detalle_productos_vendedor,null);
        bottomSheetDialog.setContentView(view);



        ImageButton btnSalir = view.findViewById(R.id.btnSalir);
        ImageButton btnEditar = view.findViewById(R.id.btnEditar);
        ImageButton btnBorrar = view.findViewById(R.id.btnBorrar);
        ImageView iconoProducto = view.findViewById(R.id.iconoProducto);
        TextView notaDescuentoTv = view.findViewById(R.id.notaDescuento);
        TextView tituloTv = view.findViewById(R.id.tituloTv);
        TextView descripcionTv = view.findViewById(R.id.descripcionTv);
        TextView categoriaTv = view.findViewById(R.id.categoriaTv);
        TextView cantidadTv = view.findViewById(R.id.cantidadTv);
        TextView precioDescuentoTv = view.findViewById(R.id.precioDescuentoTv);
        TextView precioOriginalTv = view.findViewById(R.id.precioOriginalTv);

        //tomar Datos

        final String id = modeloProductos.getProductId();
        String uid = modeloProductos.getUid();
        String descuentoDisponible = modeloProductos.getDescuentoDisponible();
        String notaDescuento = modeloProductos.getNotaDescuento();
        String precioDescuento = modeloProductos.getPrecioDescuento();
        String categoriaProducto = modeloProductos.getCategoriaProducto();
        String descripcionProducto = modeloProductos.getDescripcionProducto();
        String icon = modeloProductos.getImagenProducto();
        String cantidad = modeloProductos.getCantidadProducto();
        final String titulo = modeloProductos.getTituloProducto();
        String timestamp = modeloProductos.getTimestamp();
        String precioOriginal= modeloProductos.getPrecioOriginal();

        //dar datos

        tituloTv.setText(titulo);
        descripcionTv.setText(descripcionProducto);
        categoriaTv.setText(categoriaProducto);
        cantidadTv.setText(cantidad);
        notaDescuentoTv.setText(notaDescuento);
        precioDescuentoTv.setText("$"+ precioDescuento);
        precioOriginalTv.setText("$"+ precioOriginal);

        if (descuentoDisponible.equals("true")){
            precioDescuentoTv.setVisibility(View.VISIBLE);
            notaDescuentoTv.setVisibility(View.VISIBLE);
            precioOriginalTv.setPaintFlags(precioOriginalTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            precioDescuentoTv.setVisibility(View.GONE);
            notaDescuentoTv.setVisibility(View.GONE);

        }
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_carritocompra_azul).into(iconoProducto);

        }catch (Exception e){
            iconoProducto.setImageResource(R.drawable.ic_carritocompra_azul);

        }

        bottomSheetDialog.show();

        //Editar

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //abrir activity editar
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(context, EditarProductoActivity.class);
                intent.putExtra("productoId", id);
                context.startActivity(intent);



            }
        });
        //borrar
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                //mostrar dialogo de confirmacion para eliminar
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Borrar")
                        .setMessage("¿Estás seguro que quieres eliminar este producto : "+titulo+ " ?")
                        .setPositiveButton("BORRAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //borrar
                                borrarProducto(id);


                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Cancelar Accion

                                dialog.dismiss();

                            }
                        });


            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();

            }
        });
    }

    private void borrarProducto(String id) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                          //Producto eliminado
                        Toast.makeText(context, "Producto Eliminado...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();


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
