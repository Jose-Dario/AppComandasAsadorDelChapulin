package com.example.asadordelchapulin.encargado;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asadordelchapulin.ComandasFragment;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Comanda;
import com.example.asadordelchapulin.GenerarOrdenFragment;
import com.example.asadordelchapulin.ProductoOrdenado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CorregirComandasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CorregirComandasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static String mesa;

    public static HashMap cliente;

    public static AlertLoader alertLoader;

    private View view;

    private LinearLayout linearLayout;

    public CorregirComandasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CorregirComandasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CorregirComandasFragment newInstance(String param1, String param2) {
        CorregirComandasFragment fragment = new CorregirComandasFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_corregir_comandas, container, false);
        ((TextView)view.findViewById(R.id.tvMesaComandas)).setText("Mesa "+mesa);
        ((TextView)view.findViewById(R.id.tvClienteComandas)).setText(cliente.get("nombre").toString());
        linearLayout=view.findViewById(R.id.lyComandas);
//        FirebaseFirestore.getInstance().collection("Comandas").whereEqualTo("mesa",mesa).whereEqualTo("cliente",cliente).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for (QueryDocumentSnapshot dc:task.getResult()){
//                        Comanda comanda=new Comanda(dc.getString("mesero"),dc.getString("cliente"),dc.getString("mesa"),dc.getString("estado"),(ArrayList<HashMap>) dc.get("productos"),dc.getReference(),dc.getString("area"),dc.getString("mensaje"));
//                        View aux=inflater.inflate(R.layout.card_comanda_edit,null);
//                        ((TextView)aux.findViewById(R.id.comandaStatus)).setText(comanda.getEstado());
//                        LinearLayout productos=aux.findViewById(R.id.productosComanda);
//                        for (ProductoOrdenado productoOrdenado:comanda.getProductoOrdenados()){
//                            View prod=inflater.inflate(R.layout.card_producto_ordenado,null);
//                            ((TextView)prod.findViewById(R.id.tvCantProduct)).setText(productoOrdenado.getCantidad()+"");
//                            ((TextView)prod.findViewById(R.id.tvProductOrden)).setText(productoOrdenado.getProducto());
//                            productos.addView(prod);
//                        }
//                        ((Button)aux.findViewById(R.id.btnEliminarComanda)).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                                alert.setMessage("¿Esta seguro de eliminar la comanda?, la información se borrará del sistema")
//                                        .setCancelable(true)
//                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//
//                                            }
//                                        })
//                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                AlertLoader alertLoader1=new AlertLoader(getActivity());
//                                                comanda.getDocumentReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if(task.isSuccessful()){
//                                                            alertLoader1.finalizarLoader();
//                                                            linearLayout.removeView(aux);
//                                                        }
//                                                        else{
//                                                            alertLoader1.showError(task.getException().toString());
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        });
//                                AlertDialog titulo = alert.create();
//                                titulo.setTitle("Eliminar Comanda");
//                                titulo.show();
//                            }
//                        });
//                        aux.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                GenerarOrdenFragment.modificacion=true;
//                                GenerarOrdenFragment.comanda=comanda;
//                                GenerarOrdenFragment.mesa=comanda.getMesa();
//                                GenerarOrdenFragment.cliente=comanda.getCliente();
//                                Navigation.findNavController(view).navigate(R.id.nav_add_orden);
//                            }
//                        });
//                        linearLayout.addView(aux);
//                    }
//                    alertLoader.finalizarLoader();
//                }
//                else{
//                    alertLoader.showError(task.getException().toString());
//                }
//            }
//        });

        for (Comanda comanda: ComandasFragment.adapterComandas.getComandas()){
            if(comanda.getCliente().equals(cliente.get("nombre").toString()) && comanda.getMesa().equals(mesa)){
                View aux=inflater.inflate(R.layout.card_comanda_edit,null);
                ((TextView)aux.findViewById(R.id.comandaStatus)).setText(comanda.getEstado());
                LinearLayout productos=aux.findViewById(R.id.productosComanda);
                for (ProductoOrdenado productoOrdenado:comanda.getProductoOrdenados()){
                    View prod=inflater.inflate(R.layout.card_producto_ordenado_si,null);
                    ((TextView)prod.findViewById(R.id.tvCantProduct)).setText(productoOrdenado.getCantidad()+"");
                    ((TextView)prod.findViewById(R.id.tvProductOrden)).setText(productoOrdenado.getProducto());
                    productos.addView(prod);
                }
                ((Button)aux.findViewById(R.id.btnEliminarComanda)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("¿Esta seguro de eliminar la comanda?, la información se borrará del sistema")
                                .setCancelable(true)
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AlertLoader alertLoader1=new AlertLoader(getActivity());
                                        comanda.getDocumentReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    alertLoader1.finalizarLoader();
                                                    linearLayout.removeView(aux);
                                                }
                                                else{
                                                    alertLoader1.showError(task.getException().toString());
                                                }
                                            }
                                        });
                                    }
                                });
                        AlertDialog titulo = alert.create();
                        titulo.setTitle("Eliminar Comanda");
                        titulo.show();
                    }
                });
                aux.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GenerarOrdenFragment.modificacion=true;
                        GenerarOrdenFragment.comanda=comanda;
                        GenerarOrdenFragment.mesa=comanda.getMesa();
                        GenerarOrdenFragment.cliente=comanda.getCliente();
                        Navigation.findNavController(view).navigate(R.id.nav_add_orden);
                    }
                });
                linearLayout.addView(aux);
            }

        }

        for (Comanda comanda: ComandasFragment.adapterComandas.getEntregadas()){
            if(comanda.getCliente().equals(cliente.get("nombre").toString()) && comanda.getMesa().equals(mesa)){
                View aux=inflater.inflate(R.layout.card_comanda_edit,null);
                ((TextView)aux.findViewById(R.id.comandaStatus)).setText(comanda.getEstado());
                LinearLayout productos=aux.findViewById(R.id.productosComanda);
                for (ProductoOrdenado productoOrdenado:comanda.getProductoOrdenados()){
                    View prod=inflater.inflate(R.layout.card_producto_ordenado_si,null);
                    ((TextView)prod.findViewById(R.id.tvCantProduct)).setText(productoOrdenado.getCantidad()+"");
                    ((TextView)prod.findViewById(R.id.tvProductOrden)).setText(productoOrdenado.getProducto());
                    productos.addView(prod);
                }
                ((Button)aux.findViewById(R.id.btnEliminarComanda)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("¿Esta seguro de eliminar la comanda?, la información se borrará del sistema")
                                .setCancelable(true)
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AlertLoader alertLoader1=new AlertLoader(getActivity());
                                        comanda.getDocumentReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    alertLoader1.finalizarLoader();
                                                    linearLayout.removeView(aux);
                                                }
                                                else{
                                                    alertLoader1.showError(task.getException().toString());
                                                }
                                            }
                                        });
                                    }
                                });
                        AlertDialog titulo = alert.create();
                        titulo.setTitle("Eliminar Comanda");
                        titulo.show();
                    }
                });
                aux.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GenerarOrdenFragment.modificacion=true;
                        GenerarOrdenFragment.comanda=comanda;
                        GenerarOrdenFragment.mesa= comanda.getMesa();
                        GenerarOrdenFragment.cliente=comanda.getCliente();
                        Navigation.findNavController(view).navigate(R.id.nav_add_orden);
                    }
                });
                linearLayout.addView(aux);
            }
        }
        alertLoader.finalizarLoader();
        return view;
    }
}