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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.asadordelchapulin.area.ComandasCocinaFragment;
import com.example.asadordelchapulin.area.ComandasFinalizadasFragment;
import com.example.asadordelchapulin.area.MainArea;
import com.example.asadordelchapulin.area.TTSManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterComandasCocina extends BaseAdapter {
    public Context context;
    private List<Comanda> comandas;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    private static SendNotification notification=new SendNotification();

    public static TTSManager ttsManager;

    public Comanda getComanda(int position){
        return comandas.get(position);
    }
    public AdapterComandasCocina(Context context){
        this.context = context;
        this.comandas = new ArrayList<>();
    }

    public void remove(DocumentChange documentChange){
        for (Comanda comanda:comandas){
            if(comanda.getDocumentReference().equals(documentChange.getDocument().getReference())){
                comandas.remove(comanda);
                this.notifyDataSetChanged();
                return;
            }
        }
    }

    public void actualizar(DocumentChange documentChange){
        for (Comanda comanda:comandas){
            if(comanda.getDocumentReference().equals(documentChange.getDocument().getReference())){
                comanda.setMesa(documentChange.getDocument().getString("mesa"));
                    comanda.setEstado(documentChange.getDocument().getString("estado"));
                    comanda.setMensaje(documentChange.getDocument().getString("mensaje"));
                ArrayList<ProductoOrdenado> listaAuxiliarProductos=new ArrayList<>();
                for (Map map:(ArrayList< Map >)documentChange.getDocument().get("productos")){
                    listaAuxiliarProductos.add(new ProductoOrdenado(map.get("producto").toString(),Double.parseDouble(map.get("precio").toString()),
                            map.get("area").toString(),Integer.parseInt(map.get("cantidad").toString()),map.get("descripcion").toString()));
                }
                comanda.setProductoOrdenados(listaAuxiliarProductos);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void remove(Comanda comanda){
        comandas.remove(comanda);
        this.notifyDataSetChanged();
    }

    public void add(Comanda comanda){
        comandas.add(comanda);
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

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        v= layoutInflater.inflate(R.layout.card_comandas_cocina,null);
        Comanda comanda=getComanda(i);
        ((TextView)v.findViewById(R.id.tituloComandaCocina)).setText("Mesa "+comanda.getMesa()+": "+comanda.getMesero());
        ((TextView)v.findViewById(R.id.clienteComandaCocina)).setText("Cliente: "+comanda.getCliente());
        ((TextView)v.findViewById(R.id.estadoComandaCocina)).setText("Estado: "+comanda.getEstado());
        TextView mensaje=v.findViewById(R.id.mensajeComandaCocina);
        if(comanda.getMensaje().equals("mensaje")){
            mensaje.setVisibility(View.GONE);
        }
        else {
            mensaje.setText("Mensaje: "+comanda.getMensaje());
            mensaje.setVisibility(View.VISIBLE);
        }
        LinearLayout contenedor=v.findViewById(R.id.contPedidosCocina);
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
        Button btnVerificar=v.findViewById(R.id.btnVerificarComanda);
        Button btnAccion=v.findViewById(R.id.btnAccionComanda);
        ImageButton btnVoice=v.findViewById(R.id.btnListenComanda);
        btnVoice.setImageResource(R.drawable.baseline_record_voice_over_24);
        switch (comanda.getEstado()){
            case "en espera":
                btnAccion.setEnabled(true);
                btnVerificar.setEnabled(true);
                break;
            case "en preparacion":
                btnAccion.setEnabled(true);
                btnVerificar.setEnabled(false);
                btnAccion.setText("Finalizar");
                btnAccion.setBackgroundColor(0XFF850000);
                break;
            case "en verificacion":
                btnAccion.setEnabled(false);
                //btnVerificar.setEnabled(false);
                mensaje.setVisibility(View.VISIBLE);
                break;
            case "en modificacion":
                btnAccion.setEnabled(false);
                //btnVerificar.setEnabled(false);
                break;
        }
        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater infla = ((MainArea)context).getLayoutInflater();
                View dialogView = infla.inflate(R.layout.card_enviar_mensaje, null);
                EditText mensaje=dialogView.findViewById(R.id.inputMensaje);

                // Crea el AlertDialog
                builder.setView(dialogView).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,"Mensaje no enviado",Toast.LENGTH_SHORT).show();
                            }
                        })
                        //.setTitle("AlertDialog con Diseño Personalizado")
                        .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(mensaje.getText().toString().equals("")){
                                    Toast.makeText(context,"Verificación anulada",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    AlertLoader alertLoader=new AlertLoader((Activity) context);
                                    comanda.getDocumentReference().update("estado","en verificacion").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                comanda.getDocumentReference().update("mensaje",mensaje.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            notificar("Verificación de comanda","Comanda del cliente "+comanda.getCliente()+", Mesa "+comanda.getMesa()+", necesita ser verificada",comanda.getMesero());
                                                            alertLoader.minimizar("Verificación enviada");
                                                        }
                                                        else {
                                                            alertLoader.showError("Error al enviar la verificación");
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                alertLoader.showError("Error al enviar la verificación");
                                            }
                                        }
                                    });

                                }
                                // Acciones a realizar cuando se hace clic en Aceptar
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnAccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (comanda.getEstado()){
                    case "en espera":
                    case "corregida":
                        comanda.getDocumentReference().update("estado","en preparacion");
                        btnAccion.setText("Finalizar");
                        btnAccion.setEnabled(true);
                        btnVerificar.setEnabled(false);
                        break;
                    case  "en preparacion":
                        comanda.getDocumentReference().update("estado","finalizado");
                        ComandasFinalizadasFragment.adapterComandasFinalizadas.add(comanda);
                        remove(comanda);
                        notificar("Comanda finalizada","Comanda del cliente "+comanda.getCliente()+", Mesa "+comanda.getMesa()+", ha sido elaborada",comanda.getMesero());
                        break;
                }
            }
        });

        ((ImageButton)v.findViewById(R.id.btnListenComanda)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto="Mesa "+comanda.getMesa()+"... ";
                for(ProductoOrdenado producto:comanda.getProductoOrdenados()){
                    if(producto.getArea().equals("comal")){
                        texto+=getLecturaComal(producto);
                    }
                    else{
                        texto+=getLectura(producto);
                    }
                }
                ttsManager.initQueue(texto);
            }
        });
        return v;
    }

    public String getLecturaComal(ProductoOrdenado productoOrdenado){
        String lectura="";
        if(productoOrdenado.getCantidad()==1){
            lectura+="Un platillo con ";
        }
        else {
            lectura+=productoOrdenado.getCantidad()+" platillos con ";
        }
        if(!productoOrdenado.getDescripcion().equals("")){
            lectura += getLecturaDetalle(productoOrdenado.getDescripcion())+"...";
        }
        else {
            lectura+=productoOrdenado.getProducto();
        }
        return lectura;
    }

    public String getLecturaDetalle(String  descripcion){
        String detalle="";
        for (String antojito:descripcion.split(", ")){
            detalle+=leerAntojito(antojito);
        }
        return detalle;
    }

    public String leerAntojito(String antojito){
        String textoAntojito="";
        String [] palabras=antojito.split(" ");
        if(isPalabraFemino(palabras[1])){
            if(Integer.parseInt(palabras[0])==1){
                palabras[0]="una";
            }
        }
        else{
            if(Integer.parseInt(palabras[0])==1){
                palabras[0]="un";
            }
        }
        for (String palabra:palabras){
            textoAntojito+=palabra+" ";
        }
        return textoAntojito;
    }

    public boolean isPalabraFemino(String palabra){
        if(palabra.charAt(palabra.length()-1)=='a' || palabra.charAt(palabra.length()-1)=='A'){
            return true;
        }
        else if(palabra.equalsIgnoreCase("orden")){
            return true;
        }
        else if(palabra.length()>1){
            if(palabra.substring(palabra.length()-1,palabra.length()).equalsIgnoreCase("as")){
                return true;
            }
        }
        return false;
    }

    @SuppressLint("SuspiciousIndentation")
    public String getLectura(ProductoOrdenado producto){
        String[] palabras = producto.getProducto().split(" ");
        String palabra = palabras[0];
        String aux="";
        if(palabra.charAt(palabra.length()-1)=='a' || isPalabraFemino(palabra)){
            if(producto.getCantidad()==1){
                aux+="una "+producto.getProducto()+" "+producto.getDescripcion()+",";
            }
            else{
                aux+=producto.getCantidad()+" "+producto.getProducto()+" "+producto.getDescripcion()+",";
            }
        }
        else if (palabra.charAt(palabra.length()-1)=='s' || palabra.charAt(palabra.length()-1)=='s'){
            if(producto.getCantidad()==1){
                    if(palabra.charAt(palabra.length()-2)=='a'||palabra.charAt(palabra.length()-2)=='a')
                    {
                        aux+="unas "+producto.getProducto()+" "+producto.getDescripcion()+",";

                    }
                    else
                    aux+="unos "+producto.getProducto()+" "+producto.getDescripcion()+",";
            }
            else{
                aux+=producto.getCantidad()+" "+producto.getProducto()+" "+producto.getDescripcion()+",";
            }
        }
        else{
            if(producto.getCantidad()==1){
                aux+="un "+producto.getProducto()+" "+producto.getDescripcion()+",";
            }
            else{
                aux+=producto.getCantidad()+" "+producto.getProducto()+" "+producto.getDescripcion()+",";
            }
        }
        return aux;
    }

    public void notificar(String titulo, String mensaje, String mesero){
        firebaseFirestore.collection("Usuarios").whereEqualTo("nombre",mesero).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot dc:task.getResult()){
                        notification.sendMessage(dc.getString("token"),titulo,mensaje);
                    }
                }
            }
        });
    }
}