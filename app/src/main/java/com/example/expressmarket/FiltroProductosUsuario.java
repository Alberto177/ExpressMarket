package com.example.expressmarket;

import android.widget.Filter;

import com.example.expressmarket.adapters.AdapterProductUser;
import com.example.expressmarket.adapters.AdapterProducto;
import com.example.expressmarket.models.ModeloProductos;

import java.util.ArrayList;

public class FiltroProductosUsuario extends Filter {


    private AdapterProductUser adapter;
    private ArrayList<ModeloProductos> filterList;


    public FiltroProductosUsuario(AdapterProductUser adapter, ArrayList<ModeloProductos> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validacion de los datos
        if (constraint != null && constraint.length()>0){



            constraint = constraint.toString().toUpperCase();

            ArrayList<ModeloProductos> filteredModeloProductos = new ArrayList<>();
            for (int i= 0; i < filterList.size(); i++){
                if (filterList.get(i).getTituloProducto().toUpperCase().contains(constraint) ||
                        filterList.get(i).getCategoriaProducto().toUpperCase().contains(constraint)){
                    // aniadir los datos filtrados
                    filteredModeloProductos.add(filterList.get(i));

                }
            }
            results.count = filteredModeloProductos.size();
            results.values= filteredModeloProductos;
        }else {
            results.count = filterList.size();
            results.values= filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        adapter.productosList = (ArrayList<ModeloProductos>) results.values;
        //recargar adapter
        adapter.notifyDataSetChanged();
    }
}
