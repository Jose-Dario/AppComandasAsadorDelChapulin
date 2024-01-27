package com.example.asadordelchapulin.encargado;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Mesa;
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterMesas extends BaseAdapter {
    public Context context;
    private List<Mesa> mesas;

    public Mesa getMesa(int position) {
        return mesas.get(position);
    }

    public AdapterMesas(Context context) {
        this.context = context;
        mesas = new ArrayList<>();
    }

    public List<Mesa> getMesas() {
        return mesas;
    }

    public Mesa getMesa(String id) {
        for (Mesa aux : mesas) {
            if (aux.getId().equals(id)) {
                return aux;
            }
        }
        return null;
    }

    public void actualizar(DocumentChange documentChange) {
        for (Mesa aux : mesas) {
            if (aux.getDocumentReference().equals(documentChange.getDocument().getReference())) {
                if (!aux.getEstado().equals(documentChange.getDocument().getString("estado" ))) {
                    this.notifyDataSetChanged();
                }
                aux.setEstado(documentChange.getDocument().getString("estado" ));
                aux.setMesero(documentChange.getDocument().getString("mesero" ));
                aux.setCupo(documentChange.getDocument().getString("cupo" ));
                aux.setClientes((ArrayList<HashMap>) documentChange.getDocument().get("clientes" ));
            }
        }
    }

    public void remove(Mesa mesa) {
        mesas.remove(mesa);
        this.notifyDataSetChanged();
    }

    public void add(Mesa mesa) {
        mesas.add(mesa);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mesas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ResourceAsColor" )
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        v = layoutInflater.inflate(R.layout.card_mesa, null);
        ((TextView) v.findViewById(R.id.idMesa)).setText(getMesa(i).getId());
        ((TextView) v.findViewById(R.id.status)).setText(getMesa(i).getEstado());
        if (getMesa(i).getEstado().equals("disponible" )) {
            v.findViewById(R.id.linear).setBackgroundResource(R.color.disponible);
        }
        if (getMesa(i).getEstado().equals("ocupado" )) {
            v.findViewById(R.id.linear).setBackgroundResource(R.color.ocupado);
        }
        if (getMesa(i).getEstado().equals("reservado" )) {
            v.findViewById(R.id.linear).setBackgroundResource(R.color.reservado);
        }
        if (getMesa(i).getEstado().equals("desactivado" )) {
            v.findViewById(R.id.linear).setBackgroundResource(R.color.desactivado);
        }
        return v;
    }
}
