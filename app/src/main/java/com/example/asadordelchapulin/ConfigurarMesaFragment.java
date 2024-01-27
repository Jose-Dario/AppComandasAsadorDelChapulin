package com.example.asadordelchapulin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigurarMesaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigurarMesaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private EditText nombre;

    private Button activar,reservar,cancelarReservacion;



    public static Mesa mesa;

    public ConfigurarMesaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigurarMesaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigurarMesaFragment newInstance(String param1, String param2) {
        ConfigurarMesaFragment fragment = new ConfigurarMesaFragment();
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
        view= inflater.inflate(R.layout.fragment_configurar_mesa, container, false);
        ((TextView)view.findViewById(R.id.tvMesaTitleConfiguracion)).setText("Mesa "+mesa.getId());
        nombre=view.findViewById(R.id.etNombreCliente);
        activar=view.findViewById(R.id.btnActivarMesa);
        reservar=view.findViewById(R.id.btnReservarMesa);
        cancelarReservacion=view.findViewById(R.id.btnCancelarReservacion);

        if(mesa.getEstado().equals("reservado")){
            reservar.setVisibility(View.GONE);
            cancelarReservacion.setVisibility(View.VISIBLE);
            nombre.setHint(mesa.getClientes().get(0).get("nombre").toString());
        }
        activar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mesa.getEstado().equals("reservado")){
                    activarReservacion();
                }
                else {
                    actualizarMesa("ocupado");
                }
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarMesa("reservado");
            }
        });

        cancelarReservacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("¿Está seguro de cancelar la reservación?")
                        .setCancelable(true)
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertLoader alert=new AlertLoader(getActivity(),view);
                                mesa.getDocumentReference().update("clientes",new ArrayList<>());
                                mesa.getDocumentReference().update("mesero","");
                                mesa.getDocumentReference().update("estado","disponible").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            alert.dimiss("Reservación cancelada");
                                            //Navigation.findNavController(view).popBackStack();
                                        }
                                        else {
                                            alert.showError("Error, Intentelo de nuevo");
                                        }
                                    }
                                });
                            }
                        });
                AlertDialog titulo=alert.create();
                titulo.setTitle("Cancelar reservación");
                titulo.show();
            }
        });

        return view;
    }

    public void actualizarMesa(String estado){
        AlertLoader alert=new AlertLoader(getActivity(),view);
        if(!nombre.getText().toString().equals("")){
            mesa.addCliente(nombre.getText().toString());
            mesa.getDocumentReference().update("mesero",PerfilFragment.usuario.getNombre());
            mesa.getDocumentReference().update("estado",estado).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(estado.equals("ocupado")){
                            alert.dimiss("Mesa activada");
                            MesaDetalleFragment.mesa=mesa;
                            Navigation.findNavController(view).navigate(R.id.nav_detalle_mesa);
                        }
                        else{
                            alert.dimiss("Mesa reservada");
                        }
                    }
                    else {
                        alert.showError("Error, Intentelo de nuevo");
                    }
                }
            });

        }
        else {
            Toast.makeText(getContext(),"Campos vacíos",Toast.LENGTH_SHORT).show();
        }
    }




    public void activarReservacion(){
        AlertLoader alert=new AlertLoader(getActivity(),view);
        if(!nombre.getText().toString().equals("")){
            HashMap cliente=new HashMap();
            cliente.put("nombre",nombre.getText().toString());
            cliente.put("consumo",0);
            cliente.put("folio","");
            ArrayList<HashMap> clientes=new ArrayList<>();
            clientes.add(cliente);
            mesa.getDocumentReference().update("clientes",clientes);
        }
        mesa.getDocumentReference().update("estado","ocupado").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    alert.finalizarLoader();
                    Toast.makeText(getContext(),"Mesa activada",Toast.LENGTH_SHORT).show();
                    MesaDetalleFragment.mesa=mesa;
                    Navigation.findNavController(view).popBackStack();
                    Navigation.findNavController(view).navigate(R.id.nav_detalle_mesa);
                }
                else {
                    alert.showError("Error al activar la mesa");
                }
            }
        });

    }

}