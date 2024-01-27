package com.example.asadordelchapulin.area;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.asadordelchapulin.Comanda;
import com.example.asadordelchapulin.ProductoOrdenado;
import com.example.asadordelchapulin.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterComandasFinalizadas extends BaseAdapter {
    public Context context;
    private List<Comanda> comandas;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    public Comanda getComanda(int position){
        return comandas.get(position);
    }

    public static TTSManager ttsManager;

    public AdapterComandasFinalizadas(Context context){
        this.context=context;
        this.comandas =new ArrayList<>();
    }

    public void actualizar(DocumentChange documentChange){
        for (Comanda comanda:comandas){
            if(comanda.getDocumentReference().equals(documentChange.getDocument().getReference())){
                comanda.setMesa(documentChange.getDocument().getString("mesa"));
                comanda.setEstado(documentChange.getDocument().getString("estado"));
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
        ((Button)v.findViewById(R.id.btnVerificarComanda)).setVisibility(View.GONE);
        ((Button)v.findViewById(R.id.btnAccionComanda)).setVisibility(View.GONE);
        ((ImageButton)v.findViewById(R.id.imgButtonListo)).setVisibility(View.GONE);
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
}
