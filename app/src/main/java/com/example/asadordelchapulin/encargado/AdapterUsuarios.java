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
import com.example.asadordelchapulin.AdapterMensajes;
import com.example.asadordelchapulin.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AdapterUsuarios extends BaseAdapter {
    public Context context;
    private List<Usuario> usuarios;


    public Usuario getUsuario(int position){
        return usuarios.get(position);
    }

    public AdapterUsuarios(Context context){
        this.context=context;
        usuarios=new ArrayList<>();
    }

    public void add(Usuario usuario){
        usuarios.add(usuario);
        AdapterMensajes.users.add(usuario);
        this.notifyDataSetChanged();
    }

    public Usuario getUsuario(String nombre){
        for (Usuario usuario:usuarios){
            if(usuario.getNombre().equalsIgnoreCase(nombre)){
                return usuario;
            }
        }
        return null;
    }
    public void remove(Usuario usuario){
        usuarios.remove(usuario);
    }
    @Override
    public int getCount() {
        return usuarios.size();
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
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        v= layoutInflater.inflate(R.layout.card_user,null);
        ((TextView)v.findViewById(R.id.tvNombreUser)).setText(getUsuario(i).getNombre());
        ((TextView)v.findViewById(R.id.tvCorreo)).setText(getUsuario(i).getCorreo());
        ((TextView)v.findViewById(R.id.tvRol)).setText(getUsuario(i).getRol());
        ImageView imgProfile=v.findViewById(R.id.imgProfileUser);
        getUsuario(i).cargarProfileUser(imgProfile);
        return v;
    }
}

