package com.example.asadordelchapulin;

import static com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.asadordelchapulin.area.MainArea;
import com.example.asadordelchapulin.encargado.MainEncargado;
import com.example.asadordelchapulin.mesero.MainMesero;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    private FirebaseAuth firebaseAuth;

    public static Usuario usuario;
    private FirebaseFirestore firestore;

    private String correo, contrasenia, rol, nombre;

    private boolean isVisible = false;

    private boolean sesionGuardada;

    private EditText etCorreo, etPassword;
    private ProgressBar load;

    private ImageButton btnImg;

    private LinearLayout login;
    private AlertLoader alertLoader;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Solicitar permiso de notificaciones
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"},
                    REQUEST_PERMISSIONS_ACCESS_FINE_LOCATION);
        }
        // Verificar si se tienen los permisos

        firebaseAuth = FirebaseAuth.getInstance();
        PerfilFragment.firebaseAuth = firebaseAuth;
        firestore = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        nombre = preferences.getString("nombre", "nombre");
        correo = preferences.getString("correo", "correo");
        rol = preferences.getString("rol", "rol");
        contrasenia = preferences.getString("contrasenia", "contrasenia");
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.linearLogin);
        load = findViewById(R.id.loadLogin);
        ((ImageView) findViewById(R.id.imvLogo)).setImageResource(R.drawable.logo);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnImg = findViewById(R.id.ibtnvisible);
        if (preferences.getBoolean("estado", false) == true) {
            sesionGuardada = true;
            iniciarSe();
        } else {
            login.setVisibility(View.VISIBLE);
            load.setVisibility(View.GONE);
        }
    }

    public void iniciarSesion(View view) {
        correo = etCorreo.getText().toString();
        contrasenia = etPassword.getText().toString();
        if (correo == null || contrasenia == null) {
            Toast.makeText(this, "Llene los campos solicitados", Toast.LENGTH_SHORT).show();
        } else {
            alertLoader = new AlertLoader(this);
            iniciarSe();
        }
    }

    public void iniciarSe() {
        firebaseAuth.signInWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    consultarUsuario();
                } else {
                    if (alertLoader != null) {
                        alertLoader.showError("Correo y/o contraseña incorrectos");
                    } else {
                        Toast.makeText(getApplicationContext(), "Correo y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        cerrarSesion();
                        recreate();
                    }
                }
            }
        });
    }

    public void iniciarActivity(String nombre, String correo, String rol) {
        if (rol != null) {
            if (alertLoader != null) {
                alertLoader.finalizarLoader();
            }
            switch (rol) {
                case "Mesero":
                    Intent intent = new Intent(this, MainMesero.class);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("correo", correo);
                    intent.putExtra("rol", rol);
                    startActivity(intent);
                    finish();
                    break;
                case "Encargado":
                    Intent intentEncargado = new Intent(this, MainEncargado.class);
                    intentEncargado.putExtra("nombre", nombre);
                    intentEncargado.putExtra("correo", correo);
                    intentEncargado.putExtra("rol", rol);
                    startActivity(intentEncargado);
                    finish();
                    break;
                case "Cocinero":
                case "Barman":
                case "Comalero":
                    Intent intentCocinero = new Intent(this, MainArea.class);
                    intentCocinero.putExtra("nombre", nombre);
                    intentCocinero.putExtra("correo", correo);
                    intentCocinero.putExtra("rol", rol);
                    startActivity(intentCocinero);
                    finish();
                    break;
            }
        } else {
            Toast.makeText(this, "Error al obtener datos de la BD", Toast.LENGTH_SHORT).show();
        }
    }

    public void recuperarContrasenia(View view) {
        correo = etCorreo.getText().toString();
        if (!correo.equals("")) {
            showAlert();
        } else {
            Toast.makeText(this, "Ingrese el correo electrónico asociado a su cuenta", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlert() {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
        alert.setMessage("Se enviará un link de recuperación a tu correo")
                .setCancelable(true)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertLoader alertLoader1 = new AlertLoader(MainActivity.this);
                        firebaseAuth.sendPasswordResetEmail(correo)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        alertLoader1.minimizar("Revisa tu bandeja de entrada");
                                    } else {
                                        alertLoader1.showError(task.getException().toString());
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        androidx.appcompat.app.AlertDialog titulo = alert.create();
        titulo.setTitle("Recuperar Contraseña");
        titulo.show();
    }

    public void showPassword(View view) {
        isVisible = !isVisible;
        if (isVisible) {
            btnImg.setImageResource(R.drawable.baseline_visibility_24);
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            btnImg.setImageResource(R.drawable.baseline_visibility_off_24);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void consultarUsuario() {
        firestore.collection("Usuarios").whereEqualTo("correo", correo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot queryDocumentSnapshot = task.getResult();
                    if (queryDocumentSnapshot.getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshot.getDocuments().get(0);
                        correo = documentSnapshot.getString("correo");
                        rol = documentSnapshot.getString("rol");
                        nombre = documentSnapshot.getString("nombre");
                        usuario = new Usuario(nombre, correo, rol, documentSnapshot.getReference(), documentSnapshot.getString("profileReference"));
                        PerfilFragment.usuario = usuario;
                        CambiarPasswordFragment.password = contrasenia;
                        iniciarActivity(nombre, correo, rol);
                        guardarSesion(correo, rol, nombre);
                    } else {
                        if (alertLoader != null) {
                            alertLoader.showError("Esta cuenta ha sido eliminada");
                        } else{
                            Toast.makeText(getApplicationContext(), "Esta cuenta ha sido eliminada", Toast.LENGTH_SHORT).show();
                            cerrarSesion();
                            recreate();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void guardarSesion(String correo, String rol, String nombre) {
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("estado", true);
        editor.putString("correo", correo);
        editor.putString("rol", rol);
        editor.putString("nombre", nombre);
        editor.putString("contrasenia", contrasenia);
        editor.commit();
        if (!sesionGuardada) {
            guardarToken();
        }
    }

    public void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("estado", false);
        editor.commit();
    }

    public void guardarToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            guardarToken();
                            return;
                        } else {
                            PerfilFragment.usuario.getDocumentReference().update("token", task.getResult());
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes enviar notificaciones
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Active el permiso de notificaciones, porfavor.")
                        .setCancelable(true)
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog titulo = alert.create();
                titulo.setTitle("Permiso de Notificaciones");
                titulo.show();
            }
        }
    }
}