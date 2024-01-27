package com.example.asadordelchapulin.encargado;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;


import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdapterPlatillos extends BaseAdapter {
    public Context context;
    private List<Platillo> platillos;


    public Platillo getPlatillo(int position) {
        return platillos.get(position);
    }

    public AdapterPlatillos(Context context, ArrayList<Platillo> platillos) {
        this.context = context;
        this.platillos = platillos;
    }

    public void add(Platillo platillo) {
        platillos.add(platillo);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return platillos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        v = layoutInflater.inflate(R.layout.card_platillo, null);
        Platillo platillo = this.getPlatillo(i);
        ((TextView) v.findViewById(R.id.tvNombreCategoria)).setText(platillo.getNombre());
        ((TextView) v.findViewById(R.id.tvPrecioPlatillo)).setText("$ " + platillo.getPrecio() + " " );
        ImageView imgPlatillo = v.findViewById(R.id.imgPlatillo);
        Utils.cargarImagen(context,platillo.getUri(),imgPlatillo);
        return v;
    }
}
