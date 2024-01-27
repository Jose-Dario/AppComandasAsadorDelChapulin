package com.example.asadordelchapulin.mesero;

import static com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.asadordelchapulin.AdapterComandas;
import com.example.asadordelchapulin.AdapterMensajes;
import com.example.asadordelchapulin.Comanda;
import com.example.asadordelchapulin.ComandasFragment;
import com.example.asadordelchapulin.GenerarOrdenFragment;
import com.example.asadordelchapulin.MainActivity;
import com.example.asadordelchapulin.Mensaje;
import com.example.asadordelchapulin.Mesa;
import com.example.asadordelchapulin.MesasFragment;
import com.example.asadordelchapulin.PerfilFragment;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Usuario;
import com.example.asadordelchapulin.encargado.AdapterCategorias;
import com.example.asadordelchapulin.encargado.AdapterMesas;
import com.example.asadordelchapulin.encargado.Categoria;
import com.example.asadordelchapulin.encargado.Platillo;
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

import com.example.asadordelchapulin.databinding.ActivityHomeMeseroBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainMesero extends AppCompatActivity {

    private ActivityHomeMeseroBinding binding;

    private static final int CODIGO_PERMISO = 123;


    public FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    public AdapterComandas adapterComandas;

    public AdapterMesas adapterMesas;
    public AdapterCategorias adapterCategorias;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Solicitar permiso de notificaciones
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_MEDIA_IMAGES"},
                    CODIGO_PERMISO);
        }
        adapterComandas=new AdapterComandas(this);
        ComandasFragment.adapterComandas=adapterComandas;
        adapterMesas=new AdapterMesas(this);
        MesasFragment.adapterMesas=adapterMesas;
        adapterCategorias=new AdapterCategorias(this);
        GenerarOrdenFragment.adaptadorCategorias=adapterCategorias;
        binding = ActivityHomeMeseroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_mesero);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        consultarGeneral();
        consultarMesas();
        consultarComandas();
        consultarCategorias();
        ((FloatingActionButton)findViewById(R.id.btnRestart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    public void consultarComandas(){
        firebaseFirestore.collection("Comandas").
                orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showError("Error al consultar las Comandas");
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                        adapterComandas.add(new Comanda(dc.getDocument().getString("mesero"),dc.getDocument().getString("cliente"),dc.getDocument().getString("mesa"),dc.getDocument().getString("estado"),(ArrayList) dc.getDocument().get("productos"),dc.getDocument().getReference(),dc.getDocument().getString("area"),dc.getDocument().getString("mensaje")));
                                    break;
                                case MODIFIED:
                                    adapterComandas.actualizar(dc);
                                    break;
                                case REMOVED:
                                    adapterComandas.eliminar(dc);
                                    break;
                            }
                        }
                    }
                });
    }

    public void consultarMesas(){
        firebaseFirestore.collection("Mesas").orderBy("idDoc", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showError("Error al consultar la tabla Mesas");
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    adapterMesas.add(new Mesa(dc.getDocument().getString("id"),dc.getDocument().getString("mesero")
                                            ,(ArrayList<HashMap>) dc.getDocument().get("clientes"),dc.getDocument().getString("estado"),dc.getDocument().getReference(),dc.getDocument().getString("cupo")));
                                    break;
                                case MODIFIED:
                                    adapterMesas.actualizar(dc);
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                });
    }

    public void consultarCategorias(){
        firebaseFirestore.collection("Categorias").orderBy("nombre", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showError("Error al consultar la información de Platillos");
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    ArrayList<Platillo> arrayList=new ArrayList<>();
                                    ArrayList<String> nombrePlatillos=new ArrayList<>();
                                    String areaCat=dc.getDocument().getString("area");
                                    for (Map map:(ArrayList<Map>)dc.getDocument().get("productos")){
                                        arrayList.add(new Platillo(map.get("nombre").toString(),map.get("descripcion").toString(),Double.parseDouble(map.get("precio").toString()),(boolean) map.get("existencia"),areaCat));
                                        nombrePlatillos.add(map.get("nombre").toString());
                                    }
                                    adapterCategorias.add(new Categoria(dc.getDocument().getString("nombre"),arrayList, dc.getDocument().getReference(), dc.getDocument().getString("area"),nombrePlatillos,dc.getDocument().getDouble("id").intValue()));
                                    break;
                                case MODIFIED:
                                    adapterCategorias.actualizar(dc);
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                });
    }
    public void consultarGeneral(){
        firebaseFirestore.collection("General").document("barra").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                GenerarOrdenFragment.docBarra=value.getBoolean("estado");
            }
        });
        firebaseFirestore.collection("General").document("comal").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                GenerarOrdenFragment.docComal=value.getBoolean("estado");
            }
        });
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
}