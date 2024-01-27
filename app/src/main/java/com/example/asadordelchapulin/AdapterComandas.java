package com.example.asadordelchapulin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.Map;

public class AdapterComandas extends BaseAdapter implements OnTaskCompleted{
    public Context context;
    private ArrayList<Comanda> comandas;

    private ArrayList<Comanda> entregadas;

    private View vista;
    public Comanda getComanda(int position){
        return comandas.get(position);
    }

    public AdapterComandas(Context context){
        this.context=context;
        this.comandas =new ArrayList<>();
        entregadas=new ArrayList<>();
    }

    public ArrayList<Comanda> getComandas(){
        return comandas;
    }

    public void eliminar(DocumentChange dc){
        for (Comanda aux:comandas){
            if(aux.getDocumentReference().equals(dc.getDocument().getReference())){
                comandas.remove(aux);
                this.notifyDataSetChanged();
                return;
            }
        }
    }

    public void remove(DocumentChange dc){
        for (Comanda aux:comandas){
            if(aux.getDocumentReference().equals(dc.getDocument().getReference())){
                comandas.remove(aux);
                return;
            }
        }
        for (Comanda aux:entregadas){
            if(aux.getDocumentReference().equals(dc.getDocument().getReference())){
                entregadas.remove(aux);
                return;
            }
        }
    }

    public void actualizar(DocumentChange documentChange){
        for (Comanda comanda:comandas){
            if(comanda.getDocumentReference().equals(documentChange.getDocument().getReference())){
                comanda.setMesa(documentChange.getDocument().getString("mesa"));
                comanda.setEstado(documentChange.getDocument().getString("estado"));
                ArrayList<ProductoOrdenado> listaAuxiliarProductos=new ArrayList<>();
                for (Map map:(ArrayList< Map >)documentChange.getDocument().get("productos")){
                    listaAuxiliarProductos.add(new ProductoOrdenado(map.get("producto").toString(),Double.parseDouble(map.get("precio").toString()),
                            map.get("area").toString(),Integer.parseInt(map.get("cantidad").toString()),map.get("descripcion").toString()));
                }
                comanda.setProductoOrdenados(listaAuxiliarProductos);
                if(comanda.getEstado().equals("entregado") || comanda.getEstado().equals("finalizado")){
                    entregadas.add(comanda);
                    remove(comanda);
                } else if (comanda.getEstado().equals("en verificacion")) {
                    comanda.setEstado(documentChange.getDocument().getString("estado"));
                    comanda.setMensaje(documentChange.getDocument().getString("mensaje"));
                }
                notifyDataSetChanged();
                break;
            }
        }
    }

    public ArrayList<Comanda> getEntregadas() {
        return entregadas;
    }

    public void remove(Comanda comanda){
        comandas.remove(comanda);
        this.notifyDataSetChanged();
    }

    public void setView(View vista){
        this.vista=vista;
    }

