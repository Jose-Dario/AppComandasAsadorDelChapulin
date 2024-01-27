package com.example.asadordelchapulin;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MesaDetalleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MesaDetalleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GridView gridView;
    private AdapterCliente adapterCliente;

    private View view;

    public static Mesa mesa;



    public MesaDetalleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MesaDetalleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MesaDetalleFragment newInstance(String param1, String param2) {
        MesaDetalleFragment fragment = new MesaDetalleFragment();
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_mesa_detalle, container, false);
        ((TextView)view.findViewById(R.id.tituloMesa)).setText("Mesa "+mesa.getId());
        ((TextView)view.findViewById(R.id.showMeseroMesa)).setText("Mesero: "+mesa.getMesero());
        gridView=view.findViewById(R.id.contenedorClientes);
        ArrayList <String> clientes=new ArrayList<>();
        for(HashMap aux:mesa.getClientes()){
            clientes.add(aux.get("nombre").toString());
        }
        adapterCliente=new AdapterCliente(getContext(),clientes);
        gridView.setAdapter(adapterCliente);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetalleCuentaFragment.cliente=mesa.getClientes().get(position);
                Navigation.findNavController(view).navigate(R.id.nav_detalle_cuenta);
            }
        });

        ((Button)view.findViewById(R.id.btnAddCuenta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_add_cuenta);
            }
        });

        ((Button)view.findViewById(R.id.btnTraspasarMesa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.card_traspasar_mesa, null);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner mesaDestino = dialogView.findViewById(R.id.spinnerTraspaso);
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,MesasFragment.adapterMesas.getMesas());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mesaDestino.setAdapter(adapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);
                builder.setView(dialogView).setPositiveButton("Traspasar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                        Mesa mesaToDestino=(Mesa) mesaDestino.getSelectedItem();
                        if(mesaToDestino.getEstado().equals("disponible")){
                            for(Comanda comanda:ComandasFragment.adapterComandas.getComandas()){
                                if(comanda.getMesa().equals(mesa.getId())){
                                    updateMesaComanda(comanda.getDocumentReference(),mesaToDestino.getId());
                                }
                            }
                            for(Comanda comanda:ComandasFragment.adapterComandas.getEntregadas()){
                                if(comanda.getMesa().equals(mesa.getId())){
                                    updateMesaComanda(comanda.getDocumentReference(),mesaToDestino.getId());
                                }
                            }
                            mesaToDestino.getDocumentReference().update("estado","ocupado");
                            mesaToDestino.getDocumentReference().update("mesero",mesa.getMesero());
                            mesaToDestino.getDocumentReference().update("clientes",mesa.getClientes()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mesa.getDocumentReference().update("estado","disponible");
                                        mesa.getDocumentReference().update("clientes",new ArrayList<>());
                                        alertLoader.dimiss("Mesa traspasada correctamente");
                                    }
                                    else {
                                        alertLoader.showError(task.getException().toString());
                                    }
                                }
                            });
                        }
                        else {
                            alertLoader.showError("La mesa se encuentra ocupada/reservada");
                        }

                    }
                }).setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        ((Button)view.findViewById(R.id.btnDespedirMesa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("¿Está seguro de despedir la mesa?")
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
                                if(mesa.getClientes().size()==0){
                                    mesa.getDocumentReference().update("numAdultos",0);
                                    mesa.getDocumentReference().update("numNinos",0);
                                    mesa.getDocumentReference().update("clientes",new ArrayList<>());
                                    mesa.getDocumentReference().update("estado","disponible").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                alert.dimiss("Mesa despedia");
                                            }
                                            else {
                                                alert.showError(task.getException().toString());
                                            }
                                        }
                                    });
                                }
                                else{
                                    alert.showError("No has saldado todas las cuentas");
                                }
                            }
                        });
                AlertDialog titulo=alert.create();
                titulo.setTitle("Despedir Mesa");
                titulo.show();
            }
        });

        return view;
    }

    public void registrarVenta(AlertLoader alertLoader){
        for (HashMap cliente: mesa.getClientes()){
            HashMap venta=new HashMap();
            ArrayList <Comanda> comandas=new ArrayList<>();
            ArrayList <ProductoOrdenado> productosOrdenadosGuardar=new ArrayList<>();
            for (Comanda aux: ComandasFragment.adapterComandas.getComandas()){
                if(aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())){
                    comandas.add(aux);
                }
            }

            for (Comanda aux: ComandasFragment.adapterComandas.getEntregadas()){
                if(aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())){
                    comandas.add(aux);
                }
            }

            for(Comanda aux:comandas){
                for(ProductoOrdenado productoOrdenado:aux.getProductoOrdenados()){
                    buscarProducto(productoOrdenado,productosOrdenadosGuardar);
                }
            }
            venta.put("total",Double.parseDouble(cliente.get("consumo").toString()));
            venta.put("cliente",cliente.get("nombre").toString());
            venta.put("mesero",mesa.getMesero());
            venta.put("fecha",new Date());
            venta.put("mesa",mesa.getId());
            venta.put("productosOrdenados",productosOrdenadosGuardar);
            venta.put("folio",cliente.get("folio").toString());
            FirebaseFirestore.getInstance().collection("Ventas").add(venta);
        }
        mesa.getDocumentReference().update("numAdultos",0);
        mesa.getDocumentReference().update("numNinos",0);
        mesa.getDocumentReference().update("clientes",new ArrayList<>());
        mesa.getDocumentReference().update("estado","disponible").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    alertLoader.dimiss("Mesa despedia");
                }
                else {
                    alertLoader.showError("Ocurrió un error al conectar con la BD");
                }
            }
        });

    }

    public void updateMesaComanda(DocumentReference documentReference, String mesita){
        documentReference.update("mesa",mesita).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
                else {
                    updateMesaComanda(documentReference,mesita);
                }
            }
        });
    }

    public void buscarProducto(ProductoOrdenado productoOrdenado, ArrayList<ProductoOrdenado> productosOrdenados){
        for(ProductoOrdenado aux:productosOrdenados){
            if(aux.getProducto().equals(productoOrdenado.getProducto()) ){
                aux.setCantidad(aux.getCantidad()+productoOrdenado.getCantidad());
                System.out.println(aux.getCantidad()+"");
                return;
            }
        }
        productosOrdenados.add(new ProductoOrdenado(productoOrdenado.getProducto(),productoOrdenado.getPrecio(),productoOrdenado.getArea(),productoOrdenado.getCantidad(),productoOrdenado.getDescripcion()));
    }
}