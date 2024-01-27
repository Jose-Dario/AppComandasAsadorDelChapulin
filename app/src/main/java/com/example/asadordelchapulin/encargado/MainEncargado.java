package com.example.asadordelchapulin.encargado;

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
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asadordelchapulin.AdapterComandas;
import com.example.asadordelchapulin.AdapterMensajes;
import com.example.asadordelchapulin.Comanda;
import com.example.asadordelchapulin.ComandasFragment;
import com.example.asadordelchapulin.GenerarOrdenFragment;
import android.Manifest;

import com.example.asadordelchapulin.MainActivity;
import com.example.asadordelchapulin.Mensaje;
import com.example.asadordelchapulin.Mesa;
import com.example.asadordelchapulin.MesasFragment;
import com.example.asadordelchapulin.PerfilFragment;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Usuario;
import com.example.asadordelchapulin.databinding.ActivityHomeEncargadoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainEncargado extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeEncargadoBinding binding;


    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public AdapterUsuarios adapterUsuarios;

    public AdapterCategorias adapterCategorias;

    public AdapterMesas adapterMesas;

    public AdapterComandas adapterComandas;

    public static ImageView profile;
    public static TextView nameU, emailU;
    private static final int CODIGO_PERMISO = 123;

    public static ArrayList<String> listAreas = new ArrayList<>();


    public static ArrayList<String> roles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Solicitar permiso de notificaciones
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_MEDIA_IMAGES"},
                    CODIGO_PERMISO);
        }

        adapterUsuarios = new AdapterUsuarios(this);
        UsuariosFragment.adapterUsuarios = adapterUsuarios;
        adapterCategorias = new AdapterCategorias(this);
        PlatillosFragment.adapterCategorias = adapterCategorias;
        GenerarOrdenFragment.adaptadorCategorias = adapterCategorias;
        adapterMesas = new AdapterMesas(this);
        MesasFragment.adapterMesas = adapterMesas;
        MesaFragment.adapterMesas = adapterMesas;
        adapterComandas = new AdapterComandas(this);
        ComandasFragment.adapterComandas = adapterComandas;
        binding = ActivityHomeEncargadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHomeEncargado.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_perfil, R.id.nav_edit_mesas, R.id.nav_usuarios, R.id.nav_platillos, R.id.nav_configuracion, R.id.nav_ventas, R.id.nav_correcion)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_encargado);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        profile = (ImageView) binding.navView.getHeaderView(0).findViewById(R.id.imgPerfil);
        nameU = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.nombreUser);
        emailU = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.correoUser);
        PerfilFragment.usuario.cargarImagen(profile);
        nameU.setText(PerfilFragment.usuario.getNombre());
        emailU.setText(PerfilFragment.usuario.getCorreo());
        consultarGeneral();
        consultarMesas();
        consultarUsuarios();
        consultarCategorias();
        consultarComandas();
        ((FloatingActionButton)findViewById(R.id.btnRestartEncargado)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home_encargado, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_encargado);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void consultarUsuarios() {
        firebaseFirestore.collection("Usuarios").whereNotEqualTo("correo", PerfilFragment.usuario.getCorreo()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        adapterUsuarios.add(new Usuario(documentSnapshot.getString("nombre"), documentSnapshot.getString("correo"), documentSnapshot.getString("rol"), documentSnapshot.getReference(), documentSnapshot.getString("profileReference")));
                        AdapterMensajes.users.add(adapterUsuarios.getUsuario(adapterUsuarios.getCount() - 1));
                    }
                } else {
                    showError("Ocurrio un error al consultar la tabla Usuarios");
                    Toast.makeText(getApplicationContext(), "Ocurrio un error al realizar la consulta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void consultarCategorias() {
        firebaseFirestore.collection("Categorias").orderBy("nombre", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showError("Error al consultar la tabla Platillos");
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    ArrayList<Platillo> arrayList = new ArrayList<>();
                                    ArrayList<String> nombrePlatillos = new ArrayList<>();
                                    String areaCat=dc.getDocument().getString("area");
                                    for (Map map : (ArrayList<Map>) dc.getDocument().get("productos")) {
                                        arrayList.add(new Platillo(map.get("nombre").toString(), map.get("descripcion").toString(), Double.parseDouble(map.get("precio").toString()), (boolean) map.get("existencia"),areaCat));
                                        nombrePlatillos.add(map.get("nombre").toString());
                                    }
                                    adapterCategorias.add(new Categoria(dc.getDocument().getString("nombre"), arrayList, dc.getDocument().getReference(), dc.getDocument().getString("area"), nombrePlatillos, dc.getDocument().getDouble("id").intValue()));
                                    break;
                                case MODIFIED:
                                    adapterCategorias.actualizar(dc);
                                    break;
                                case REMOVED:
                                    adapterCategorias.remove(dc);
                                    break;
                            }
                        }
                    }
                });
    }

    public void consultarMesas() {
        firebaseFirestore.collection("Mesas").orderBy("idDoc", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showError("Ocurrió un error al consultar la tabla Mesas");
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    adapterMesas.add(new Mesa(dc.getDocument().getString("id"), dc.getDocument().getString("mesero"),
                                            (ArrayList<HashMap>) dc.getDocument().get("clientes"), dc.getDocument().getString("estado"), dc.getDocument().getReference(), dc.getDocument().getString("cupo")));
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

    public void consultarComandas() {
        firebaseFirestore.collection("Comandas").
                orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showError("Ocurrió un error al consultar la tabla Comandas");
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    adapterComandas.add(new Comanda(dc.getDocument().getString("mesero"), dc.getDocument().getString("cliente"), dc.getDocument().getString("mesa"), dc.getDocument().getString("estado"), (ArrayList) dc.getDocument().get("productos"), dc.getDocument().getReference(), dc.getDocument().getString("area"), dc.getDocument().getString("mensaje")));
                                    break;
                                case MODIFIED:
                                    adapterComandas.actualizar(dc);
                                    break;
                                case REMOVED:
                                    adapterComandas.remove(dc);
                                    break;
                            }
                        }
                    }
                });
    }

    public static ArrayList<String> getAreas() {
        if (listAreas.size() > 0) {
            return listAreas;
        } else {
            listAreas.add("barra");
            listAreas.add("cocina");
            listAreas.add("comal");
            return listAreas;
        }
    }


    public static ArrayList<String> getRoles() {
        if (roles.size() > 0) {
            return roles;
        } else {
            roles.add("Encargado");
            roles.add("Mesero");
            roles.add("Barman");
            roles.add("Cocinero");
            roles.add("Comalero");
            return roles;
        }
    }

    public void consultarGeneral(){
        firebaseFirestore.collection("General").document("barra").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                GenerarOrdenFragment.docBarra=value.getBoolean("estado");
                ConfiguracionFragment.docBarra=value.getReference();
            }
        });
        firebaseFirestore.collection("General").document("comal").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                GenerarOrdenFragment.docComal=value.getBoolean("estado");
                ConfiguracionFragment.docComal=value.getReference();
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