    public void add(Comanda comanda){
        if(comanda.getEstado().equals("entregado") || comanda.getEstado().equals("finalizado")){
            entregadas.add(comanda);
        }
        else {
            comandas.add(comanda);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comandas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        v= layoutInflater.inflate(R.layout.card_comanda,null);
        Comanda comanda=getComanda(i);
        ((TextView)v.findViewById(R.id.tituloComanda)).setText("Mesa "+comanda.getMesa()+": "+comanda.getMesero());
        ((TextView)v.findViewById(R.id.clienteComanda)).setText("Cliente: "+comanda.getCliente());
        ((TextView)v.findViewById(R.id.estadoComanda)).setText("Estado: "+comanda.getEstado());
        TextView mensaje=v.findViewById(R.id.mensajeComanda);
        if(!comanda.getArea().equals("cocina")){
            ((ImageButton)v.findViewById(R.id.btnPrintComanda)).setVisibility(View.GONE);
        }

        if(comanda.getMensaje().equals("mensaje")){
            mensaje.setVisibility(View.GONE);
        }
        else {
            mensaje.setText("Mensaje: "+comanda.getMensaje());
            mensaje.setVisibility(View.VISIBLE);
        }
        LinearLayout contenedor=v.findViewById(R.id.linearLayoutProductos);
        for (ProductoOrdenado aux:comanda.getProductoOrdenados()){
            View view1=layoutInflater.inflate(R.layout.card_producto_ordenado_si,null);
            ((TextView)view1.findViewById(R.id.tvCantProduct)).setText(""+aux.getCantidad());
            ((TextView)view1.findViewById(R.id.tvProductOrden)).setText(aux.getProducto());
            TextView descripcion=view1.findViewById(R.id.tvDescripProduct);
            if(!aux.getDescripcion().equals("")){
                descripcion.setText(aux.getDescripcion());
                descripcion.setVisibility(View.VISIBLE);
            }else{
                descripcion.setVisibility(View.GONE);
            }
            contenedor.addView(view1);
        }
        Button btnEntregar=v.findViewById(R.id.btnEntregar);
        if(comanda.getArea().equalsIgnoreCase("barra")){
            btnEntregar.setVisibility(View.VISIBLE);
        }
        Button btnComanda=v.findViewById(R.id.btnComanda);
        switch (comanda.getEstado()){
            case "en espera":
                btnComanda.setEnabled(true);
                btnComanda.setText("Modificar");
                break;
            case "en verificacion":
                btnComanda.setEnabled(true);
                btnComanda.setText("Modificar");
                btnComanda.setBackgroundColor(0XFF850000);
                break;
            case "en modificacion":
                btnComanda.setEnabled(false);
                break;
            case "finalizado":
                btnComanda.setEnabled(true);
                btnComanda.setText("Entregar");
                break;
            case "en preparacion":
                btnComanda.setEnabled(false);
                break;
        }
        ((Button)v.findViewById(R.id.btnComanda)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comanda.getEstado().equals("finalizado")){
                    AlertLoader alertLoader=new AlertLoader((Activity) context);
                    comanda.getDocumentReference().update("estado","entregado").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                alertLoader.finalizarLoader();
                            }
                            else{
                                alertLoader.showError(task.getException().toString());
                            }
                        }
                    });
                }
                else{
                    if(comanda.getMensaje().length()>9 && comanda.getMensaje().substring(0,10).equals("reposición")){
                        AlertDialog.Builder alert=new AlertDialog.Builder(context);
                        alert.setMessage("La comanda no puede modificarse, es reposición de platillos")
                                .setCancelable(true)
                                .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog titulo=alert.create();
                        titulo.setTitle("Acción inválida");
                        titulo.show();
                    }
                    else{
                        AlertDialog.Builder alert=new AlertDialog.Builder(context);
                        alert.setMessage("¿Está por modificar la comanda?")
                                .setCancelable(true)
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AlertLoader alertLoader=new AlertLoader((Activity) context);
                                        if(comanda.getEstado().equals("en espera") || comanda.getEstado().equals("en verificacion") || comanda.getEstado().equals("corregida")){
                                            comanda.getDocumentReference().update("estado","en modificacion").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        alertLoader.finalizarLoader();
                                                        GenerarOrdenFragment.mesa=comanda.getMesa();
                                                        GenerarOrdenFragment.comanda=comanda;
                                                        GenerarOrdenFragment.cliente=comanda.getCliente();
                                                        GenerarOrdenFragment.mesero=comanda.getMesero();
                                                        Navigation.findNavController(vista).navigate(R.id.nav_add_orden);
                                                    }
                                                    else {
                                                        alertLoader.showError("Error al intentar modificar la comanda");
                                                    }
                                                }
                                            });

                                        }
                                        else if(comanda.getEstado().equals("finalizado")){
                                            comanda.getDocumentReference().update("estado","entregado");
                                        }
                                    }
                                });
                        AlertDialog titulo=alert.create();
                        titulo.setTitle("Modificar comanda");
                        titulo.show();
                    }
                }
            }
        });

        ((ImageButton)v.findViewById(R.id.btnPrintComanda)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoImprimir = "";
                textoImprimir += Utils.centrar("Mesa " + comanda.getMesa() + " : " + comanda.getMesero()) + "\n\n";
                for (ProductoOrdenado productoOrdenado : comanda.getProductoOrdenados()) {
                    textoImprimir += formatear(productoOrdenado.getCantidad() + " " + productoOrdenado.getProducto()) + "\n";
                    if (!productoOrdenado.getDescripcion().equals("")) {
                        textoImprimir +=  formatear(" => " +productoOrdenado.getDescripcion()) + "\n";
                    }

                }
                textoImprimir += "\n\n\n";
                System.out.println(textoImprimir);
                print(textoImprimir);

            }
        });

        btnEntregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertLoader alertLoader=new AlertLoader((Activity) context);
                comanda.getDocumentReference().update("estado","entregado").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            alertLoader.finalizarLoader();
                        }
                        else{
                            alertLoader.showError(task.getException().toString());
                        }
                    }
                });
            }
        });
        return v;
    }

    public void print(String textoImprimir){
        ImprimirComanda miAsyncTask = new ImprimirComanda(this, textoImprimir);
        miAsyncTask.execute();
    }

    public String formatear(String string){
        String aux="";
        String linea="";
        String [] palabras=string.split(" ");
        for(String palabra:palabras){
            if(linea.length()+1+palabra.length()>24){
                aux+=linea+"\n";
                linea="  ";
                linea+=palabra+" ";
            }
            else{
                linea+=palabra+" ";
            }
        }
        aux+=linea;

        return aux;
    }

    @Override
    public void onTaskCompleted(String result) {
    }
}