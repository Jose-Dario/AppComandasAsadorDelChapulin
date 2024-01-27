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
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterCategorias extends BaseAdapter {
    public Context context;
    private ArrayList<Categoria> categorias;
    private ArrayList<String> nombreCategorias;


    public Categoria getCategoria(int position) {
        return categorias.get(position);
    }

    public Categoria getDelComal(){
        for (Categoria categoria:categorias){
            if(categoria.getNombre().equals("Del comal")){
                return categoria;
            }
        }
        return null;
    }

    public ArrayList<String> getNombresCategoria() {
        nombreCategorias.remove("+ Nuevo producto" );
        nombreCategorias.add("+ Nuevo producto" );
        return nombreCategorias;
    }

    public ArrayList<Categoria> getCategorias(){
        return categorias;

    }

    public AdapterCategorias(Context context) {
        this.context = context;
        categorias = new ArrayList<>();
        nombreCategorias = new ArrayList<>();
    }

    public void remove(Categoria categoria) {
        categorias.remove(categoria);
        this.notifyDataSetChanged();
    }

    public void add(Categoria categoria) {
        categorias.add(categoria);
        nombreCategorias.add(categoria.getNombre());
        this.notifyDataSetChanged();
    }

    public void actualizar(DocumentChange dc) {
        for (Categoria categoria : categorias) {
            if (categoria.getDocumentReference().equals(dc.getDocument().getReference())) {
                ArrayList<Platillo> arrayList = new ArrayList<>();
                ArrayList<String> nombrePlatillos = new ArrayList<>();
                for (Map map : (ArrayList<Map>) dc.getDocument().get("productos" )) {
                    arrayList.add(new Platillo(map.get("nombre" ).toString(), map.get("descripcion" ).toString(), Double.parseDouble(map.get("precio" ).toString()), (boolean) map.get("existencia" ),categoria.getArea()));
                    nombrePlatillos.add(map.get("nombre" ).toString());
                }
                categoria.setPlatillos(arrayList);
                return;
            }
        }
    }

    public void remove(DocumentChange dc) {
        for (Categoria categoria : categorias) {
            if (categoria.getDocumentReference().equals(dc.getDocument().getReference())) {
                categorias.remove(categoria);
                this.notifyDataSetChanged();
                return;
            }
        }
    }

    public Categoria getUltimaCategoria() {
        if (categorias.size() > 0)
            return getCategoria(getCount() - 1);
        return null;
    }

    @Override
    public int getCount() {
        return categorias.size();
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
        v = layoutInflater.inflate(R.layout.card_categoria, null);
        Categoria categoria = getCategoria(i);
        ((TextView) v.findViewById(R.id.tvNombreCategoria)).setText(categoria.getNombre());
        ImageView imageView = v.findViewById(R.id.imgCategoria);
        Utils.cargarImagen(context,categoria.getUri(),imageView);
        //imageView.setImageURI(categoria.getUri());
        //categoria.cargarImg(imageView);
        return v;
    }
}
