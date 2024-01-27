package com.example.asadordelchapulin.encargado;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    private View view;

    private ArrayAdapter arrayAdapter;

    private EditText nombre,correo,contrasenia,rol,confirmarContrasenia;

    private Spinner spinnerRol;

    private AlertLoader alertLoader;


    public AddUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddUserFragment newInstance(String param1, String param2) {
        AddUserFragment fragment = new AddUserFragment();
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
        view= inflater.inflate(R.layout.fragment_add_user, container, false);
        spinnerRol=view.findViewById(R.id.spinner);
        arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, MainEncargado.getRoles());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(arrayAdapter);
        nombre=view.findViewById(R.id.etNombreUsuario);
        correo=view.findViewById(R.id.etCorreoUsuario);
        contrasenia=view.findViewById(R.id.etContraseniaUsuario);
        confirmarContrasenia=view.findViewById(R.id.etConfirmarContraseniaUsuario);
        ((Button)view.findViewById(R.id.btnRegistrarUsuario)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarRegistro()) {
                    alertLoader=new AlertLoader(getActivity(),view);
                    String correoUser, contraseniaUser;
                    correoUser = correo.getText().toString();
                    contraseniaUser = contrasenia.getText().toString();
                    firebaseAuth.createUserWithEmailAndPassword(correoUser,contraseniaUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            try {
                                if(task.isSuccessful()){
                                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                                    if(firebaseUser!=null){
                                        Map item = new HashMap<>();
                                        item.put("nombre", nombre.getText().toString());
                                        item.put("correo",correoUser);
                                        item.put("rol",spinnerRol.getSelectedItem().toString());
                                        item.put("profileReference", firebaseUser.getUid());
                                        item.put("token","");
                                        FirebaseFirestore.getInstance().collection("Usuarios").add(item).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){
                                                    UsuariosFragment.adapterUsuarios.add(new Usuario(item.get("nombre").toString(), item.get("correo").toString(), item.get("rol").toString(),task.getResult(), item.get("profileReference").toString()));
                                                    alertLoader.minimizar("Usuario registrado exitosamente");
                                                }
                                                else {
                                                    alertLoader.showError("Error al registrar el usuario");
                                                }
                                            }
                                        });
                                    }
                                }
                                else {
                                    throw task.getException();
                                }
                            }
                            catch (FirebaseAuthUserCollisionException e){
                                firebaseAuth.fetchSignInMethodsForEmail(correoUser)
                                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                                if (task.isSuccessful()) {
                                                    List<String> providers = task.getResult().getSignInMethods();
                                                    if (providers != null && !providers.isEmpty()) {
                                                        Map item = new HashMap<>();
                                                        item.put("nombre", nombre.getText().toString());
                                                        item.put("correo",correoUser);
                                                        item.put("rol",spinnerRol.getSelectedItem().toString());
                                                        item.put("profileReference", providers.get(0));
                                                        item.put("token","");
                                                        FirebaseFirestore.getInstance().collection("Usuarios").add(item).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if (task.isSuccessful()){
                                                                    UsuariosFragment.adapterUsuarios.add(new Usuario(item.get("nombre").toString(), item.get("correo").toString(), item.get("rol").toString(),task.getResult(), item.get("profileReference").toString()));
                                                                    firebaseAuth.sendPasswordResetEmail(correoUser)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        alertLoader.minimizar("El usuario ya existe, se le envió un correo para cambiar su contraseña");
                                                                                    }
                                                                                    else{
                                                                                        alertLoader.showError(task.getException().toString());
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                                else {
                                                                    alertLoader.showError("Error al registrar el usuario");
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                                else {
                                                    alertLoader.showError(task.getException().toString());
                                                }
                                            }
                                        });
                            } catch (Exception e) {
                                alertLoader.showError(e.toString());
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

    public boolean validarRegistro(){
        if(!nombre.getText().toString().equals("") && !correo.getText().toString().equals("") && !contrasenia.getText().toString().equals("") && !confirmarContrasenia.getText().toString().equals("")){
            if(contrasenia.getText().toString().length()>6){
                if(contrasenia.getText().toString().equals(confirmarContrasenia.getText().toString())){
                    return true;
                }
                else {
                    Toast.makeText(getContext(),"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getContext(),"Ingrese una contraseña válida, mayor a 6 caracteres",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(),"Rellene los campos solicitados",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}