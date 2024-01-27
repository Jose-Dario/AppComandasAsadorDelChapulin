package com.example.asadordelchapulin.area;

import static com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.asadordelchapulin.AdapterComandasCocina;
import com.example.asadordelchapulin.AdapterMensajes;
import com.example.asadordelchapulin.Comanda;
import com.example.asadordelchapulin.MainActivity;
import com.example.asadordelchapulin.Mensaje;
import com.example.asadordelchapulin.PerfilFragment;
import com.example.asadordelchapulin.ProductoOrdenado;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Usuario;
import com.example.asadordelchapulin.encargado.AdapterCategorias;
import com.example.asadordelchapulin.encargado.Categoria;
import com.example.asadordelchapulin.encargado.Platillo;
import com.example.asadordelchapulin.encargado.PlatillosFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.asadordelchapulin.databinding.ActivityHomeCocinaBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainArea extends AppCompatActivity {

    private ActivityHomeCocinaBinding binding;
    private static final int CODIGO_PERMISO = 123;
    private MediaPlayer entrante,corregida;

    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public AdapterComandasFinalizadas comandasFinalizadas;

    //public AdapterComandasCocina adapterComandasCocina;
    public AdapterCategorias adapterCategorias;

    public static TTSManager ttsManager;

    public MyAdapter adapterCocinaPrueba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Solicitar permiso de notificaciones
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_MEDIA_IMAGES"},
                    CODIGO_PERMISO);
        }
        comandasFinalizadas = new AdapterComandasFinalizadas(this);
        ComandasFinalizadasFragment.adapterComandasFinalizadas = comandasFinalizadas;
        //adapterComandasCocina = new AdapterComandasCocina(this);
        adapterCategorias = new AdapterCategorias(this);
        ttsManager=new TTSManager();
        ttsManager.init(this);
        AdapterComandasCocina.ttsManager=ttsManager;

        //prueba
        adapterCocinaPrueba=new MyAdapter();
        MyAdapter.ttsManager=ttsManager;

        AdapterComandasFinalizadas.ttsManager=ttsManager;
        PlatillosFragment.adapterCategorias = adapterCategorias;
        //ComandasCocinaFragment.adapterComandasCocina = adapterComandasCocina;
        binding = ActivityHomeCocinaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view_cocina);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navig_inicio, R.id.navig_comandas_finalizadas, R.id.navig_platillos, R.id.navig_perfil)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_cocina);
        NavigationUI.setupWithNavController(binding.navViewCocina, navController);
        entrante = MediaPlayer.create(getApplicationContext(), R.raw.entrante);
        corregida =MediaPlayer.create(getApplicationContext(),R.raw.corregida);
        consultarComandas();
        consultarCategorias();
    }

    public void consultarComandas() {
        String area;
        if (PerfilFragment.usuario.getRol().equals("Barman")) {
            area = "barra";
        } else if (PerfilFragment.usuario.getRol().equals("Cocinero")) {
            area = "cocina";
        } else {
            area = "comal";
        }
        firebaseFirestore.collection("Comandas").
                orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showError("Error al consultar la tabla Comandas");
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    if (dc.getDocument().getString("area").equals(area) || dc.getDocument().getString("area").equals("ambos")) {
                                        if (dc.getDocument().getString("estado").equals("entregado") || dc.getDocument().getString("estado").equals("finalizado")) {
                                            comandasFinalizadas.add(new Comanda(dc.getDocument().getString("mesero"), dc.getDocument().getString("cliente"), dc.getDocument().getString("mesa"), dc.getDocument().getString("estado"), (ArrayList) dc.getDocument().get("productos"), dc.getDocument().getReference(), dc.getDocument().getString("area"), dc.getDocument().getString("mensaje")));
                                        } else {
                                            Comanda comanda=new Comanda(dc.getDocument().getString("mesero"), dc.getDocument().getString("cliente"), dc.getDocument().getString("mesa"), dc.getDocument().getString("estado"), (ArrayList) dc.getDocument().get("productos"), dc.getDocument().getReference(), dc.getDocument().getString("area"), dc.getDocument().getString("mensaje"));
                                            //adapterComandasCocina.add(comanda);
                                            adapterCocinaPrueba.add(comanda);
                                            if (ComandasPruebaFragment.lecturaAuto) {
                                                String texto="Comanda entrante... Mesa "+comanda.getMesa()+"... ";
                                                for(ProductoOrdenado producto:comanda.getProductoOrdenados()){
                                                    if(producto.getArea().equals("comal")){
                                                        texto+=getLecturaComal(producto);
                                                    }
                                                    else{
                                                        texto+=getLectura(producto);

                                                    }
                                                }
                                                if(ttsManager.getTextToSpeech().isSpeaking()){
                                                    ttsManager.addQueue(texto);
                                                }
                                                else{
                                                    ttsManager.initQueue(texto);
                                                }
                                            }
                                            else {
                                                entrante.start();
                                            }
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    if (dc.getDocument().getString("area").equals(area)) {
                                        adapterCocinaPrueba.actualizar(dc);
                                        Comanda comandaAuxiliar=new Comanda(dc.getDocument().getString("mesero"), dc.getDocument().getString("cliente"), dc.getDocument().getString("mesa"), dc.getDocument().getString("estado"), (ArrayList) dc.getDocument().get("productos"), dc.getDocument().getReference(), dc.getDocument().getString("area"), dc.getDocument().getString("mensaje"));
                                        if(dc.getDocument().getString("estado").equals("corregida")){
                                            if(ComandasCocinaFragment.lecturaAuto){
                                                String texto="Comanda corregida... Mesa "+comandaAuxiliar.getMesa()+"... ...";
                                                for(ProductoOrdenado producto:comandaAuxiliar.getProductoOrdenados()){
                                                    if(producto.getArea().equals("comal")){
                                                        texto+=getLecturaComal(producto);
                                                    }
                                                    else{
                                                        texto+=getLectura(producto);

                                                    }
                                                }
                                                if(ttsManager.getTextToSpeech().isSpeaking()){
                                                    ttsManager.addQueue(texto);
                                                }
                                                else{
                                                    ttsManager.initQueue(texto);
                                                }
                                            }
                                            else{
                                                String texto="Comanda corregida... de la Mesa "+comandaAuxiliar.getMesa();
                                                if(ttsManager.getTextToSpeech().isSpeaking()){
                                                    ttsManager.addQueue(texto);
                                                }
                                                else{
                                                    ttsManager.initQueue(texto);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    if (dc.getDocument().getString("area").equals(area)) {
                                        adapterCocinaPrueba.remove(dc);
                                    }
                                    break;
                            }
                        }
                    }
                });
    }

    public void consultarCategorias() {
        firebaseFirestore.collection("Categorias").orderBy("nombre", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        ArrayList<Platillo> arrayList = new ArrayList<>();
                        ArrayList<String> nombrePlatillos = new ArrayList<>();
                        String areaCat=documentSnapshot.getString("area");
                        for (Map map : (ArrayList<Map>) documentSnapshot.get("productos")) {
                            arrayList.add(new Platillo(map.get("nombre").toString(), map.get("descripcion").toString(), Double.parseDouble(map.get("precio").toString()), (boolean) map.get("existencia"),areaCat));
                            nombrePlatillos.add(map.get("nombre").toString());
                        }
                        adapterCategorias.add(new Categoria(documentSnapshot.getString("nombre"), arrayList, documentSnapshot.getReference(), documentSnapshot.getString("area"), nombrePlatillos, documentSnapshot.getDouble("id").intValue()));
                    }
                } else {
                    showError("Error al consultar la tabla Platillos");
                }
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_PERMISO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes enviar notificaciones
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Active el permiso de lectura externa, porfavor.")
                        .setCancelable(true)
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog titulo = alert.create();
                titulo.setTitle("Permiso de lectura de imagenes");
                titulo.show();
            }
        }
    }

    public void showError(String error){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(error)
                .setCancelable(false)
                .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reiniciar();
                    }
                });
        AlertDialog titulo = alert.create();
        titulo.setTitle("Reiniciar Aplicación");
        titulo.show();
    }

    public void reiniciar(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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

    public MyAdapter getAdapterCocinaPrueba(){
        return adapterCocinaPrueba;
    }

}