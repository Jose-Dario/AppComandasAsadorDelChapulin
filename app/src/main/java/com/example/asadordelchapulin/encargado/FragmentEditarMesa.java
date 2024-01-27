package com.example.asadordelchapulin.encargado;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Mesa;
import com.example.asadordelchapulin.MesasFragment;
import com.example.asadordelchapulin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentEditarMesa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEditarMesa extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    public static Mesa mesa;

    private EditText cupo;

    private Button activar;

    public FragmentEditarMesa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentEditarMesa.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentEditarMesa newInstance(String param1, String param2) {
        FragmentEditarMesa fragment = new FragmentEditarMesa();
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
        view=inflater.inflate(R.layout.fragment_editar_mesa, container, false);
        ((TextView)view.findViewById(R.id.tvMesaTitle)).setText("Mesa "+mesa.getId());
        cupo=view.findViewById(R.id.etChangeCupo);
        cupo.setHint(mesa.getCupo());
        ((Button)view.findViewById(R.id.btnActualizarMesa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cupo.getText().toString().equals("")){
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    mesa.getDocumentReference().update("cupo",cupo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                alertLoader.dimiss("Información actualizada");
                            }
                            else {
                                alertLoader.showError("Ocurrió un error al actualizar la información");
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(),"El campo se encuentra vacío",Toast.LENGTH_SHORT).show();
                }
            }
        });

        activar=view.findViewById(R.id.btnDesactivar);
        if(mesa.getEstado().equals("desactivado")){
            activar.setText("Activar");
        }
        activar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mesa.getEstado().equals("desactivado")){
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    mesa.getDocumentReference().update("estado","disponible").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                alertLoader.dimiss("Mesa "+mesa.getId()+" activada");
                            }
                            else {
                                alertLoader.showError("Ocurrió un error al habilitar la mesa");
                            }
                        }
                    });
                }
                else{
                    if(mesa.getEstado().equals("disponible")){
                        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                        alert.setMessage("¿Está seguro de desactivar la mesa "+mesa.getId()+"?")
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
                                        mesa.getDocumentReference().update("estado","desactivado").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    alertLoader.dimiss("Mesa "+mesa.getId()+" desactivada");
                                                }
                                                else {
                                                    alertLoader.showError("Ocurrió un error al modificar el estado de la mesa");
                                                }
                                            }
                                        });
                                    }
                                });
                        AlertDialog titulo=alert.create();
                        titulo.setTitle("Desactivar Mesa");
                        titulo.show();

                    }
                    else {
                        Toast.makeText(getContext(),"La mesa se encuentra ocupada/reservada",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ((Button)view.findViewById(R.id.btnEliminarMesa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("¿Está seguro de eliminar la mesa "+mesa.getId()+"?")
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
                                mesa.getDocumentReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            MesasFragment.adapterMesas.remove(mesa);
                                            alertLoader.dimiss("Mesa "+mesa.getId()+" eliminada correctamente");
                                        }
                                        else {
                                            alertLoader.showError("Error al eliminar la mesa");
                                        }
                                    }
                                });
                            }
                        });
                AlertDialog titulo=alert.create();
                titulo.setTitle("Eliminar Mesa");
                titulo.show();
            }
        });

        return view;
    }
}