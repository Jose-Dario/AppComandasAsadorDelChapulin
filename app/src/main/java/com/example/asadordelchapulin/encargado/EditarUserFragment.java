package com.example.asadordelchapulin.encargado;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.SendNotification;
import com.example.asadordelchapulin.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private Spinner rolUser;

    public static Usuario usuario;

    private ArrayAdapter arrayAdapter   ;

    public EditarUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarUserFragment newInstance(String param1, String param2) {
        EditarUserFragment fragment = new EditarUserFragment();
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
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_editar_user, container, false);
        ((TextView)view.findViewById(R.id.tvUserName)).setText(usuario.getNombre());
        ((TextView)view.findViewById(R.id.tvEmailUser)).setText(usuario.getCorreo());
        rolUser=view.findViewById(R.id.spinerRolUser);
        arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, MainEncargado.getRoles());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolUser.setAdapter(arrayAdapter);
        rolUser.setSelection(MainEncargado.getRoles().indexOf(usuario.getRol()));
        ImageView profileUser=view.findViewById(R.id.imgProfileUserEdit);
        usuario.cargarProfileUser(profileUser);

        ((Button)view.findViewById(R.id.btnActualizarRol)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rolUser.getSelectedItem().toString().equals(usuario.getRol())){
                    Toast.makeText(getContext(),"Es el rol actual del usuario",Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    usuario.getDocumentReference().update("rol",rolUser.getSelectedItem().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                alertLoader.dimiss("Rol del usuario actualizado");
                                FirebaseFirestore.getInstance().collection("Usuarios").whereEqualTo("correo",usuario.getCorreo()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            if(task.getResult().getDocuments().size()>0){
                                                SendNotification sendNotification=new SendNotification();
                                                sendNotification.sendMessage(task.getResult().getDocuments().get(0).getString("token"),"Rol modificado","El administrador ha modificado tu rol, reiniciar aplicación");
                                            }
                                        }
                                    }
                                });
                            }
                            else {
                                alertLoader.showError(task.getException().toString());
                            }
                        }
                    });
                    usuario.setRol(rolUser.getSelectedItem().toString());
                }
            }
        });

        ((Button)view.findViewById(R.id.btnEliminarUser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("¿Está seguro de eliminar al usuario?")
                        .setCancelable(true)
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                                usuario.getDocumentReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            UsuariosFragment.adapterUsuarios.remove(usuario);
                                            alertLoader.dimiss("Usuario eliminado");
                                        }
                                        else {
                                            alertLoader.showError(task.getException().toString());
                                        }

                                    }
                                });
                            }
                        });
                AlertDialog titulo=alert.create();
                titulo.setTitle("Eliminar Usuario");
                titulo.show();
            }
        });

        return view;
    }
}