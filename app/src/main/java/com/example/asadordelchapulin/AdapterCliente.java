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

public class AdapterCliente extends BaseAdapter {
    public Context context;
    private List<String> clientes;


    public String getCliente(int position){
        return clientes.get(position);
    }

    public AdapterCliente(Context context, ArrayList<String> clientes){
        this.context=context;
        this.clientes=clientes;
    }

    public void remove(String cliente){
        clientes.remove(cliente);
        this.notifyDataSetChanged();
    }

    public void add(String cliente){
        clientes.add(cliente);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return clientes.size();
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
        v= layoutInflater.inflate(R.layout.card_cliente,null);
        ((TextView)v.findViewById(R.id.tvNombreCliente)).setText(getCliente(i));
        return v;
    }
}
