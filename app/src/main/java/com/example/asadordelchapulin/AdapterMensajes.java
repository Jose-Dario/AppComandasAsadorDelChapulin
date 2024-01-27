package com.example.asadordelchapulin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class AdapterMensajes extends BaseAdapter {
    public Context context;
    private ArrayList<Mensaje> mensajes;
    public Mensaje getMensaje(int position){
        return mensajes.get(position);
    }
    public static ArrayList<Usuario> users=new ArrayList<>();

    public AdapterMensajes(Context context){
        this.context=context;
        this.mensajes =new ArrayList<>();
    }

    public void remove(DocumentReference documentReference){
        for (Mensaje mensaje:mensajes){
            if(mensaje.getDocumentReference().equals(documentReference)){
                mensajes.remove(mensaje);
                return;
            }
        }
    }

    public ArrayList<Mensaje> getMensajes(){
        return mensajes;
    }


    public void add(Mensaje mensaje){
        mensajes.add(mensaje);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mensajes.size();
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
        Mensaje mensaje=getMensaje(i);
        if(mensaje.getUser().equals(PerfilFragment.usuario.getNombre())){
            v= layoutInflater.inflate(R.layout.card_mensaje_propio,null);
            ((TextView)v.findViewById(R.id.contenidoMensaje)).setText(mensaje.getContenido());
            ((TextView)v.findViewById(R.id.tvHora)).setText(Utils.imprimirFecha(mensaje.getFecha()));
        }
        else {
            v= layoutInflater.inflate(R.layout.card_mensaje,null);
            ImageView profile=v.findViewById(R.id.imgUserMensaje);
            for(Usuario usuario:users){
                if(usuario.getNombre().equalsIgnoreCase(mensaje.getUser())){
                    usuario.cargarProfileUser(profile);
                }
            }
            ((TextView)v.findViewById(R.id.nameUserMensaje)).setText(mensaje.getUser());
            ((TextView)v.findViewById(R.id.contMsj)).setText(mensaje.getContenido());
            ((TextView)v.findViewById(R.id.tvHoraMsj)).setText(Utils.imprimirFecha(mensaje.getFecha()));
        }
        return v;
    }
}