package com.example.asadordelchapulin.encargado;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.PerfilFragment;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.SendNotification;
import com.example.asadordelchapulin.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarPlatilloFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarPlatilloFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText nombrePlatillo, precioPlatillo, descripcionPlatillo;
    private ImageView imageView;
    private SendNotification notification=new SendNotification();

    public static Platillo platillo;
    private View view;
    private Switch existencia;

    public EditarPlatilloFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarPlatilloFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarPlatilloFragment newInstance(String param1, String param2) {
        EditarPlatilloFragment fragment = new EditarPlatilloFragment();
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
        view= inflater.inflate(R.layout.fragment_editar_platillo, container, false);
        nombrePlatillo=view.findViewById(R.id.editNombrePlatillo);
        precioPlatillo=view.findViewById(R.id.editPrecioPlatillo);
        existencia=view.findViewById(R.id.existenciaProducto);
        if(platillo.getExistencia()){
            existencia.setChecked(true);
        }
        descripcionPlatillo=view.findViewById(R.id.edtiDescripcionPlatillo);
        nombrePlatillo.setHint(platillo.getNombre());
        precioPlatillo.setHint(platillo.getPrecio()+"");
        descripcionPlatillo.setHint(platillo.getDescripcion());
        imageView=view.findViewById(R.id.imgPlatilloEdit);
        Utils.cargarImagen(getContext(),platillo.getUri(),imageView);

        ((Button)view.findViewById(R.id.btnActualizarPlatillo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nombrePlatillo.getText().toString().equals("") && precioPlatillo.getText().toString().equals("") && descripcionPlatillo.getText().toString().equals("")){
                    Toast.makeText(getContext(), "No hay modificaciones", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    if(!nombrePlatillo.getText().toString().equals("")){
                        platillo.setNombre(nombrePlatillo.getText().toString());
                    }
                    if(!precioPlatillo.getText().toString().equals("")){
                        platillo.setPrecio(Double.parseDouble(precioPlatillo.getText().toString()));
                    }
                    if(!descripcionPlatillo.getText().toString().equals("")){
                        platillo.setDescripcion(descripcionPlatillo.getText().toString());
                    }
                    PlatillosCategoriaFragment.categoria.updateProductos(alertLoader);

                }
            }
        });

        ((Button)view.findViewById(R.id.btnEliminarPlatillo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("¿Está seguro de eliminar el producto?")
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
                                Utils.removeImgCategoria(platillo.getNombre(),getContext());
                                PlatillosCategoriaFragment.categoria.remove(platillo);
                                PlatillosCategoriaFragment.categoria.getDocumentReference().update("productos",PlatillosCategoriaFragment.categoria.getPlatillos()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            alertLoader.dimiss("Producto eliminado");
                                        }
                                        else {
                                            alertLoader.showError("Error al eliminar el producto");
                                        }
                                    }
                                });
                                //Toast.makeText(getContext(),"Platillo eliminado correctamente",Toast.LENGTH_SHORT).show();
                                //Navigation.findNavController(view).popBackStack();
                                //Navigation.findNavController(view).navigate(R.id.nav_platillos_categoria);
                            }
                        });
                AlertDialog titulo=alert.create();
                titulo.setTitle("Eliminar producto");
                titulo.show();
            }
        });

        existencia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //estado.update("activo",true);
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    platillo.setExistencia(true);
                    PlatillosCategoriaFragment.categoria.getDocumentReference().update("productos",PlatillosCategoriaFragment.categoria.getPlatillos()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                alertLoader.dimiss("Producto en existencia");
                                notificar("Producto en existencia",platillo.getNombre()+" en existencia");
                            }
                            else {
                                alertLoader.showError("Error al modificar la existencia del producto");
                            }
                        }
                    });
                }
                else {
                    AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                    alert.setMessage("¿Esta seguro de indicar la no existencia del producto?")
                            .setCancelable(false)
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    existencia.setChecked(true);
                                }
                            })
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                                    platillo.setExistencia(false);
                                    PlatillosCategoriaFragment.categoria.getDocumentReference().update("productos",PlatillosCategoriaFragment.categoria.getPlatillos()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                alertLoader.dimiss("Producto sin existencia");
                                                notificar("Producto fuera de stock",platillo.getNombre()+" sin existencia");
                                            }
                                            else {
                                                alertLoader.showError("Error al indicar la no existencia del producto");
                                            }
                                        }
                                    });
                                }
                            });
                    AlertDialog titulo=alert.create();
                    titulo.setTitle("Modificar existencia");
                    titulo.show();
                }
            }
        });

        return view;
    }

    public void notificar(String titulo, String mensaje){
        FirebaseFirestore.getInstance().collection("Usuarios").whereNotEqualTo("nombre", PerfilFragment.usuario.getNombre()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot dc:task.getResult()){
                        if(!dc.getString("token").equals("")){
                            notification.sendMessage(dc.getString("token"),titulo,mensaje);
                        }
                    }
                }
            }
        });
    }
}