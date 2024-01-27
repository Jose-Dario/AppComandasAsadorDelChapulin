package com.example.asadordelchapulin.area;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Comanda;
import com.example.asadordelchapulin.ProductoOrdenado;
import com.example.asadordelchapulin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<Comanda> dataList;

    public static TTSManager ttsManager;


    public MyAdapter() {
        this.dataList = new ArrayList<>();
    }

    public void add(Comanda comanda){
        dataList.add(comanda);
        notifyItemChanged(dataList.size()-1);
    }

    public void actualizar(DocumentChange documentChange){
        for(int i=0;i<dataList.size();i++){
            Comanda comanda=dataList.get(i);
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
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void remove(DocumentChange documentChange){
        for (int i=0;i<dataList.size();i++){
            if(dataList.get(i).getDocumentReference().equals(documentChange.getDocument().getReference())){
                notifyItemRemoved(i);
                dataList.remove(i);
                return;
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Infla el diseño del elemento de la lista
        View view = inflater.inflate(R.layout.card_comandas_cocina, parent, false);

        // Retorna una nueva instancia del ViewHolder
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Obtiene el elemento actual en la posición
        Comanda item = dataList.get(position);

        // Establece los datos en el ViewHolder
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Clase ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo;
        //private TextView estado;
        private ImageButton listen,listo;
        private LinearLayout pedidos;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Encuentra la vista dentro del diseño del elemento
            pedidos = itemView.findViewById(R.id.contPedidosCocina);
            titulo=itemView.findViewById(R.id.tituloComandaCocina);
            //estado=itemView.findViewById(R.id.estadoComandaCocina);
            listen=itemView.findViewById(R.id.btnListenComanda);
            listo=itemView.findViewById(R.id.imgButtonListo);
        }

        public void bind(Comanda item) {
            // Establece los datos en la vista
            titulo.setText("Mesa "+item.getMesa()+": "+item.getMesero());
            pedidos.removeAllViews();
            //estado.setText("Estado: "+item.getEstado());
            for (ProductoOrdenado aux:item.getProductoOrdenados()){
                View view1=LayoutInflater.from(itemView.getContext()).inflate(R.layout.card_producto_ordenado_si,null);
                ((TextView)view1.findViewById(R.id.tvCantProduct)).setText(""+aux.getCantidad());
                ((TextView)view1.findViewById(R.id.tvProductOrden)).setText(aux.getProducto());
                TextView descripcion=view1.findViewById(R.id.tvDescripProduct);
                if(!aux.getDescripcion().equals("")){
                    descripcion.setText(aux.getDescripcion());
                    descripcion.setVisibility(View.VISIBLE);
                }else{
                    descripcion.setVisibility(View.GONE);
                }
                pedidos.addView(view1);
            }

            listo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemRemoved(dataList.indexOf(item));
                    dataList.remove(item);
                    ComandasFinalizadasFragment.adapterComandasFinalizadas.add(item);
                    item.getDocumentReference().update("estado","finalizado");
                }
            });

            listen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String texto="Mesa "+item.getMesa()+"... ";
                    for(ProductoOrdenado producto:item.getProductoOrdenados()){
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
        }
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
            if(!productoOrdenado.getProducto().equalsIgnoreCase("del comal")){
                lectura+=productoOrdenado.getProducto()+" ";
            }
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
        if(esEntero(palabras[0])){
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

    private static boolean esEntero(String str) {
        try {
            // Intenta convertir el String a un número entero
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            // La conversión falló
            return false;
        }
    }

}
