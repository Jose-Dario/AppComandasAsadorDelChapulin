package com.example.asadordelchapulin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class AdapterProductosOrdenados extends BaseAdapter {
    public Context context;
    private List<ProductoOrdenado> productoOrdenados;
    public ProductoOrdenado getProductoOrdenado(int position){
        return productoOrdenados.get(position);
    }

    public List<ProductoOrdenado> getProductosOrdenados(){
        return productoOrdenados;
    }

    public AdapterProductosOrdenados(Context context){
        this.context=context;
        this.productoOrdenados =new ArrayList<>();
    }

    public AdapterProductosOrdenados(Context context, ArrayList<ProductoOrdenado> productoOrdenados){
        this.context=context;
        this.productoOrdenados =productoOrdenados;
    }

    public void remove(ProductoOrdenado productoOrdenado){
        productoOrdenados.remove(productoOrdenado);
        this.notifyDataSetChanged();
    }

    public void add(ProductoOrdenado productoOrdenado){
        productoOrdenados.add(productoOrdenado);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return productoOrdenados.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        v= layoutInflater.inflate(R.layout.card_producto_ordenado,null);
        ((TextView)v.findViewById(R.id.tvCantProduct)).setText(""+getProductoOrdenado(i).getCantidad());
        ((TextView)v.findViewById(R.id.tvProductOrden)).setText(getProductoOrdenado(i).getProducto());
        if(!getProductoOrdenado(i).getDescripcion().equals("")){
            TextView descripcion=v.findViewById(R.id.tvDescripProduct);
            descripcion.setText(getProductoOrdenado(i).getDescripcion());
            descripcion.setVisibility(View.VISIBLE);
        }
        return v;
    }
}
