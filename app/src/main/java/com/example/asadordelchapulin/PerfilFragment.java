package com.example.asadordelchapulin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asadordelchapulin.encargado.MainEncargado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static FirebaseAuth firebaseAuth;

    private static final int GALLERY_REQUEST_CODE = 123;

    private Uri selectedImageUri;

    private View view;

    public static Usuario usuario;

    private ImageView profile;

    private EditText etNombre, etCorreo;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
        etNombre = view.findViewById(R.id.etNombre);
        etCorreo = view.findViewById(R.id.etCorreoPerfil);
        etNombre.setHint(usuario.getNombre());
        etCorreo.setHint(usuario.getCorreo());
        ((EditText) view.findViewById(R.id.etRol)).setText(usuario.getRol());
        profile = view.findViewById(R.id.imgPerfilUser);
        usuario.cargarImagen(profile);

        ((Button) view.findViewById(R.id.changePassword)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_cambiar_contrasenia);
            }
        });

        ((Button) view.findViewById(R.id.btnActualizarPerfil)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etNombre.getText().toString().equals("") || !etCorreo.getText().toString().equals("")) {
                    AlertLoader alertLoader = new AlertLoader(getActivity());
                    if (!etNombre.getText().toString().equals("")) {
                        usuario.getDocumentReference().update("nombre", etNombre.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    etNombre.setHint(etNombre.getText().toString());
                                    etNombre.setText("");
                                    etNombre.clearFocus();
                                    if (!etCorreo.getText().toString().equals("")) {
                                        firebaseAuth.getCurrentUser().updateEmail(etCorreo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    usuario.getDocumentReference().update("correo", etCorreo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                usuario.setCorreo(etCorreo.getText().toString());
                                                                etCorreo.setHint(etCorreo.getText().toString());
                                                                etCorreo.setText("");
                                                                etCorreo.clearFocus();
                                                                alertLoader.finalizarLoader();
                                                                Toast.makeText(getContext(), "Información de perfil actualizada", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                alertLoader.showError(task.getException().toString());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    alertLoader.showError(task.getException().toString());
                                                }
                                            }
                                        });
                                    } else {
                                        alertLoader.finalizarLoader();
                                        Toast.makeText(getContext(), "Información de perfil actualizada", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    alertLoader.showError(task.getException().toString());
                                }
                            }
                        });
                    } else if (!etCorreo.getText().toString().equals("")) {
                        firebaseAuth.getCurrentUser().updateEmail(etCorreo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    usuario.getDocumentReference().update("correo",etCorreo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                usuario.setCorreo(etCorreo.getText().toString());
                                                etCorreo.setHint(etCorreo.getText().toString());
                                                etCorreo.setText("");
                                                etCorreo.clearFocus();
                                                alertLoader.finalizarLoader();
                                                Toast.makeText(getContext(), "Información de perfil actualizada", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                alertLoader.showError(task.getException().toString());
                                            }
                                        }
                                    });

                                } else {
                                    alertLoader.showError(task.getException().toString());
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((ImageButton) view.findViewById(R.id.btnImgChangeProfile)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                    }
                }
        );

        ((Button) view.findViewById(R.id.btnCerrarSesion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertLoader alertLoader = new AlertLoader(getActivity());
                usuario.getDocumentReference().update("token", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            alertLoader.finalizarLoader();
                            firebaseAuth.signOut();
                            SharedPreferences preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("estado", false);
                            editor.commit();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            alertLoader.showError("Error al cerrar sesión");
                        }
                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Utils.uploadImageProfile(selectedImageUri, getContext());
            usuario.setImgProfile(Utils.convertUriToBitmap(selectedImageUri, getContext()));
            usuario.cargarImagen(profile);
            if (usuario.getRol().equals("Encargado")) {
                usuario.cargarImagen(MainEncargado.profile);
            }
        }
    }
}
